package com.ssafy.triplog.attraction.controller;

//2. 관광지 정보 (  AttractionController  )
//   - 관광지 등록
//   - 관광지 수정
//   - 관광지 삭제
//   - 모든 관광지 list 조회(평점 높은 순, 등록 순, 조회 순)
//   - 특정 지역에 있는 관광지 list 조회
//   - 관광지 검색 기능 (list)
//   - 관광지 상세 조회
//   - 특정 위도, 경도로 부터 K거리 내에 있는 관광지 list조회


import com.ssafy.triplog.attraction.dto.AttractionDto;
import com.ssafy.triplog.attraction.dto.AttractionPreviewResponse;
import com.ssafy.triplog.attraction.dto.AttractionRequest;
import com.ssafy.triplog.attraction.service.AttractionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/attraction")
@RequiredArgsConstructor
@Slf4j
public class AttractionController {

    private final AttractionService attractionService;


    @Operation(summary = "파일 swagger 테스트", description = "스웨거에서 파일을 입력받아 넘겨주는 테스트를 진행 ")
    @PostMapping("file/test")
    public ResponseEntity<String> uploadFile(
            @Parameter(description = "업로드할 파일", required = true)
            @RequestParam("file") MultipartFile file) {

        return ResponseEntity.ok("파일 이름: " + file.getOriginalFilename());
    }

    @Operation(summary = "관광지 등록", description = "새로운 관광지 정보를 등록합니다. 등록 후 등록 된 attraction no 리턴합니다.")
    @PostMapping
    public ResponseEntity<Long> createAttraction(@Valid @ModelAttribute AttractionRequest request) {
        log.debug("createAttraction -----> request : {}", request);
        return ResponseEntity.ok(attractionService.createAttractionWithImages(request));
    }

    @Operation(summary = "관광지 수정", description = "기존 관광지 정보를 수정합니다. 수정 후 attraction no 리턴합니다. ")
    @PutMapping("/{attractionNo}")
    public ResponseEntity<Long> updateAttraction(@PathVariable Long attractionNo,
                                                 @Valid @RequestBody AttractionDto request) {
        log.debug("updateAttraction -----> attractionNo : {}, request : {}", attractionNo, request);
        return ResponseEntity.ok(0L);
    }

    @Operation(summary = "관광지 삭제", description = "관광지 정보를 삭제합니다.")
    @DeleteMapping("/{attractionNo}")
    public ResponseEntity<String> deleteAttraction(@PathVariable Long attractionNo) {
        log.debug("deleteAttraction -----> attractionNo : {}", attractionNo);
        return ResponseEntity.ok("관광지가 삭제 되었습니다. ");
    }

    @Operation(summary = "관광지 상세 조회", description = "특정 관광지의 상세 정보를 조회합니다.")
    @GetMapping("/{attractionNo}")
    public ResponseEntity<AttractionDto> getAttractionDetail(@PathVariable Long attractionNo) {
        log.debug("getAttractionDetail -----> attractionNo : {}", attractionNo);
        return ResponseEntity.ok(new AttractionDto());
    }

    @Operation(summary = "조건에 맞는 관광지 리스트 조회", description = "타입, 지역, 검색 키워드 정렬 등을 조건으로 관광지를 검색합니다.")
    @GetMapping("/list")
    public ResponseEntity<List<AttractionPreviewResponse>> searchAttractions(@RequestParam(required = false) List<Integer> types,
                                                                             @RequestParam(required = false) Integer sidoNo,
                                                                             @RequestParam(required = false) Integer gugunNo,
                                                                             @RequestParam(required = false) String keyword,
                                                                             @RequestParam(defaultValue = "createdAt") String sortBy,
                                                                             @RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "10") int size) {
        log.debug("searchAttractions -----> types : {}, sidoNo : {}, gugunNo : {}, keyword : {}, sortBy : {}, page : {}, size : {}",
                types, sidoNo, gugunNo, keyword, sortBy, page, size);

        return ResponseEntity.ok(
                List.of(new AttractionPreviewResponse(), new AttractionPreviewResponse())
        );
    }

    @Operation(summary = "주변 관광지 조회", description = "위도와 경도를 기준으로 반경 K km 내 관광지를 조회합니다.")
    @GetMapping("/nearby")
    public ResponseEntity<List<AttractionPreviewResponse>> getNearbyAttractions(@RequestParam Double latitude,
                                                                                @RequestParam Double longitude,
                                                                                @RequestParam Double distanceKm,
                                                                                @RequestParam(defaultValue = "0") int page,
                                                                                @RequestParam(defaultValue = "10") int size) {
        log.debug("getNearbyAttractions -----> lat : {}, lng : {}, distanceKm : {}, page : {}, size : {}",
                latitude, longitude, distanceKm, page, size);
        return ResponseEntity.ok(
                List.of(new AttractionPreviewResponse(), new AttractionPreviewResponse())
        );
    }

}
