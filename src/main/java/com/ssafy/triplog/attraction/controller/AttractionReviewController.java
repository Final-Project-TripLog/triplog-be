package com.ssafy.triplog.attraction.controller;

import com.ssafy.triplog.attraction.dto.AttractionReviewDto;
import com.ssafy.triplog.attraction.dto.AttractionReviewResponseDto;
import com.ssafy.triplog.attraction.service.ReviewService;
import com.ssafy.triplog.security.dto.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Attraction Review API", description = "관광지 리뷰 관리 API")
public class AttractionReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 등록 - ok", description = "관광지에 대한 리뷰를 등록합니다. 새로 등록된 리뷰의 관광지 no 리턴 -> 다시 관광지 리뷰를 불러오기 위한 관광지 no")
    @PostMapping
    public ResponseEntity<Long> createReview(@RequestBody AttractionReviewDto request) {
        log.debug("createReview -----> request : {}", request);

        // JWT 토큰에서 현재 로그인한 사용자 ID 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userNo = userDetails.getUserNo();
        String userNickname = userDetails.getNickname();

        // 사용자 정보 설정
        request.setUserNo(userNo);
        request.setUserNickname(userNickname);

        try {
            // 리뷰 등록 서비스 호출
            Long attractionNo = reviewService.addReview(convertToResponseDto(request));
            return ResponseEntity.status(HttpStatus.CREATED).body(attractionNo);
        } catch (Exception e) {
            log.error("리뷰 등록 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(summary = "리뷰 수정 - ok", description = "기존 리뷰의 내용을 수정합니다. 수정 된 리뷰의 관광지 no 리턴 -> 다시 관광지 리뷰를 불러오기 위한 관광지 no")
    @PutMapping("/{reviewNo}")
    public ResponseEntity<Long> updateReview(
            @PathVariable Long reviewNo,
            @RequestBody AttractionReviewDto request) {

        log.debug("updateReview -----> reviewNo : {}, request : {}", reviewNo, request);

        // JWT 토큰에서 현재 로그인한 사용자 ID 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userNo = userDetails.getUserNo();

        try {
            // 리뷰 소유권 확인
            AttractionReviewResponseDto existingReview = reviewService.findReviewById(reviewNo);
            if (existingReview == null) {
                return ResponseEntity.notFound().build();
            }

            if (!existingReview.getUserNo().equals(userNo)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // 요청 데이터에 리뷰 번호 설정
            AttractionReviewResponseDto reviewToUpdate = convertToResponseDto(request);
            reviewToUpdate.setNo(reviewNo);
            reviewToUpdate.setUserNo(userNo);
            reviewToUpdate.setUserNickname(existingReview.getUserNickname());
            reviewToUpdate.setAttractionNo(existingReview.getAttractionNo());

            // 리뷰 수정 서비스 호출
            Long attractionNo = reviewService.updateReview(reviewToUpdate);
            return ResponseEntity.ok(attractionNo);
        } catch (Exception e) {
            log.error("리뷰 수정 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(summary = "리뷰 삭제 - ok", description = "관광지에 작성한 리뷰를 삭제합니다.")
    @DeleteMapping("/{reviewNo}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewNo) {
        log.debug("deleteReview -----> reviewNo : {}", reviewNo);

        // JWT 토큰에서 현재 로그인한 사용자 ID 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userNo = userDetails.getUserNo();

        try {
            // 리뷰 소유권 확인
            AttractionReviewResponseDto existingReview = reviewService.findReviewById(reviewNo);
            if (existingReview == null) {
                return ResponseEntity.notFound().build();
            }

            // 관리자(ROLE_ADMIN)이거나 리뷰 작성자인 경우에만 삭제 허용
            boolean isAdmin = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (!isAdmin && !existingReview.getUserNo().equals(userNo)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("해당 리뷰를 삭제할 권한이 없습니다.");
            }

            // 리뷰 삭제 서비스 호출
            reviewService.deleteReview(reviewNo);
            return ResponseEntity.ok("리뷰가 정상적으로 삭제되었습니다.");
        } catch (Exception e) {
            log.error("리뷰 삭제 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("리뷰 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Operation(summary = "관광지별 리뷰 목록 조회 - ok", description = "특정 관광지에 대한 리뷰 목록을 조회합니다.")
    @GetMapping("/attraction/{attractionNo}")
    public ResponseEntity<List<AttractionReviewDto>> getReviewsByAttraction(
            @PathVariable Long attractionNo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.debug("getReviewsByAttraction -----> attractionNo : {}, page : {}, size : {}",
                attractionNo, page, size);

        try {
            // 관광지별 리뷰 목록 조회 서비스 호출
            List<AttractionReviewResponseDto> reviews =
                    reviewService.getReviewsByAttraction(attractionNo, page, size);

            // ResponseDto를 Dto로 변환
            List<AttractionReviewDto> result = reviews.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("관광지별 리뷰 목록 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "사용자별 리뷰 목록 조회 - ok", description = "특정 사용자가 작성한 리뷰 목록을 조회합니다.")
    @GetMapping("/user/{userNo}")
    public ResponseEntity<List<AttractionReviewDto>> getReviewsByUser(
            @PathVariable Long userNo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.debug("getReviewsByUser -----> userNo : {}, page : {}, size : {}",
                userNo, page, size);

        try {
            // 사용자별 리뷰 목록, 조회 서비스 호출
            List<AttractionReviewResponseDto> reviews =
                    reviewService.getReviewsByUser(userNo, page, size);

            // ResponseDto를 Dto로 변환
            List<AttractionReviewDto> result = reviews.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("사용자별 리뷰 목록 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ReviewDto를 ReviewResponseDto로 변환하는 헬퍼 메서드
    private AttractionReviewResponseDto convertToResponseDto(AttractionReviewDto dto) {
        AttractionReviewResponseDto responseDto = new AttractionReviewResponseDto();
        responseDto.setNo(dto.getNo());
        responseDto.setUserNickname(dto.getUserNickname());
        responseDto.setRating(dto.getRating());
        responseDto.setContent(dto.getContent());
        responseDto.setCreateAt(dto.getCreateAt());
        responseDto.setUpdateAt(dto.getUpdateAt());
        responseDto.setAttractionNo(dto.getAttractionNo());
        responseDto.setUserNo(dto.getUserNo());
        return responseDto;
    }

    // ReviewResponseDto를 ReviewDto로 변환하는 헬퍼 메서드
    private AttractionReviewDto convertToDto(AttractionReviewResponseDto responseDto) {
        AttractionReviewDto dto = new AttractionReviewDto();
        dto.setNo(responseDto.getNo());
        dto.setUserNickname(responseDto.getUserNickname());
        dto.setRating(responseDto.getRating());
        dto.setContent(responseDto.getContent());
        dto.setCreateAt(responseDto.getCreateAt());
        dto.setUpdateAt(responseDto.getUpdateAt());
        dto.setAttractionNo(responseDto.getAttractionNo());
        dto.setUserNo(responseDto.getUserNo());
        return dto;
    }
}