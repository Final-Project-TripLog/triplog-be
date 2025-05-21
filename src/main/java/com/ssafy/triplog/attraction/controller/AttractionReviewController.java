package com.ssafy.triplog.attraction.controller;

//4. 관광지 리뷰 (  AttractionReviewController  )
//   - 리뷰 등록
//   - 리뷰 수정
//   - 리뷰 삭제
//   - 특정 관광지에 대한 리뷰 list 조회
//   - 특정 사용자가 작성한 리뷰 list 조회

import com.ssafy.triplog.attraction.dto.AttractionReviewDto;
import com.ssafy.triplog.attraction.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
@Slf4j
public class AttractionReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 등록", description = "관광지에 대한 리뷰를 등록합니다. 새로 등록된 리뷰의 관광지 no 리턴 -> 다시 관광지 리뷰를 불러오기 위한 관광지 no")
    @PostMapping
    public ResponseEntity<Long> createReview(@RequestBody AttractionReviewDto request) {
        log.debug("createReview -----> request : {}", request);
        return ResponseEntity.ok(0L);
    }

    @Operation(summary = "리뷰 수정", description = "기존 리뷰의 내용을 수정합니다. 수정 된 리뷰의 관광지 no 리턴 -> 다시 관광지 리뷰를 불러오기 위한 관광지 no\"")
    @PutMapping("/{reviewNo}")
    public ResponseEntity<Long> updateReview(@PathVariable Long reviewNo,
                                             @RequestBody AttractionReviewDto request) {
        log.debug("updateReview -----> reviewNo : {}, request : {}", reviewNo, request);
        return ResponseEntity.ok(0L);
    }

    @Operation(summary = "리뷰 삭제", description = "관광지에 작성한 리뷰를 삭제합니다.")
    @DeleteMapping("/{reviewNo}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewNo) {
        log.debug("deleteReview -----> reviewNo : {}", reviewNo);
        return ResponseEntity.ok("리뷰가 정상적으로 삭제되었습니다.");
    }

    @Operation(summary = "관광지별 리뷰 목록 조회", description = "특정 관광지에 대한 리뷰 목록을 조회합니다.")
    @GetMapping("/attraction/{attractionNo}")
    public ResponseEntity<List<AttractionReviewDto>> getReviewsByAttraction(@PathVariable Long attractionNo,
                                                                            @RequestParam(defaultValue = "0") int page,
                                                                            @RequestParam(defaultValue = "10") int size) {
        log.debug("getReviewsByAttraction -----> attractionNo : {}, page : {}, size : {}", attractionNo, page, size);
        return ResponseEntity.ok(List.of(new AttractionReviewDto(), new AttractionReviewDto()));
    }

    @Operation(summary = "사용자별 리뷰 목록 조회", description = "특정 사용자가 작성한 리뷰 목록을 조회합니다.")
    @GetMapping("/user/{userNo}")
    public ResponseEntity<List<AttractionReviewDto>> getReviewsByUser(@PathVariable Long userNo,
                                                                      @RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "10") int size) {
        log.debug("getReviewsByUser -----> userNo : {}, page : {}, size : {}", userNo, page, size);
        return ResponseEntity.ok(List.of(new AttractionReviewDto(), new AttractionReviewDto()));
    }
}
