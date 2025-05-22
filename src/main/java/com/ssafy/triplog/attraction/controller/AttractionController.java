package com.ssafy.triplog.attraction.controller;

import com.ssafy.triplog.attraction.dto.*;
import com.ssafy.triplog.attraction.service.AttractionService;
import com.ssafy.triplog.attraction.service.BookmarkService;
import com.ssafy.triplog.attraction.service.ReviewService;
import com.ssafy.triplog.planpost.dto.PlanPostResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attraction")
@RequiredArgsConstructor
@Slf4j
public class AttractionController {

    private final AttractionService attractionService;
    private final ReviewService reviewService;
    private final BookmarkService bookmarkService;

    @Operation(summary = "관광지 목록 조회 - ok", description = "조건에 맞는 관광지 목록을 조회합니다.")
    @GetMapping("/list")
    public ResponseEntity<List<AttractionResponseDto>> getAttractions(
            @RequestParam(required = false) List<String> types,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.debug("getAttractions -----> types: {}, keyword: {}, sortBy: {}, page: {}, size: {}",
                types, keyword, sortBy, page, size);

        List<AttractionResponseDto> attractions = attractionService.getAttractions(
                types, keyword, sortBy, page, size);

        return ResponseEntity.ok(attractions);
    }

    @Operation(summary = "관광지 상세 조회 - ok", description = "특정 관광지의 상세 정보를 조회합니다.")
    @GetMapping("/{attractionNo}")
    public ResponseEntity<AttractionResponseDto> getAttractionDetail(@PathVariable Long attractionNo) {
        log.debug("getAttractionDetail -----> attractionNo: {}", attractionNo);

        AttractionResponseDto attraction = attractionService.getAttractionDetail(attractionNo);
        return ResponseEntity.ok(attraction);
    }

    @Operation(summary = "관광지 이미지 조회 - ok", description = "관광지와 관련된 이미지를 조회합니다.")
    @GetMapping("/{attractionNo}/images")
    public ResponseEntity<List<AttractionImageResponseDto>> getAttractionImages(@PathVariable Long attractionNo) {
        log.debug("getAttractionImages -----> attractionNo: {}", attractionNo);

        List<AttractionImageResponseDto> images = attractionService.getAttractionImages(attractionNo);
        return ResponseEntity.ok(images);
    }

    @Operation(summary = "관광지 리뷰 조회 - ok", description = "관광지에 작성된 리뷰를 조회합니다.")
    @GetMapping("/{attractionNo}/reviews")
    public ResponseEntity<List<AttractionReviewResponseDto>> getAttractionReviews(
            @PathVariable Long attractionNo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.debug("getAttractionReviews -----> attractionNo: {}, page: {}, size: {}",
                attractionNo, page, size);

        // 인증 정보 로깅
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("현재 인증 정보: {}", auth);

        List<AttractionReviewResponseDto> reviews = reviewService.getReviewsByAttraction(
                attractionNo, page, size);

        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "관광지 상세 정보와 이미지 함께 조회 - ok", description = "관광지 상세 정보와 이미지를 함께 조회합니다.")
    @GetMapping("/{attractionNo}/detail-with-images")
    public ResponseEntity<AttractionDetailWithImagesResponseDto> getAttractionDetailWithImages(
            @PathVariable Long attractionNo) {

        log.debug("getAttractionDetailWithImages -----> attractionNo: {}", attractionNo);

        AttractionResponseDto attraction = attractionService.getAttractionDetail(attractionNo);
        List<AttractionImageResponseDto> images = attractionService.getAttractionImages(attractionNo);

        AttractionDetailWithImagesResponseDto response = new AttractionDetailWithImagesResponseDto();
        response.setAttraction(attraction);
        response.setImages(images);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "관광지 포함 여행 계획 조회 - ok", description = "특정 관광지를 포함하는 여행 계획 목록을 조회합니다.")
    @GetMapping("/{attractionNo}/plans")
    public ResponseEntity<List<PlanPostResponse>> getPlansContainingAttraction(
            @PathVariable Long attractionNo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.debug("getPlansContainingAttraction -----> attractionNo: {}, page: {}, size: {}",
                attractionNo, page, size);

        List<PlanPostResponse> plans = attractionService.getPlansContainingAttraction(
                attractionNo, page, size);

        return ResponseEntity.ok(plans);
    }
}