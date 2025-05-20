package com.ssafy.triplog.security.jwt;

import com.ssafy.triplog.security.dto.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;

/**
 * 사용자 자원 소유권 검사를 위한 필터
 * 특정 URL 패턴에 대해 요청 시 JWT 토큰의 사용자 ID와 URL 경로의 사용자 ID를 비교하여
 * 자신의 리소스에만 접근할 수 있도록 제한하는 보안 필터
 */
public class UserResourceOwnershipFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil; // JWT 관련 유틸리티

    // URL 패턴과 해당 패턴에서 사용자 ID를 추출하기 위한 매핑 정보
    private final Map<Pattern, String> protectedUrlPatterns;

    /**
     * 생성자 - 필터 초기화 및 보호할 URL 패턴 등록
     * @param jwtUtil JWT 토큰 처리를 위한 유틸리티 객체
     */
    public UserResourceOwnershipFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        this.protectedUrlPatterns = new HashMap<>();

        // 보호할 URL 패턴 등록
        // 정규식 패턴: /triplog/api/경로/(숫자ID) 형태의 URL을 매칭
        // (\\d+) 부분이 사용자 ID를 추출하는 그룹

        // 특정 사용자의 여행 계획 목록 접근 보호
        protectedUrlPatterns.put(Pattern.compile("/triplog/api/myplans/user/(\\d+).*"), "userNo");

        // 특정 사용자의 프로필 정보 접근 보호
        protectedUrlPatterns.put(Pattern.compile("/triplog/api/users/(\\d+).*"), "userNo");

        // 특정 사용자가 작성한 리뷰 목록 접근 보호
        protectedUrlPatterns.put(Pattern.compile("/triplog/api/review/user/(\\d+).*"), "userNo");

        // 필요에 따라 추가 보호 대상 URL 패턴 등록 가능
        // protectedUrlPatterns.put(Pattern.compile("패턴"), "파라미터명");
    }

    /**
     * HTTP 요청에 대한 필터 처리를 구현
     * 요청 경로가 보호 대상 URL 패턴과 일치하는 경우, 인증된 사용자 ID와 요청의 사용자 ID를 비교
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param filterChain 필터 체인 (다음 필터로 요청을 전달하기 위한 객체)
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // SecurityContext에서 현재 인증 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증된 사용자이고, CustomUserDetails 타입인 경우에만 검사 진행
        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof CustomUserDetails) {

            // 인증된 사용자의 세부 정보 추출
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long authenticatedUserNo = userDetails.getUserNo(); // 현재 로그인한 사용자의 ID

            // 현재 요청 URL 경로 가져오기
            String requestPath = request.getRequestURI();

            // 등록된 보호 URL 패턴과 현재 요청 경로 비교
            for (Map.Entry<Pattern, String> entry : protectedUrlPatterns.entrySet()) {
                Pattern pattern = entry.getKey();

                // 현재 요청 경로가 보호 패턴과 일치하는지 확인
                Matcher matcher = pattern.matcher(requestPath);
                if (matcher.matches()) {
                    // URL에서 사용자 ID 추출 (첫 번째 캡처 그룹)
                    Long resourceOwnerId = Long.parseLong(matcher.group(1));

                    // 관리자 권한을 가진 경우 모든 리소스에 접근 허용 (예외 처리)
                    if (userDetails.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                        break; // 관리자는 검사를 통과하고 다음 필터로 진행
                    }

                    // 일반 사용자인 경우, 자신의 리소스에만 접근 가능하도록 ID 비교
                    if (!authenticatedUserNo.equals(resourceOwnerId)) {
                        // ID가 일치하지 않으면 접근 거부 (403 Forbidden)
                        response.setStatus(HttpStatus.FORBIDDEN.value());
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\":\"접근 권한이 없습니다. 본인의 데이터만 조회할 수 있습니다.\"}");
                        return; // 필터 체인 중단 (요청 처리 종료)
                    }
                    break; // 패턴 일치 확인 후 반복 종료
                }
            }
        }

        // 접근 검사를 통과했거나 보호 대상이 아닌 URL인 경우 다음 필터로 진행
        filterChain.doFilter(request, response);
    }
}