package com.ssafy.triplog.planpost.controller;


//6. 여행 계획 게시글 (  PlanPostController  ) --> 포큿 수?
//        - 여행 계획 게시글 등록
//    - 여행 계획 게시글 수정
//    - 여행 계획 게시글 삭제
//    - 여행 계획 게시글 list 조회(좋아요수, 포크 수, 조회 수 기반 정렬)
//    - 여행 계획 게시글 좋아요 등록
//    - 여행 계획 게시글 좋아요 삭제
//    - 게시글 검색(장소별)
//    - 게시글 검색(제목, 정보, 태그 기반)

import com.ssafy.triplog.planpost.dto.PlanPostDto;
import com.ssafy.triplog.planpost.dto.PlanPostRequest;
import com.ssafy.triplog.planpost.dto.PlanPostResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/planposts")
@RequiredArgsConstructor
@Slf4j
public class PlanPostController {

    @Operation(summary = "여행 계획 게시글 등록", description = "개인 여행 계획을 기반으로 여행 계획 게시글을 등록합니다. --> 리턴값 : 게시글 no")
    @PostMapping
    public ResponseEntity<Long> createPlanPost(@RequestBody PlanPostRequest request) {
        log.debug("createPlanPost -----> request : {}", request);
        return ResponseEntity.ok(0L);
    }

    @Operation(summary = "여행 계획 게시글 수정", description = "기존 여행 계획 게시글을 수정합니다. --> 리턴값 : 게시글 no")
    @PutMapping("/{postNo}")
    public ResponseEntity<Long> updatePlanPost(@PathVariable Long postNo,
                                               @RequestBody PlanPostRequest request) {
        log.debug("updatePlanPost -----> postNo : {}, request : {}", postNo, request);
        return ResponseEntity.ok(0L);
    }

    @Operation(summary = "여행 계획 게시글 삭제", description = "선택한 여행 계획 게시글을 삭제합니다.")
    @DeleteMapping("/{postNo}")
    public ResponseEntity<String> deletePlanPost(@PathVariable Long postNo) {
        log.debug("deletePlanPost -----> postNo : {}", postNo);
        return ResponseEntity.ok("여행 계획 게시글이 삭제되었습니다.");
    }

    @Operation(summary = "여행 계획 게시글 목록 조회", description = "게시글을 좋아요 수, 포크 수, 조회 수 기준으로 정렬하여 조회합니다.")
    @GetMapping
    public ResponseEntity<?> getPlanPostList(@RequestParam String sortBy,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size) {
        log.debug("getPlanPostList -----> sortBy : {}, page : {}, size : {}", sortBy, page, size);
        return ResponseEntity.ok("getPlanPostList");
    }

    @Operation(summary = "여행 계획 게시글 좋아요 등록", description = "선택한 게시글에 좋아요를 추가합니다.")
    @PostMapping("/{postNo}/like")
    public ResponseEntity<String> likePlanPost(@PathVariable Long postNo) {
        log.debug("likePlanPost -----> postNo : {}", postNo);
        return ResponseEntity.ok("좋아요 처리 완료하였습니다.");
    }

    @Operation(summary = "여행 계획 게시글 좋아요 삭제", description = "선택한 게시글의 좋아요를 취소합니다.")
    @DeleteMapping("/{postNo}/like")
    public ResponseEntity<String> unlikePlanPost(@PathVariable Long postNo) {
        log.debug("unlikePlanPost -----> postNo : {}", postNo);
        return ResponseEntity.ok("좋아요 취소 처리 완료되었습니다.");
    }

    @Operation(summary = "게시글 검색 - 장소별", description = "특정 관광지 ID로 연결된 게시글들을 검색합니다.")
    @GetMapping("/filter")
    public ResponseEntity<List<PlanPostResponse>> searchPlanPostsByAttraction(@RequestParam(defaultValue = "전체") Long sidoNo,
                                                                              @RequestParam(defaultValue = "전체") Long gugunsNo,
                                                                              @RequestParam(defaultValue = "0") int page,
                                                                              @RequestParam(defaultValue = "10") int size) {
        log.debug("searchPlanPostsByAttraction -----> sidoNo : {}, gugunsNo : {} , page : {}, size : {}", sidoNo, gugunsNo, page, size);
        return ResponseEntity.ok(List.of(new PlanPostResponse(), new PlanPostResponse()));
    }

    @Operation(summary = "게시글 검색 - 제목, 설명, 태그", description = "제목, 소개 글, 태그를 기준으로 게시글을 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<List<PlanPostResponse>> searchPlanPosts(@RequestParam(defaultValue = "전체") Long sidoNo,
                                                                  @RequestParam(defaultValue = "전체") Long gugunsNo,
                                                                  @RequestParam String keyword,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int size) {
        log.debug("searchPlanPosts -----> sidoNo : {}, gugunNo : {} , keyword : {}, page : {}, size : {}", sidoNo, gugunsNo, keyword, page, size);
        return ResponseEntity.ok(List.of(new PlanPostResponse(), new PlanPostResponse()));
    }
}
