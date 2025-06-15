// src/main/java/com/ssafy/triplog/myplan/controller/MyPlanController.java
package com.ssafy.triplog.myplan.controller;

import com.ssafy.triplog.myplan.dto.MyDailyPlanDto;
import com.ssafy.triplog.myplan.dto.MyPlanDto;
import com.ssafy.triplog.myplan.dto.MyPlanRequest;
import com.ssafy.triplog.myplan.service.MyPlanService;
//import com.ssafy.triplog.security.util.AuthenticationUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/myplans")
@RequiredArgsConstructor
@Slf4j
public class MyPlanController {

    private final MyPlanService myPlanService;
//    private final AuthenticationUtil authenticationUtil;

    @Operation(summary = "여행 계획 등록 - ok", description = "사용자가 새로운 개인 여행 계획을 등록합니다.")
    @PostMapping
    public ResponseEntity<Long> createMyPlan(@RequestBody MyPlanRequest request) {
        // JWT에서 현재 사용자 번호 추출
//        Long userNo = authenticationUtil.getCurrentUserNo();

//        log.debug("createMyPlan -----> userNo: {}, request : {}", userNo, request);

        Long planNo = myPlanService.createMyPlan(request);
        return ResponseEntity.ok(planNo);
    }


    // {
    //  "title": "제주도 3박 4일 가족 여행",
    //  "description": "온 가족이 함께하는 제주도 힐링 여행",
    //  "startTime": "2025-06-15T08:00:00",
    //  "endTime": "2025-06-18T20:00:00",
    //  "totalMember": 4,
    //  "dailyPlans": [
    //    {
    //      "visitedDate": "2025-06-15",
    //      "startTime": "09:00:00",
    //      "endTime": "12:00:00",
    //      "moveTime": 30,
    //      "attractionTitle": "제주공항",
    //      "attractionThumbnail": "https://example.com/airport.jpg",
    //      "attractionLatitude": 33.5067,
    //      "attractionLongitude": 126.4929,
    //      "attractionRating": 4.0,
    //      "memo": "제주도 도착, 렌터카 픽업",
    //      "attractionNo": 1
    //    },
    //    {
    //      "visitedDate": "2025-06-15",
    //      "startTime": "14:00:00",
    //      "endTime": "17:00:00",
    //      "moveTime": 45,
    //      "attractionTitle": "성산일출봉",
    //      "attractionThumbnail": "https://example.com/seongsan.jpg",
    //      "attractionLatitude": 33.4569,
    //      "attractionLongitude": 126.9419,
    //      "attractionRating": 4.5,
    //      "memo": "세계자연유산, 일출 명소",
    //      "attractionNo": 2
    //    },
    //    {
    //      "visitedDate": "2025-06-16",
    //      "startTime": "09:30:00",
    //      "endTime": "12:30:00",
    //      "moveTime": 60,
    //      "attractionTitle": "한라산 국립공원",
    //      "attractionThumbnail": "https://example.com/hallasan.jpg",
    //      "attractionLatitude": 33.3624,
    //      "attractionLongitude": 126.5346,
    //      "attractionRating": 4.7,
    //      "memo": "등산, 어리목 코스 추천",
    //      "attractionNo": 3
    //    },
    //    {
    //      "visitedDate": "2025-06-16",
    //      "startTime": "15:00:00",
    //      "endTime": "18:00:00",
    //      "moveTime": 30,
    //      "attractionTitle": "쇠소깍",
    //      "attractionThumbnail": "https://example.com/soesokkak.jpg",
    //      "attractionLatitude": 33.2423,
    //      "attractionLongitude": 126.4234,
    //      "attractionRating": 4.3,
    //      "memo": "카약 체험, 맑은 물",
    //      "attractionNo": 4
    //    },
    //    {
    //      "visitedDate": "2025-06-17",
    //      "startTime": "10:00:00",
    //      "endTime": "13:00:00",
    //      "moveTime": 40,
    //      "attractionTitle": "우도",
    //      "attractionThumbnail": "https://example.com/udo.jpg",
    //      "attractionLatitude": 33.5012,
    //      "attractionLongitude": 126.9567,
    //      "attractionRating": 4.4,
    //      "memo": "배로 이동, 자전거 대여",
    //      "attractionNo": 5
    //    },
    //    {
    //      "visitedDate": "2025-06-17",
    //      "startTime": "16:00:00",
    //      "endTime": "19:00:00",
    //      "moveTime": 50,
    //      "attractionTitle": "정방폭포",
    //      "attractionThumbnail": "https://example.com/jeongbang.jpg",
    //      "attractionLatitude": 33.2345,
    //      "attractionLongitude": 126.5678,
    //      "attractionRating": 4.2,
    //      "memo": "바다로 떨어지는 폭포",
    //      "attractionNo": 6
    //    },
    //    {
    //      "visitedDate": "2025-06-18",
    //      "startTime": "10:00:00",
    //      "endTime": "12:00:00",
    //      "moveTime": 20,
    //      "attractionTitle": "동문시장",
    //      "attractionThumbnail": "https://example.com/dongmun.jpg",
    //      "attractionLatitude": 33.5123,
    //      "attractionLongitude": 126.5234,
    //      "attractionRating": 4.1,
    //      "memo": "기념품 쇼핑, 흑돼지 고기",
    //      "attractionNo": 7
    //    }
    //  ]
    //}

    @Operation(summary = "여행 계획 수정 - ok", description = "기존의 개인 여행 계획 정보를 수정합니다.")
    @PutMapping("/{planNo}")
    public ResponseEntity<Long> updateMyPlan(@PathVariable Long planNo,
                                             @RequestBody MyPlanRequest request) {
        log.debug("updateMyPlan -----> planNo : {}, request : {}", planNo, request);

        Long updatedPlanNo = myPlanService.updateMyPlan(planNo, request);
        return ResponseEntity.ok(updatedPlanNo);
    }

    @Operation(summary = "여행 계획 삭제 - ok", description = "사용자의 특정 여행 계획을 삭제합니다.")
    @DeleteMapping("/{planNo}")
    public ResponseEntity<String> deleteMyPlan(@PathVariable Long planNo) {

        log.debug("deleteMyPlan -----> planNo : {}", planNo);

        myPlanService.deleteMyPlan(planNo);
        return ResponseEntity.ok("여행 계획이 삭제되었습니다.");
    }

    @Operation(summary = "사용자별 여행 계획 목록 조회 - ok", description = "현재 로그인한 사용자의 개인 여행 계획 목록을 조회합니다.")
    @GetMapping("/user")
    public ResponseEntity<List<MyPlanDto>> getMyPlansByUser(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size) {

        log.debug("getMyPlansByUser -----> page : {}, size : {}", page, size);

        List<MyPlanDto> myPlans = myPlanService.getMyPlans(page, size);
        return ResponseEntity.ok(myPlans);
    }

    @Operation(summary = "여행 계획 상세 조회 - ok", description = "선택한 개인 여행 계획의 상세 정보를 조회합니다.")
    @GetMapping("/{planNo}")
    public ResponseEntity<List<MyDailyPlanDto>> getMyPlanDetail(@PathVariable Long planNo) {

        log.debug("getMyPlanDetail -----> planNo : {}", planNo);

        try {
            // 권한 검사 + 일일 계획 조회
            List<MyDailyPlanDto> dailyPlans = myPlanService.getMyPlanDetail(planNo);

            log.debug(dailyPlans.toString() + "************");
            return ResponseEntity.ok(dailyPlans);

        } catch (RuntimeException e) {
            log.warn("여행 계획 상세 조회 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.emptyList());
        }
    }
}