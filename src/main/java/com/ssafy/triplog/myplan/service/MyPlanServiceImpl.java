// src/main/java/com/ssafy/triplog/myplan/service/impl/MyPlanServiceImpl.java
package com.ssafy.triplog.myplan.service;

import com.ssafy.triplog.myplan.dto.MyDailyPlanDto;
import com.ssafy.triplog.myplan.dto.MyPlanDto;
import com.ssafy.triplog.myplan.dto.MyPlanRequest;
import com.ssafy.triplog.myplan.mapper.MyPlanMapper;
import com.ssafy.triplog.myplan.service.MyPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 개인 여행 계획 서비스 구현체
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MyPlanServiceImpl implements MyPlanService {

    private final MyPlanMapper myPlanMapper;

    //    private final MyPlanMapper.xml myPlanMapper;
//
//    @Override
//    public Long createMyPlan(Long userNo, MyPlanRequest request) {
//        log.debug("MyPlanServiceImpl.createMyPlan -----> userNo: {}, request: {}", userNo, request);
//
//        // MyPlanDto 생성
//        MyPlanDto myPlanDto = MyPlanDto.builder()
//                .userNo(userNo)
//                .title(request.getTitle())
//                .description(request.getDescription())
//                .startDate(request.getStartDate())
//                .endDate(request.getEndDate())
//                .isPublic(request.getIsPublic())
//                .build();
//
//        // 여행 계획 저장
//        myPlanMapper.insertMyPlan(myPlanDto);
//
//        log.debug("여행 계획 생성 완료 -----> planNo: {}", myPlanDto.getPlanNo());
//        return myPlanDto.getPlanNo();
//    }
//
//    @Override
//    public Long updateMyPlan(Long planNo, Long userNo, MyPlanRequest request) {
//        log.debug("MyPlanServiceImpl.updateMyPlan -----> planNo: {}, userNo: {}, request: {}", planNo, userNo, request);
//
//        // 권한 확인
//        if (!isPlanOwner(planNo, userNo)) {
//            throw new RuntimeException("여행 계획을 수정할 권한이 없습니다.");
//        }
//
//        // 업데이트할 데이터 생성
//        MyPlanDto updateDto = MyPlanDto.builder()
//                .planNo(planNo)
//                .userNo(userNo)
//                .title(request.getTitle())
//                .description(request.getDescription())
//                .startDate(request.getStartDate())
//                .endDate(request.getEndDate())
//                .isPublic(request.getIsPublic())
//                .build();
//
//        // 여행 계획 수정
//        int updatedRows = myPlanMapper.updateMyPlan(updateDto);
//        if (updatedRows == 0) {
//            throw new RuntimeException("여행 계획을 찾을 수 없습니다.");
//        }
//
//        log.debug("여행 계획 수정 완료 -----> planNo: {}", planNo);
//        return planNo;
//    }
//
//    @Override
//    public void deleteMyPlan(Long planNo, Long userNo) {
//        log.debug("MyPlanServiceImpl.deleteMyPlan -----> planNo: {}, userNo: {}", planNo, userNo);
//
//        // 권한 확인
//        if (!isPlanOwner(planNo, userNo)) {
//            throw new RuntimeException("여행 계획을 삭제할 권한이 없습니다.");
//        }
//
//        // 논리적 삭제 (status를 'DELETED'로 변경)
//        int deletedRows = myPlanMapper.deleteMyPlan(planNo, userNo);
//        if (deletedRows == 0) {
//            throw new RuntimeException("여행 계획을 찾을 수 없습니다.");
//        }
//
//        log.debug("여행 계획 삭제 완료 -----> planNo: {}", planNo);
//    }
//
    @Override
    @Transactional(readOnly = true)
    public List<MyPlanDto> getMyPlansByUser(Long userNo, int page, int size) {
        log.debug("MyPlanServiceImpl.getMyPlansByUser -----> userNo: {}, page: {}, size: {}", userNo, page, size);

        // 페이징 처리를 위한 offset 계산
        int offset = page * size;

        List<MyPlanDto> myPlans = myPlanMapper.selectMyPlansByUser(userNo, offset, size);

        log.debug("조회된 여행 계획 수: {}", myPlans.size());

        return myPlans;
    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<MyDailyPlanDto> getMyPlanDetail(Long planNo, Long userNo) {
//        log.debug("MyPlanServiceImpl.getMyPlanDetail -----> planNo: {}, userNo: {}", planNo, userNo);
//
//        // 권한 확인 (본인 계획이거나 공개된 계획만 조회 가능)
//        MyPlanDto myPlan = myPlanMapper.selectMyPlanById(planNo);
//        if (myPlan == null) {
//            throw new RuntimeException("여행 계획을 찾을 수 없습니다.");
//        }
//
//        // 본인 계획이 아니고 비공개 계획인 경우 접근 거부
//        if (!myPlan.getUserNo().equals(userNo) && !myPlan.getIsPublic()) {
//            throw new RuntimeException("여행 계획에 접근할 권한이 없습니다.");
//        }
//
//        List<MyDailyPlanDto> dailyPlans = myPlanMapper.selectDailyPlansByPlanNo(planNo);
//
//        log.debug("조회된 일일 계획 수: {}", dailyPlans.size());
//        return dailyPlans;
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public boolean isPlanOwner(Long planNo, Long userNo) {
//        log.debug("MyPlanServiceImpl.isPlanOwner -----> planNo: {}, userNo: {}", planNo, userNo);
//
//        MyPlanDto myPlan = myPlanMapper.selectMyPlanById(planNo);
//        boolean isOwner = myPlan != null && myPlan.getUserNo().equals(userNo);
//
//        log.debug("계획 소유자 확인 결과: {}", isOwner);
//        return isOwner;
//    }
}