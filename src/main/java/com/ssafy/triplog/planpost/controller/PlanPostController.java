package com.ssafy.triplog.planpost.controller;

import com.ssafy.triplog.myplan.service.MyPlanService;
import com.ssafy.triplog.planpost.dto.PlanPostRequest;
import com.ssafy.triplog.planpost.dto.PlanPostResponse;
import com.ssafy.triplog.planpost.service.PlanPostService;
import com.ssafy.triplog.security.dto.CustomUserDetails;
import com.ssafy.triplog.security.util.AuthenticationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Tag(name = "Plan_Post_Controller", description = "여행 계획 게시글 관련 API")
@RestController
@RequestMapping("/api/planposts")
@RequiredArgsConstructor
@Slf4j
public class PlanPostController {

    private final PlanPostService planPostService;
    private final MyPlanService myPlanService;
    private final AuthenticationUtil authenticationUtil;

    // ===== MyPlan을 활용한 게시글 생성 =====

    @Operation(summary = "개인 여행 계획으로부터 게시글 생성",
            description = "기존 개인 여행 계획(MyPlan)을 기반으로 공개 게시글을 생성합니다.")
    @PostMapping("/from-myplan/{myPlanNo}")
    public ResponseEntity<Long> createPlanPostFromMyPlan(
            @Parameter(description = "개인 여행 계획 번호") @PathVariable Long myPlanNo,
            @Parameter(description = "게시글 추가 정보") @RequestBody PlanPostFromMyPlanRequest request) {

        // JWT에서 현재 사용자 정보 추출
        Long userNo = authenticationUtil.getCurrentUserNo();
        String userNickname = authenticationUtil.getCurrentUserNickname();

        log.info("MyPlan으로부터 게시글 생성 - myPlanNo: {}, userNo: {}, 제목: {}",
                myPlanNo, userNo, request.getTitle());

        // MyPlan 소유권 확인
        if (!myPlanService.isPlanOwner(myPlanNo, userNo)) {
            log.warn("MyPlan 접근 권한 없음 - myPlanNo: {}, userNo: {}", myPlanNo, userNo);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Long postNo = planPostService.createPlanPostFromMyPlan(myPlanNo, userNo, userNickname, request);

        log.info("MyPlan으로부터 게시글 생성 완료 - postNo: {}", postNo);
        return ResponseEntity.status(HttpStatus.CREATED).body(postNo);
    }

    @Operation(summary = "내 개인 계획 목록 조회 (게시글 생성용)",
            description = "현재 사용자의 개인 여행 계획 목록을 조회합니다. (게시글로 만들기 위한 용도)")
    @GetMapping("/my-plans-for-post")
    public ResponseEntity<?> getMyPlansForPost(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long userNo = authenticationUtil.getCurrentUserNo();

        log.info("게시글 생성용 개인 계획 목록 조회 - userNo: {}", userNo);

        return ResponseEntity.ok(myPlanService.getMyPlansByUser(userNo, page, size));
    }

    // ===== 일반 게시글 CRUD =====

    @Operation(summary = "여행 계획 게시글 직접 등록",
            description = "새로운 여행 계획 게시글을 직접 등록합니다.")
    @PostMapping
    public ResponseEntity<Long> createPlanPost(
            @Valid @RequestBody PlanPostRequest request) {

        // JWT에서 현재 사용자 정보 추출
        Long userNo = authenticationUtil.getCurrentUserNo();
        String userNickname = authenticationUtil.getCurrentUserNickname();

        log.info("게시글 직접 등록 - 사용자: {}, 제목: {}", userNickname, request.getTitle());

        // 요청에 사용자 정보 설정
        request.setUserNo(userNo);
        request.setUserNickname(userNickname);

        Long postNo = planPostService.createPlanPost(request);

        log.info("게시글 직접 등록 완료 - postNo: {}", postNo);
        return ResponseEntity.status(HttpStatus.CREATED).body(postNo);
    }

    @Operation(summary = "여행 계획 게시글 단건 조회",
            description = "특정 여행 계획 게시글의 상세 정보를 조회합니다.")
    @GetMapping("/{postNo}")
    public ResponseEntity<PlanPostResponse> getPlanPost(@PathVariable Long postNo) {
        log.info("게시글 조회 요청 - postNo: {}", postNo);

        PlanPostResponse response = planPostService.getPlanPostById(postNo);

        // 조회수 증가
        planPostService.increaseViewCount(postNo);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "여행 계획 게시글 수정",
            description = "기존 여행 계획 게시글을 수정합니다.")
    @PutMapping("/{postNo}")
    public ResponseEntity<Long> updatePlanPost(
            @PathVariable Long postNo,
            @Valid @RequestBody PlanPostRequest request) {

        // JWT에서 현재 사용자 정보 추출
        Long userNo = authenticationUtil.getCurrentUserNo();
        String userNickname = authenticationUtil.getCurrentUserNickname();

        log.info("게시글 수정 요청 - postNo: {}, 사용자: {}", postNo, userNickname);

        // 권한 확인
        if (!planPostService.isPostOwner(postNo, userNo)) {
            log.warn("게시글 수정 권한 없음 - postNo: {}, userNo: {}", postNo, userNo);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        request.setNo(postNo);
        request.setUserNo(userNo);
        Long updatedPostNo = planPostService.updatePlanPost(request);

        log.info("게시글 수정 완료 - postNo: {}", updatedPostNo);
        return ResponseEntity.ok(updatedPostNo);
    }

    @Operation(summary = "여행 계획 게시글 삭제",
            description = "선택한 여행 계획 게시글을 삭제합니다.")
    @DeleteMapping("/{postNo}")
    public ResponseEntity<Void> deletePlanPost(@PathVariable Long postNo) {

        // JWT에서 현재 사용자 정보 추출
        Long userNo = authenticationUtil.getCurrentUserNo();
        String userNickname = authenticationUtil.getCurrentUserNickname();

        log.info("게시글 삭제 요청 - postNo: {}, 사용자: {}", postNo, userNickname);

        // 권한 확인
        if (!planPostService.isPostOwner(postNo, userNo)) {
            log.warn("게시글 삭제 권한 없음 - postNo: {}, userNo: {}", postNo, userNo);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        planPostService.deletePlanPost(postNo);

        log.info("게시글 삭제 완료 - postNo: {}", postNo);
        return ResponseEntity.noContent().build();
    }

    // ===== 게시글 목록 조회 및 검색 =====

    @Operation(summary = "여행 계획 게시글 목록 조회",
            description = "게시글을 좋아요 수, 포크 수, 조회 수 기준으로 정렬하여 조회합니다.")
    @GetMapping
    public ResponseEntity<List<PlanPostResponse>> getPlanPostList(
            @Parameter(description = "정렬 기준 (liked_count, fork_count, view_count, created_at)")
            @RequestParam(defaultValue = "created_at") String sortBy,
            @Parameter(description = "페이지 번호 (0부터 시작)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기")
            @RequestParam(defaultValue = "10") int size) {

        log.info("게시글 목록 조회 - sortBy: {}, page: {}, size: {}", sortBy, page, size);

        List<PlanPostResponse> posts = planPostService.getPlanPostList(sortBy, page, size);
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "게시글 검색 - 장소별",
            description = "특정 시도/구군으로 연결된 게시글들을 검색합니다.")
    @GetMapping("/filter")
    public ResponseEntity<List<PlanPostResponse>> searchPlanPostsByLocation(
            @Parameter(description = "시도 번호 (전체는 null)")
            @RequestParam(required = false) Long sidoNo,
            @Parameter(description = "구군 번호 (전체는 null)")
            @RequestParam(required = false) Long gugunNo,
            @Parameter(description = "페이지 번호")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기")
            @RequestParam(defaultValue = "10") int size) {

        log.info("장소별 게시글 검색 - sidoNo: {}, gugunNo: {}, page: {}, size: {}",
                sidoNo, gugunNo, page, size);

        List<PlanPostResponse> posts = planPostService.searchPlanPostsByLocation(sidoNo, gugunNo, page, size);
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "게시글 검색 - 키워드",
            description = "제목, 설명, 태그를 기준으로 게시글을 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<List<PlanPostResponse>> searchPlanPosts(
            @Parameter(description = "시도 번호 (전체는 null)")
            @RequestParam(required = false) Long sidoNo,
            @Parameter(description = "구군 번호 (전체는 null)")
            @RequestParam(required = false) Long gugunNo,
            @Parameter(description = "검색 키워드", required = true)
            @RequestParam String keyword,
            @Parameter(description = "페이지 번호")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기")
            @RequestParam(defaultValue = "10") int size) {

        log.info("키워드 게시글 검색 - keyword: {}, sidoNo: {}, gugunNo: {}, page: {}, size: {}",
                keyword, sidoNo, gugunNo, page, size);

        List<PlanPostResponse> posts = planPostService.searchPlanPostsByKeyword(
                keyword, sidoNo, gugunNo, page, size);
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "사용자별 게시글 목록 조회",
            description = "특정 사용자가 작성한 게시글 목록을 조회합니다.")
    @GetMapping("/user/{userNo}")
    public ResponseEntity<List<PlanPostResponse>> getPlanPostsByUser(
            @PathVariable Long userNo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("사용자별 게시글 조회 - userNo: {}, page: {}, size: {}", userNo, page, size);

        List<PlanPostResponse> posts = planPostService.getPlanPostsByUser(userNo, page, size);
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "내가 작성한 게시글 목록 조회",
            description = "현재 로그인한 사용자가 작성한 게시글 목록을 조회합니다.")
    @GetMapping("/my")
    public ResponseEntity<List<PlanPostResponse>> getMyPlanPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // JWT에서 현재 사용자 정보 추출
        Long userNo = authenticationUtil.getCurrentUserNo();

        log.info("내 게시글 목록 조회 - userNo: {}, page: {}, size: {}", userNo, page, size);

        List<PlanPostResponse> posts = planPostService.getPlanPostsByUser(userNo, page, size);
        return ResponseEntity.ok(posts);
    }

    // ===== 좋아요 기능 =====

    @Operation(summary = "여행 계획 게시글 좋아요 등록",
            description = "선택한 게시글에 좋아요를 추가합니다.")
    @PostMapping("/{postNo}/like")
    public ResponseEntity<Void> likePlanPost(@PathVariable Long postNo) {

        // JWT에서 현재 사용자 정보 추출
        Long userNo = authenticationUtil.getCurrentUserNo();
        String userNickname = authenticationUtil.getCurrentUserNickname();

        log.info("좋아요 등록 요청 - postNo: {}, 사용자: {}", postNo, userNickname);

        planPostService.likePlanPost(postNo, userNo);

        log.info("좋아요 등록 완료 - postNo: {}", postNo);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "여행 계획 게시글 좋아요 삭제",
            description = "선택한 게시글의 좋아요를 취소합니다.")
    @DeleteMapping("/{postNo}/like")
    public ResponseEntity<Void> unlikePlanPost(@PathVariable Long postNo) {

        // JWT에서 현재 사용자 정보 추출
        Long userNo = authenticationUtil.getCurrentUserNo();
        String userNickname = authenticationUtil.getCurrentUserNickname();

        log.info("좋아요 취소 요청 - postNo: {}, 사용자: {}", postNo, userNickname);

        planPostService.unlikePlanPost(postNo, userNo);

        log.info("좋아요 취소 완료 - postNo: {}", postNo);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "게시글 좋아요 상태 확인",
            description = "현재 사용자가 해당 게시글에 좋아요를 눌렀는지 확인합니다.")
    @GetMapping("/{postNo}/like/status")
    public ResponseEntity<Boolean> getLikeStatus(@PathVariable Long postNo) {

        // JWT에서 현재 사용자 정보 추출
        Long userNo = authenticationUtil.getCurrentUserNo();

        boolean isLiked = planPostService.isLikedByUser(postNo, userNo);

        return ResponseEntity.ok(isLiked);
    }

    // ===== 내부 클래스: MyPlan으로부터 게시글 생성 요청 DTO =====

    @lombok.Getter
    @lombok.Setter
    @lombok.ToString
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class PlanPostFromMyPlanRequest {
        @Parameter(description = "게시글 제목 (MyPlan 제목과 다르게 하고 싶을 때)", required = true)
        private String title;

        @Parameter(description = "게시글 설명 (추가 설명)")
        private String description;

        @Parameter(description = "썸네일 URL")
        private String thumbnail;

        @Parameter(description = "게시글 태그 목록")
        private java.util.List<String> tags;
    }
}