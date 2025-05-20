package com.ssafy.triplog.planpost.controller;

//7. 여행 계획 게시글 댓글 (  PlanCommentController  )
//
//- 여행 계획 게시글 댓글 등록
//- 여행 계획 게시글 댓글 삭제
//- 여행 계획 게시글 댓글 수정
//- 여행 계획 게시글 댓글 list 조회


import com.ssafy.triplog.planpost.dto.PlanCommentDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plan-comments")
@RequiredArgsConstructor
@Slf4j
public class PlanCommentController {

    @Operation(summary = "댓글 등록", description = "여행 계획 게시글에 댓글을 등록합니다. --> 리턴값 : 계획 게시글 no")
    @PostMapping
    public ResponseEntity<Long> createComment(@RequestBody PlanCommentDto request) {
        log.debug("createComment -----> request : {}", request);
        return ResponseEntity.ok(0L);
    }

    @Operation(summary = "댓글 삭제", description = "여행 계획 게시글의 특정 댓글을 삭제합니다. --> 리턴값 : 계획 게시글 no")
    @DeleteMapping("/{commentNo}")
    public ResponseEntity<Long> deleteComment(@PathVariable Long commentNo) {
        log.debug("deleteComment -----> commentNo : {}", commentNo);
        return ResponseEntity.ok(0L);
    }

    @Operation(summary = "댓글 수정", description = "여행 계획 게시글의 특정 댓글 내용을 수정합니다. --> 리턴값 : 계획 게시글 no")
    @PutMapping("/{commentNo}")
    public ResponseEntity<Long> updateComment(@PathVariable Long commentNo,
                                              @RequestBody PlanCommentDto request) {
        log.debug("updateComment -----> commentNo : {}, request : {}", commentNo, request);
        return ResponseEntity.ok(0L);
    }

    @Operation(summary = "댓글 목록 조회", description = "여행 계획 게시글의 전체 댓글 목록을 조회합니다.")
    @GetMapping("/post/{postNo}")
    public ResponseEntity<List<PlanCommentDto>> getCommentsByPost(@PathVariable Long postNo,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int size) {
        log.debug("getCommentsByPost -----> postNo : {}, page : {}, size : {}", postNo, page, size);
        return ResponseEntity.ok(List.of(new PlanCommentDto(), new PlanCommentDto()));
    }
}