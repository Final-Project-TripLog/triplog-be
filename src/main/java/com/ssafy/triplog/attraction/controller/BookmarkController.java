package com.ssafy.triplog.attraction.controller;

//3. 북마크 관리 (  BookmarkController  )
//    - 북마크 타입 생성
//    - 북막크 타입 수정
//    - 북마크 타입 삭제
//    - 북마크 타입 내에 있는 관광지 list 조회
//    - 북마크 타입 list 조회
//    - 북마크 생성
//    - 북마크 삭제


import com.ssafy.triplog.attraction.dto.BookmarkDto;
import com.ssafy.triplog.attraction.dto.BookmarkTypeDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
@Slf4j
public class BookmarkController {

    // ----- 북마크 타입 관련 -----
    @Operation(summary = "북마크 타입 생성", description = "사용자가 새로운 북마크 폴더(타입)를 생성합니다.")
    @PostMapping("/types")
    public ResponseEntity<String> createBookmarkType(@RequestBody BookmarkTypeDto request) {
        log.debug("createBookmarkType -----> request : {}", request);
        //200 말고 커스텀에러같은 class 호출해야함
        return ResponseEntity.status(200).body("북마크 타입 생성이 완료되었습니다.");
    }

    @Operation(summary = "북마크 타입 수정", description = "북마크 폴더의 이름을 수정합니다.")
    @PutMapping("/types/{typeNo}")
    public ResponseEntity<String> updateBookmarkType(@PathVariable Long typeNo,
                                                     @RequestBody BookmarkTypeDto request) {
        log.debug("updateBookmarkType -----> typeNo : {}, request : {}", typeNo, request);
        return ResponseEntity.ok("북마크 타입 수정이 완료되었습니다.");
    }

    @Operation(summary = "북마크 타입 삭제", description = "사용자의 북마크 타입(폴더)를 삭제합니다.")
    @DeleteMapping("/types/{typeNo}")
    public ResponseEntity<String> deleteBookmarkType(@PathVariable Long typeNo) {
        log.debug("deleteBookmarkType -----> typeNo : {}", typeNo);
        return ResponseEntity.ok("북마크 타입 삭제가 완료되었습니다.");
    }

    @Operation(summary = "북마크 타입 목록 조회", description = "사용자의 북마크 타입 리스트를 조회합니다.")
    @GetMapping("/types/{userNo}")
    public ResponseEntity<List<BookmarkTypeDto>> getBookmarkTypeList(@PathVariable Long userNo,
                                                                     @RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size) {
        log.debug("getBookmarkTypeList -----> userNo : {}, page : {} , size : {} ", userNo, page, size);
        return ResponseEntity.status(200).body(List.of(new BookmarkTypeDto(), new BookmarkTypeDto()));
    }

    @Operation(summary = "북마크 타입 내 관광지 조회", description = "선택한 북마크 타입에 포함된 관광지 리스트를 조회합니다.")
    @GetMapping("/types/{typeNo}/attractions")
    public ResponseEntity<?> getAttractionsInBookmarkType(@PathVariable Long typeNo,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size) {
        log.debug("getAttractionsInBookmarkType -----> typeNo : {}, page : {}, size : {}", typeNo, page, size);
        return ResponseEntity.ok(List.of(new BookmarkTypeDto(), new BookmarkTypeDto()));
    }

    // ----- 개별 북마크 항목 관련 -----

    @Operation(summary = "북마크 생성", description = "특정 관광지를 지정된 북마크 타입에 추가합니다. -> 리턴 값 : 북마크 type no")
    @PostMapping
    public ResponseEntity<Long> createBookmark(@RequestBody BookmarkDto request) {
        log.debug("createBookmark -----> request : {}", request);
        return ResponseEntity.ok(0L);
    }

    @Operation(summary = "북마크 삭제", description = "특정 관광지를 북마크에서 제거합니다. -> 리턴 값 : 북마크 type no")
    @DeleteMapping
    public ResponseEntity<Long> deleteBookmark(@RequestParam Long typeNo, @RequestParam Long attractionNo) {
        log.debug("deleteBookmark -----> typeNo : {}, attractionNo : {}", typeNo, attractionNo);
        return ResponseEntity.ok(0L);
    }
}