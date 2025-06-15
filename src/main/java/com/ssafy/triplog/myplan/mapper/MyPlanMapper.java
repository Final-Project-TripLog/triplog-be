// src/main/java/com/ssafy/triplog/myplan/mapper/MyPlanMapper.java
package com.ssafy.triplog.myplan.mapper;

import com.ssafy.triplog.myplan.dto.MyDailyPlanDto;
import com.ssafy.triplog.myplan.dto.MyPlanDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 개인 여행 계획 매퍼 인터페이스
 */
@Mapper
public interface MyPlanMapper {

    /**
     * 여행 계획 생성
     *
     * @param myPlanDto 여행 계획 정보
     * @return 생성된 행 수
     */
    int insertMyPlan(MyPlanDto myPlanDto);

    /**
     * 일일 여행 계획 생성
     *
     * @param myDailyPlanDto 일일 여행 계획 정보
     * @return 생성된 행 수
     */
    int insertMyDailyPlan(MyDailyPlanDto myDailyPlanDto);

    /**
     * 일일 여행 계획 여러개 일괄 생성
     *
     * @param dailyPlans 일일 여행 계획 목록
     * @return 생성된 행 수
     */
    int insertMyDailyPlans(@Param("dailyPlans") List<MyDailyPlanDto> dailyPlans);

    /**
     * 여행 계획 수정
     *
     * @param myPlanDto 수정할 여행 계획 정보
     * @return 수정된 행 수
     */
    int updateMyPlan(MyPlanDto myPlanDto);

    /**
     * 여행 계획 삭제
     *
     * @param planNo 여행 계획 번호
     * @return 삭제된 행 수
     */
    int deleteMyPlan(@Param("planNo") Long planNo);

    /**
     * 특정 여행 계획의 모든 일일 계획 삭제
     *
     * @param myPlanNo 여행 계획 번호
     * @return 삭제된 행 수
     */
    int deleteMyDailyPlansByPlanNo(@Param("myPlanNo") Long myPlanNo);

    /**
     * 사용자별 여행 계획 목록 조회
     *
     * @param offset 페이징 오프셋
     * @param limit  조회 건수
     * @return 여행 계획 목록
     */
    List<MyPlanDto> selectMyPlans(@Param("offset") int offset,
                                  @Param("limit") int limit);

    /**
     * 여행 계획 단건 조회 (권한 확인용)
     *
     * @param planNo 여행 계획 번호
     * @return 여행 계획 정보
     */
    MyPlanDto selectMyPlanById(@Param("planNo") Long planNo);

    /**
     * 여행 계획의 일일 계획 목록 조회
     *
     * @param planNo 여행 계획 번호
     * @return 일일 계획 목록 (visited_date, start_time 순으로 정렬)
     */
    List<MyDailyPlanDto> selectDailyPlansByPlanNo(@Param("planNo") Long planNo);

//    /**
//     * 사용자별 여행 계획 총 개수 조회
//     *
//     * @param userNo 사용자 번호
//     * @return 총 개수
//     */
//    int countMyPlansByUser(@Param("userNo") Long userNo);
}