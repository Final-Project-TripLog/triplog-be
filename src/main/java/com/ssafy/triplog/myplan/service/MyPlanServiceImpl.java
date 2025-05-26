// src/main/java/com/ssafy/triplog/myplan/service/MyPlanServiceImpl.java
package com.ssafy.triplog.myplan.service;

import com.ssafy.triplog.myplan.dto.MyDailyPlanDto;
import com.ssafy.triplog.myplan.dto.MyPlanDto;
import com.ssafy.triplog.myplan.dto.MyPlanRequest;
import com.ssafy.triplog.myplan.mapper.MyPlanMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

    @Override
    public Long createMyPlan(Long userNo, MyPlanRequest request) {
        log.debug("MyPlanServiceImpl.createMyPlan -----> userNo: {}, request: {}", userNo, request);

        try {
            // 1. 기본 여행 계획 생성
            MyPlanDto myPlanDto = MyPlanDto.builder()
                    .userNo(userNo)
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .startTime(request.getStartTime())
                    .endTime(request.getEndTime())
                    .totalMember(request.getTotalMember())
                    .build();

            // 2. my_plan 테이블에 저장
            log.debug("여행 계획 저장 중...");
            int planResult = myPlanMapper.insertMyPlan(myPlanDto);

            if (planResult == 0 || myPlanDto.getNo() == null) {
                throw new RuntimeException("여행 계획 생성 실패");
            }

            Long createdPlanNo = myPlanDto.getNo();
            log.debug("여행 계획 생성 완료 -----> planNo: {}", createdPlanNo);

            // 3. 일일 계획들이 있으면 함께 저장
            if (request.getDailyPlans() != null && !request.getDailyPlans().isEmpty()) {
                log.debug("일일 계획 {} 개 저장 중...", request.getDailyPlans().size());

                // 각 일일 계획에 생성된 planNo 설정
                for (MyDailyPlanDto dailyPlan : request.getDailyPlans()) {
                    dailyPlan.setMyPlanNo(createdPlanNo);
                }

                // 일일 계획들 일괄 저장
                int dailyResult = myPlanMapper.insertMyDailyPlans(request.getDailyPlans());
                log.debug("일일 계획 저장 완료 -----> 저장된 개수: {}", dailyResult);
            }

            return createdPlanNo;

        } catch (Exception e) {
            log.error("여행 계획 생성 중 오류 발생", e);
            throw new RuntimeException("여행 계획 생성 실패: " + e.getMessage(), e);
        }
    }

    @Override
    public Long updateMyPlan(Long planNo, Long userNo, MyPlanRequest request) {
        log.debug("MyPlanServiceImpl.updateMyPlan -----> planNo: {}, userNo: {}, request: {}", planNo, userNo, request);

        // 권한 확인
        if (!isPlanOwner(planNo, userNo)) {
            throw new RuntimeException("여행 계획을 수정할 권한이 없습니다.");
        }

        try {
            // 1. 기본 여행 계획 수정
            MyPlanDto updateDto = MyPlanDto.builder()
                    .no(planNo)
                    .userNo(userNo)
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .startTime(request.getStartTime())
                    .endTime(request.getEndTime())
                    .totalMember(request.getTotalMember())
                    .build();

            int planResult = myPlanMapper.updateMyPlan(updateDto);
            if (planResult == 0) {
                throw new RuntimeException("여행 계획을 찾을 수 없습니다.");
            }

            // 2. 일일 계획 수정 (기존 삭제 후 새로 생성)
            if (request.getDailyPlans() != null && !request.getDailyPlans().isEmpty()) {
                log.debug("기존 일일 계획 삭제 중...");
                myPlanMapper.deleteMyDailyPlansByPlanNo(planNo);

                log.debug("새로운 일일 계획 {} 개 저장 중...", request.getDailyPlans().size());

                // 각 일일 계획에 planNo 설정
                for (MyDailyPlanDto dailyPlan : request.getDailyPlans()) {
                    dailyPlan.setMyPlanNo(planNo);
                }

                // 새로운 일일 계획들 저장
                myPlanMapper.insertMyDailyPlans(request.getDailyPlans());
            }

            log.debug("여행 계획 수정 완료 -----> planNo: {}", planNo);
            return planNo;

        } catch (Exception e) {
            log.error("여행 계획 수정 중 오류 발생", e);
            throw new RuntimeException("여행 계획 수정 실패: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteMyPlan(Long planNo, Long userNo) {
        log.debug("MyPlanServiceImpl.deleteMyPlan -----> planNo: {}, userNo: {}", planNo, userNo);

        // 권한 확인
        if (!isPlanOwner(planNo, userNo)) {
            throw new RuntimeException("여행 계획을 삭제할 권한이 없습니다.");
        }

        try {
            // 1. 관련된 일일 계획들 먼저 삭제
            log.debug("관련 일일 계획들 삭제 중...");
            myPlanMapper.deleteMyDailyPlansByPlanNo(planNo);

            // 2. 여행 계획 삭제
            log.debug("여행 계획 삭제 중...");
            int result = myPlanMapper.deleteMyPlan(planNo, userNo);
            if (result == 0) {
                throw new RuntimeException("여행 계획을 찾을 수 없습니다.");
            }

            log.debug("여행 계획 삭제 완료 -----> planNo: {}", planNo);

        } catch (Exception e) {
            log.error("여행 계획 삭제 중 오류 발생", e);
            throw new RuntimeException("여행 계획 삭제 실패: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MyPlanDto> getMyPlansByUser(Long userNo, int page, int size) {
        log.debug("MyPlanServiceImpl.getMyPlansByUser -----> userNo: {}, page: {}, size: {}", userNo, page, size);

//        log.debug(startDate.toString(), endDate.toString());
        int offset = page * size;
        List<MyPlanDto> myPlans = myPlanMapper.selectMyPlansByUser(userNo, offset, size);

        log.debug("조회된 여행 계획 수: {}", myPlans.size());
        return myPlans;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MyDailyPlanDto> getMyPlanDetail(Long planNo, Long userNo) {
        log.debug("MyPlanServiceImpl.getMyPlanDetail -----> planNo: {}, userNo: {}", planNo, userNo);

        // 1. 여행 계획 존재 여부 및 권한 확인
        MyPlanDto myPlan = myPlanMapper.selectMyPlanById(planNo);

        if (myPlan == null) {
            throw new RuntimeException("여행 계획을 찾을 수 없습니다. planNo: " + planNo);
        }

        // 2. 권한 확인
        if (!myPlan.getUserNo().equals(userNo)) {
            throw new RuntimeException("해당 여행 계획에 접근할 권한이 없습니다. planNo: " + planNo);
        }

        // 3. 일일 계획 목록 조회
        log.debug(myPlan.toString() + " Here");
        List<MyDailyPlanDto> dailyPlans = myPlanMapper.selectDailyPlansByPlanNo(planNo);

        log.debug("조회된 일일 계획 수: {}", dailyPlans.size());
        return dailyPlans;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPlanOwner(Long planNo, Long userNo) {
        log.debug("MyPlanServiceImpl.isPlanOwner -----> planNo: {}, userNo: {}", planNo, userNo);

        MyPlanDto myPlan = myPlanMapper.selectMyPlanById(planNo);
        boolean isOwner = myPlan != null && myPlan.getUserNo().equals(userNo);

        log.debug("계획 소유자 확인 결과: {}", isOwner);
        return isOwner;
    }
}