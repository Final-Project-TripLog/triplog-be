// src/main/java/com/ssafy/triplog/myplan/controller/MyPlanController.java
package com.ssafy.triplog.myplan.controller;

import com.ssafy.triplog.myplan.dto.MyDailyPlanDto;
import com.ssafy.triplog.myplan.dto.MyPlanDto;
import com.ssafy.triplog.myplan.dto.MyPlanRequest;
import com.ssafy.triplog.myplan.service.MyPlanService;
import com.ssafy.triplog.security.util.AuthenticationUtil;
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
    private final AuthenticationUtil authenticationUtil;

    @Operation(summary = "여행 계획 등록", description = "사용자가 새로운 개인 여행 계획을 등록합니다.")
    @PostMapping
    public ResponseEntity<Long> createMyPlan(@RequestBody MyPlanRequest request) {
        // JWT에서 현재 사용자 번호 추출
        Long userNo = authenticationUtil.getCurrentUserNo();

        log.debug("createMyPlan -----> userNo: {}, request : {}", userNo, request);

        Long planNo = myPlanService.createMyPlan(userNo, request);
        return ResponseEntity.ok(planNo);
    }

    @Operation(summary = "여행 계획 수정", description = "기존의 개인 여행 계획 정보를 수정합니다.")
    @PutMapping("/{planNo}")
    public ResponseEntity<Long> updateMyPlan(@PathVariable Long planNo,
                                             @RequestBody MyPlanRequest request) {
        // JWT에서 현재 사용자 번호 추출
        Long userNo = authenticationUtil.getCurrentUserNo();

        log.debug("updateMyPlan -----> planNo : {}, userNo: {}, request : {}", planNo, userNo, request);

        Long updatedPlanNo = myPlanService.updateMyPlan(planNo, userNo, request);
        return ResponseEntity.ok(updatedPlanNo);
    }

    @Operation(summary = "여행 계획 삭제", description = "사용자의 특정 여행 계획을 삭제합니다.")
    @DeleteMapping("/{planNo}")
    public ResponseEntity<String> deleteMyPlan(@PathVariable Long planNo) {
        // JWT에서 현재 사용자 번호 추출
        Long userNo = authenticationUtil.getCurrentUserNo();

        log.debug("deleteMyPlan -----> planNo : {}, userNo: {}", planNo, userNo);

        myPlanService.deleteMyPlan(planNo, userNo);
        return ResponseEntity.ok("여행 계획이 삭제되었습니다.");
    }

    @Operation(summary = "사용자별 여행 계획 목록 조회", description = "현재 로그인한 사용자의 개인 여행 계획 목록을 조회합니다.")
    @GetMapping("/user")
    public ResponseEntity<List<MyPlanDto>> getMyPlansByUser(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size) {
        // JWT 토큰에서 현재 로그인한 사용자 ID 추출
        Long userNo = authenticationUtil.getCurrentUserNo();

        log.debug("getMyPlansByUser -----> userNo : {}, page : {}, size : {}", userNo, page, size);

        List<MyPlanDto> myPlans = myPlanService.getMyPlansByUser(userNo, page, size);
        return ResponseEntity.ok(myPlans);
    }

    @Operation(summary = "여행 계획 상세 조회", description = "선택한 개인 여행 계획의 상세 정보를 조회합니다.")
    @GetMapping("/{planNo}")
    public ResponseEntity<List<MyDailyPlanDto>> getMyPlanDetail(@PathVariable Long planNo) {
        // JWT에서 현재 사용자 번호 추출
        Long userNo = authenticationUtil.getCurrentUserNo();

        log.debug("getMyPlanDetail -----> planNo : {}, userNo: {}", planNo, userNo);

        try {
            // 권한 검사 + 일일 계획 조회
            List<MyDailyPlanDto> dailyPlans = myPlanService.getMyPlanDetail(planNo, userNo);
            return ResponseEntity.ok(dailyPlans);

        } catch (RuntimeException e) {
            log.warn("여행 계획 상세 조회 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.emptyList());
        }
    }
}