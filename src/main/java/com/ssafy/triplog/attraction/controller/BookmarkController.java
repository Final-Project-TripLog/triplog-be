package com.ssafy.triplog.attraction.controller;

import com.ssafy.triplog.attraction.dto.*;
import com.ssafy.triplog.attraction.service.BookmarkService;
import com.ssafy.triplog.attraction.service.AttractionService;
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
import java.util.Map;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Bookmark API", description = "북마크 관리 API") // 이 어노테이션 추가
public class BookmarkController {

    private final BookmarkService bookmarkService;
    private final AttractionService attractionService;

    @Operation(summary = "북마크 타입 생성 - ok", description = "사용자가 새로운 북마크 폴더(타입)를 생성합니다.")
    @PostMapping("/types")
    public ResponseEntity<BookmarkTypeResponseDto> createBookmarkType(@RequestBody BookmarkTypeCreateRequest request) {
        // name 유효성 검사
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // JWT 토큰에서 현재 로그인한 사용자 ID 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userNo = userDetails.getUserNo();

        log.debug("createBookmarkType -----> name: {}, userNo: {}", request.getName(), userNo);

        // BookmarkTypeDto 객체 생성
        BookmarkTypeDto bookmarkTypeDto = new BookmarkTypeDto();
        bookmarkTypeDto.setName(request.getName());
        bookmarkTypeDto.setUserNo(userNo);
        bookmarkTypeDto.setAttractionCount(0);  // 초기 관광지 수는 0

        // 북마크 타입 생성 서비스 호출
        BookmarkTypeResponseDto createdBookmarkType = bookmarkService.createBookmarkType(bookmarkTypeDto);

        return ResponseEntity.ok(createdBookmarkType);
    }

    @Operation(summary = "북마크 타입 수정 - ok", description = "북마크 폴더의 이름을 수정합니다.")
    @PutMapping("/types/{typeNo}")
    public ResponseEntity<BookmarkTypeResponseDto> updateBookmarkType(
            @PathVariable Long typeNo,
            @RequestBody Map<String, String> request) {

        String name = request.get("name");
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // JWT 토큰에서 현재 로그인한 사용자 ID 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userNo = userDetails.getUserNo();

        log.debug("updateBookmarkType -----> typeNo: {}, name: {}, userNo: {}", typeNo, name, userNo);

        // 북마크 타입 존재 여부 및 소유권 확인
        BookmarkTypeResponseDto existingType = bookmarkService.getBookmarkTypeDetail(typeNo);
        if (!existingType.getUserNo().equals(userNo)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // 북마크 타입 수정 요청 생성
        BookmarkTypeDto updateRequest = new BookmarkTypeDto();
        updateRequest.setNo(typeNo);
        updateRequest.setName(name);
        updateRequest.setUserNo(userNo);
        updateRequest.setAttractionCount(existingType.getAttractionCount());

        // 북마크 타입 수정 서비스 호출
        BookmarkTypeResponseDto updatedType = bookmarkService.updateBookmarkType(updateRequest);

        return ResponseEntity.ok(updatedType);
    }

    @Operation(summary = "북마크 타입 삭제 - ok", description = "사용자의 북마크 타입(폴더)를 삭제합니다.")
    @DeleteMapping("/types/{typeNo}")
    public ResponseEntity<String> deleteBookmarkType(@PathVariable Long typeNo) {
        // JWT 토큰에서 현재 로그인한 사용자 ID 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userNo = userDetails.getUserNo();

        log.debug("deleteBookmarkType -----> typeNo: {}, userNo: {}", typeNo, userNo);

        // 북마크 타입 존재 여부 및 소유권 확인
        BookmarkTypeResponseDto existingType = bookmarkService.getBookmarkTypeDetail(typeNo);
        if (!existingType.getUserNo().equals(userNo)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("해당 북마크 타입에 대한 권한이 없습니다.");
        }

        // 북마크 타입 삭제 서비스 호출
        boolean isDeleted = bookmarkService.deleteBookmarkType(typeNo);

        if (isDeleted) {
            return ResponseEntity.ok("북마크 타입 삭제가 완료되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("북마크 타입 삭제 중 오류가 발생했습니다.");
        }
    }

    @Operation(summary = "북마크 타입 목록 조회 - ok", description = "사용자의 북마크 타입 리스트를 조회합니다.")
    @GetMapping("/types/{userNo}")
    public ResponseEntity<List<BookmarkTypeResponseDto>> getBookmarkTypeList(
            @PathVariable Long userNo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.debug("getBookmarkTypeList -----> userNo: {}, page: {}, size: {}", userNo, page, size);

        // 북마크 타입 목록 조회 서비스 호출
        List<BookmarkTypeResponseDto> bookmarkTypes = bookmarkService.getBookmarkTypesByUser(userNo, page, size);

        return ResponseEntity.ok(bookmarkTypes);
    }

    @Operation(summary = "북마크 타입 내 관광지 조회 - ok", description = "선택한 북마크 타입에 포함된 관광지 리스트를 조회합니다.")
    @GetMapping("/types/{typeNo}/attractions")
    public ResponseEntity<List<AttractionResponseDto>> getAttractionsInBookmarkType(
            @PathVariable Long typeNo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.debug("getAttractionsInBookmarkType -----> typeNo: {}, page: {}, size: {}", typeNo, page, size);

        try {
            // 북마크 타입 존재 여부 확인
            bookmarkService.getBookmarkTypeDetail(typeNo);

            // 북마크 타입에 포함된 관광지 목록 조회
            List<AttractionResponseDto> attractions = attractionService.getAttractionsByBookmarkType(typeNo, page, size);

            return ResponseEntity.ok(attractions);
        } catch (Exception e) {
            log.error("북마크 타입 조회 오류: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of());
        }
    }

    // ----- 개별 북마크 항목 관련 -----
    @Operation(summary = "북마크 생성 - ok", description = "특정 관광지를 지정된 북마크 타입에 추가합니다. -> 리턴 값 : 북마크 type no")
    @PostMapping
    public ResponseEntity<Long> createBookmark(@RequestBody BookmarkDto request) {
        // 요청에서 북마크 타입 번호와 관광지 번호 추출
        Long bookmarkTypeNo = request.getBookmarkTypeNo();
        Long attractionNo = request.getAttractionNo();

        // JWT 토큰에서 현재 로그인한 사용자 ID 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userNo = userDetails.getUserNo();

        log.debug("createBookmark -----> bookmarkTypeNo: {}, attractionNo: {}, userNo: {}",
                bookmarkTypeNo, attractionNo, userNo);

        // 북마크 타입의 소유자가 현재 사용자인지 확인
        BookmarkTypeResponseDto bookmarkType = bookmarkService.getBookmarkTypeDetail(bookmarkTypeNo);
        if (!bookmarkType.getUserNo().equals(userNo)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // BookmarkResponseDto 객체 생성 (order는 서비스에서 자동 설정)
        BookmarkResponseDto bookmarkDto = new BookmarkResponseDto();
        bookmarkDto.setBookmarkTypeNo(bookmarkTypeNo);
        bookmarkDto.setAttractionNo(attractionNo);

        try {
            // 북마크 추가 서비스 호출
            Long resultBookmarkTypeNo = bookmarkService.addBookmark(bookmarkDto);
            return ResponseEntity.ok(resultBookmarkTypeNo);
        } catch (RuntimeException e) {
            log.error("북마크 생성 오류: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(summary = "북마크 삭제 - ok", description = "특정 관광지를 북마크에서 제거합니다. -> 리턴 값 : 북마크 type no")
    @DeleteMapping
    public ResponseEntity<Long> deleteBookmark(
            @RequestParam Long typeNo,
            @RequestParam Long attractionNo) {

        // JWT 토큰에서 현재 로그인한 사용자 ID 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userNo = userDetails.getUserNo();

        log.debug("deleteBookmark -----> typeNo: {}, attractionNo: {}, userNo: {}",
                typeNo, attractionNo, userNo);

        // 북마크 타입의 소유자가 현재 사용자인지 확인
        BookmarkTypeResponseDto bookmarkType = bookmarkService.getBookmarkTypeDetail(typeNo);
        if (!bookmarkType.getUserNo().equals(userNo)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            // 북마크 삭제 서비스 호출
            Long resultBookmarkTypeNo = bookmarkService.removeBookmark(typeNo, attractionNo);
            return ResponseEntity.ok(resultBookmarkTypeNo);
        } catch (RuntimeException e) {
            log.error("북마크 삭제 오류: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}