package com.ssafy.triplog.security.jwt;

import com.ssafy.triplog.security.dto.CustomUserDetails;
import com.ssafy.triplog.user.dto.UserDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

// 모든 HTTP 요청에 대해 JWT 토큰을 검증하는 필터
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil; // JWT 유틸리티

    // 필터링에서 제외할 경로 패턴 목록 (인증이 필요 없는 경로)
    private final List<String> excludedPaths = Arrays.asList(
            "/api/users/signup",
            "/api/users/login",
            "/api/users/find-email",
            "/api/users/find-password",
            "/swagger-ui",
            "/swagger-ui.html",
            "/v3/api-docs",
            "/api-docs",
            "/api/users/check-email",
            "/api/users/check-nickname",
            "/api/attraction",
            "/api/review",

            //  정적 파일 경로 추가
            "/uploads",
            "/triplog/uploads",
            "/static",
            "/css",
            "/js",
            "/images",

            // 엘라스틱 서치 - context path 고려하여 수정
            "/api/elastic"
    );

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // 필터 적용 여부 결정 - excludedPaths에 포함된 경로는 필터링하지 않음
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // getRequestURI()를 사용하여 context path가 포함된 전체 URI 가져오기
        String fullUri = request.getRequestURI();

        // context path 추출
        String contextPath = request.getContextPath();

        // context path를 제거한 실제 서블릿 경로 추출
        String servletPath = fullUri;
        if (contextPath != null && !contextPath.isEmpty() && fullUri.startsWith(contextPath)) {
            servletPath = fullUri.substring(contextPath.length());
        }

        System.out.println("[DEBUG] Full URI: " + fullUri);
        System.out.println("[DEBUG] Context Path: " + contextPath);
        System.out.println("[DEBUG] Servlet Path: " + servletPath);

        // 제외된 경로에 대해서는 필터링하지 않음
        for (String excludedPath : excludedPaths) {
            if (servletPath.startsWith(excludedPath)) {
                System.out.println("[DEBUG] 경로 제외됨: " + servletPath + " (매칭 패턴: " + excludedPath + ")");
                return true;
            }
        }

        // 정적 파일 확장자 체크 추가
        if (isStaticResource(servletPath)) {
            System.out.println("[DEBUG] 정적 리소스로 제외됨: " + servletPath);
            return true;
        }

        // 홈 경로는 필터링하지 않음
        if (servletPath.equals("/") || servletPath.isEmpty()) {
            System.out.println("[DEBUG] 홈 경로로 제외됨: " + servletPath);
            return true;
        }

        System.out.println("[DEBUG] JWT 필터 적용됨: " + servletPath);
        return false;
    }

    // 정적 파일 확장자 체크 메서드 추가
    private boolean isStaticResource(String path) {
        String[] staticExtensions = {".png", ".jpg", ".jpeg", ".gif", ".css", ".js", ".ico", ".svg", ".webp"};
        String lowerPath = path.toLowerCase();

        for (String extension : staticExtensions) {
            if (lowerPath.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    // 실제 필터 로직 구현
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("[DEBUG] JWT 필터 실행 중 - URI: " + request.getRequestURI());

        // 요청 헤더에서 Authorization 값 추출
        String authorization = request.getHeader("Authorization");

        // Authorization 헤더 검증
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            // 인증 정보가 없으면 요청 거부 (401 Unauthorized)
            System.out.println("[DEBUG] Authorization 헤더 없음 또는 잘못됨");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"인증이 필요합니다. 로그인 후 이용해주세요.\"}");
            return;
        }

        // "Bearer " 제거 후 토큰 추출
        String token = authorization.split(" ")[1];

        // 토큰 만료 여부 검증
        if (jwtUtil.isExpired(token)) {
            // 토큰이 만료되었으면 요청 거부 (401 Unauthorized)
            System.out.println("[DEBUG] JWT 토큰 만료됨");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"인증 토큰이 만료되었습니다. 다시 로그인해주세요.\"}");
            return;
        }

        // 토큰에서 사용자 정보 추출
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);
        Long userNo = jwtUtil.getUserNo(token);
        String nickname = jwtUtil.getNickname(token);

        System.out.println("[DEBUG] JWT 토큰에서 추출한 사용자 정보 - UserNo: " + userNo + ", Email: " + username);

        // 임시 UserDto 객체 생성 (DB 조회 결과가 아니라 토큰에서 추출한 정보만 담음)
        UserDto userDto = new UserDto();
        userDto.setEmail(username);
        userDto.setRole(role);
        userDto.setNo(userNo);
        userDto.setNickname(nickname);

        // CustomUserDetails에 사용자 정보 삽입
        CustomUserDetails customUserDetails = new CustomUserDetails(userDto);

        // Authentication 객체 생성 (인증된 사용자 정보)
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                customUserDetails, null, customUserDetails.getAuthorities());

        // SecurityContext에 인증 정보 설정 (현재 요청에 대한 인증 정보 저장)
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 필터 체인 계속 실행 (다음 필터로 요청 전달)
        filterChain.doFilter(request, response);
    }
}