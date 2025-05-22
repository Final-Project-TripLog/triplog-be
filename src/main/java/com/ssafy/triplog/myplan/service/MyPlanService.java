// src/main/java/com/ssafy/triplog/myplan/service/MyPlanService.java
package com.ssafy.triplog.myplan.service;

import com.ssafy.triplog.myplan.dto.MyDailyPlanDto;
import com.ssafy.triplog.myplan.dto.MyPlanDto;
import com.ssafy.triplog.myplan.dto.MyPlanRequest;

import java.util.List;

/**
 * 개인 여행 계획 서비스 인터페이스
 */
public interface MyPlanService {

//    /**
//     * 여행 계획 생성
//     * @param userNo 사용자 번호
//     * @param request 여행 계획 생성 요청 정보
//     * @return 생성된 여행 계획 번호
//     */
//    Long createMyPlan(Long userNo, MyPlanRequest request);
//
//    /**
//     * 여행 계획 수정
//     * @param planNo 여행 계획 번호
//     * @param userNo 사용자 번호
//     * @param request 여행 계획 수정 요청 정보
//     * @return 수정된 여행 계획 번호
//     */
//    Long updateMyPlan(Long planNo, Long userNo, MyPlanRequest request);
//
//    /**
//     * 여행 계획 삭제
//     * @param planNo 여행 계획 번호
//     * @param userNo 사용자 번호
//     */
//    void deleteMyPlan(Long planNo, Long userNo);

    /**
     * 사용자별 여행 계획 목록 조회
     * @param userNo 사용자 번호
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 여행 계획 목록
     */
    List<MyPlanDto> getMyPlansByUser(Long userNo, int page, int size);

//    /**
//     * 여행 계획 상세 조회 (일일 계획 포함)
//     * @param planNo 여행 계획 번호
//     * @param userNo 사용자 번호
//     * @return 일일 여행 계획 목록
//     */
//    List<MyDailyPlanDto> getMyPlanDetail(Long planNo, Long userNo);
//
//    /**
//     * 여행 계획 소유자 확인
//     * @param planNo 여행 계획 번호
//     * @param userNo 사용자 번호
//     * @return 소유자이면 true, 아니면 false
//     */
//    boolean isPlanOwner(Long planNo, Long userNo);
}