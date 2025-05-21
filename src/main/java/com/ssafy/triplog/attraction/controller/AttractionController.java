package com.ssafy.triplog.attraction.controller;

import com.ssafy.triplog.attraction.dto.*;
import com.ssafy.triplog.attraction.service.AttractionService;
import com.ssafy.triplog.attraction.service.BookmarkService;
import com.ssafy.triplog.attraction.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

    @Operation(summary = "관광지 목록 조회", description = "조건에 맞는 관광지 목록을 조회합니다.")
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

    @Operation(summary = "관광지 상세 조회", description = "특정 관광지의 상세 정보를 조회합니다.")
    @GetMapping("/{attractionNo}")
    public ResponseEntity<AttractionResponseDto> getAttractionDetail(@PathVariable Long attractionNo) {
        log.debug("getAttractionDetail -----> attractionNo: {}", attractionNo);

        AttractionResponseDto attraction = attractionService.getAttractionDetail(attractionNo);
        return ResponseEntity.ok(attraction);
    }

    @Operation(summary = "관광지 이미지 조회", description = "관광지와 관련된 이미지를 조회합니다.")
    @GetMapping("/{attractionNo}/images")
    public ResponseEntity<List<AttractionImageResponseDto>> getAttractionImages(@PathVariable Long attractionNo) {
        log.debug("getAttractionImages -----> attractionNo: {}", attractionNo);

        List<AttractionImageResponseDto> images = attractionService.getAttractionImages(attractionNo);
        return ResponseEntity.ok(images);
    }

    @Operation(summary = "관광지 리뷰 조회", description = "관광지에 작성된 리뷰를 조회합니다.")
    @GetMapping("/{attractionNo}/reviews")
    public ResponseEntity<List<AttractionReviewResponseDto>> getAttractionReviews(
            @PathVariable Long attractionNo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.debug("getAttractionReviews -----> attractionNo: {}, page: {}, size: {}",
                attractionNo, page, size);

        List<AttractionReviewResponseDto> reviews = reviewService.getReviewsByAttraction(
                attractionNo, page, size);

        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "관광지 상세 정보와 이미지 함께 조회", description = "관광지 상세 정보와 이미지를 함께 조회합니다.")
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
}

@RestController
@RequestMapping("/api/bookmark")
@RequiredArgsConstructor
@Slf4j
public class BookmarkController {

    private final BookmarkService bookmarkService;
    private final AttractionService attractionService;

    @Operation(summary = "북마크 타입 목록 조회", description = "사용자의 북마크 타입 목록을 조회합니다.")
    @GetMapping("/types/{userNo}")
    public ResponseEntity<List<BookmarkTypeResponseDto>> getBookmarkTypes(
            @PathVariable Long userNo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.debug("getBookmarkTypes -----> userNo: {}, page: {}, size: {}", userNo, page, size);

        List<BookmarkTypeResponseDto> bookmarkTypes = bookmarkService.getBookmarkTypesByUser(
                userNo, page, size);

        return ResponseEntity.ok(bookmarkTypes);
    }

    @Operation(summary = "북마크 타입 내 관광지 조회", description = "특정 북마크 타입에 포함된 관광지를 조회합니다.")
    @GetMapping("/types/{bookmarkTypeNo}/attractions")
    public ResponseEntity<BookmarkTypeWithAttractionsResponseDto> getAttractionsInBookmarkType(
            @PathVariable Long bookmarkTypeNo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.debug("getAttractionsInBookmarkType -----> bookmarkTypeNo: {}, page: {}, size: {}",
                bookmarkTypeNo, page, size);

        // 북마크 타입 정보 조회
        BookmarkTypeResponseDto bookmarkType = bookmarkService.getBookmarkTypeDetail(bookmarkTypeNo);

        // 북마크에 포함된 관광지 목록 조회
        List<AttractionResponseDto> attractions = attractionService.getAttractionsByBookmarkType(
                bookmarkTypeNo, page, size);

        // 응답 구성
        BookmarkTypeWithAttractionsResponseDto response = new BookmarkTypeWithAttractionsResponseDto();
        response.setBookmarkType(bookmarkType);
        response.setAttractions(attractions);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "북마크 생성", description = "관광지를 북마크에 추가합니다.")
    @PostMapping
    public ResponseEntity<Long> createBookmark(@RequestBody BookmarkResponseDto bookmarkDto) {
        log.debug("createBookmark -----> request: {}", bookmarkDto);

        Long bookmarkTypeNo = bookmarkService.addBookmark(bookmarkDto);
        return ResponseEntity.ok(bookmarkTypeNo);
    }

    @Operation(summary = "북마크 삭제", description = "북마크에서 관광지를 제거합니다.")
    @DeleteMapping
    public ResponseEntity<Long> deleteBookmark(
            @RequestParam Long bookmarkTypeNo,
            @RequestParam Long attractionNo) {

        log.debug("deleteBookmark -----> bookmarkTypeNo: {}, attractionNo: {}",
                bookmarkTypeNo, attractionNo);

        Long resultTypeNo = bookmarkService.removeBookmark(bookmarkTypeNo, attractionNo);
        return ResponseEntity.ok(resultTypeNo);
    }
}

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 등록", description = "관광지에 리뷰를 등록합니다.")
    @PostMapping
    public ResponseEntity<Long> createReview(@RequestBody AttractionReviewResponseDto reviewDto) {
        log.debug("createReview -----> request: {}", reviewDto);

        Long attractionNo = reviewService.addReview(reviewDto);
        return ResponseEntity.ok(attractionNo);
    }

    @Operation(summary = "리뷰 수정", description = "기존 리뷰를 수정합니다.")
    @PutMapping("/{reviewNo}")
    public ResponseEntity<Long> updateReview(
            @PathVariable Long reviewNo,
            @RequestBody AttractionReviewResponseDto reviewDto) {

        log.debug("updateReview -----> reviewNo: {}, request: {}", reviewNo, reviewDto);

        reviewDto.setNo(reviewNo);
        Long attractionNo = reviewService.updateReview(reviewDto);
        return ResponseEntity.ok(attractionNo);
    }

    @Operation(summary = "리뷰 삭제", description = "리뷰를 삭제합니다.")
    @DeleteMapping("/{reviewNo}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewNo) {
        log.debug("deleteReview -----> reviewNo: {}", reviewNo);

        reviewService.deleteReview(reviewNo);
        return ResponseEntity.ok("리뷰가 성공적으로 삭제되었습니다.");
    }

    @Operation(summary = "사용자 작성 리뷰 조회", description = "사용자가 작성한 리뷰를 조회합니다.")
    @GetMapping("/user/{userNo}")
    public ResponseEntity<List<AttractionReviewResponseDto>> getUserReviews(
            @PathVariable Long userNo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.debug("getUserReviews -----> userNo: {}, page: {}, size: {}", userNo, page, size);

        List<AttractionReviewResponseDto> reviews = reviewService.getReviewsByUser(userNo, page, size);
        return ResponseEntity.ok(reviews);
    }
}