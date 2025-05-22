package com.ssafy.triplog.myplan.controller;


//5. 개인 여행 계획 (  MyPlanController  )
//    - 여행 계획 등록
//    - 여행 계획 수정
//    - 여행 계획 삭제
//    - 특정 사용자의 여행 계획 list 조회
//    - 개인 여행 계획 상세보기

import com.ssafy.triplog.myplan.dto.MyDailyPlanDto;
import com.ssafy.triplog.myplan.dto.MyPlanDto;
import com.ssafy.triplog.myplan.dto.MyPlanRequest;
import com.ssafy.triplog.myplan.service.MyPlanService;
import com.ssafy.triplog.security.jwt.JWTUtil;
import com.ssafy.triplog.security.util.AuthenticationUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/myplans")
@RequiredArgsConstructor
@Slf4j
public class MyPlanController {

    private final JWTUtil jwtUtil;
    private final AuthenticationUtil authenticationUtil;
    private final MyPlanService myPlanService;

    @Operation(summary = "여행 계획 등록", description = "사용자가 새로운 개인 여행 계획을 등록합니다. --> 리턴값 : myPlanDto no")
    @PostMapping
    public ResponseEntity<Long> createMyPlan(@RequestBody MyPlanRequest request) {
        log.debug("createMyPlan -----> request : {}", request);
        return ResponseEntity.ok(0L);
    }

    @Operation(summary = "여행 계획 수정", description = "기존의 개인 여행 계획 정보를 수정합니다. --> 리턴값 : myPlanDto no\"")
    @PutMapping("/{planNo}")
    public ResponseEntity<Long> updateMyPlan(@PathVariable Long planNo,
                                             @RequestBody MyPlanRequest request) {
        log.debug("updateMyPlan -----> planNo : {}, request : {}", planNo, request);
        return ResponseEntity.ok(0L);
    }

    @Operation(summary = "여행 계획 삭제", description = "사용자의 특정 여행 계획을 삭제합니다.")
    @DeleteMapping("/{planNo}")
    public ResponseEntity<String> deleteMyPlan(@PathVariable Long planNo) {
        log.debug("deleteMyPlan -----> planNo : {}", planNo);
        return ResponseEntity.ok("여행 계획이 삭제되었습니다. ");
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
        log.debug("getMyPlanDetail -----> planNo : {}", planNo);
        return ResponseEntity.ok(List.of(new MyDailyPlanDto(), new MyDailyPlanDto()));
    }
}