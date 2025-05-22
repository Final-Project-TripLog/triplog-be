// src/main/java/com/ssafy/triplog/myplan/mapper/MyPlanMapper.xml.java
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
     * @param myPlanDto 여행 계획 정보
     * @return 생성된 행 수
     */
    int insertMyPlan(MyPlanDto myPlanDto);

    /**
     * 여행 계획 수정
     * @param myPlanDto 수정할 여행 계획 정보
     * @return 수정된 행 수
     */
    int updateMyPlan(MyPlanDto myPlanDto);

    /**
     * 여행 계획 삭제 (논리적 삭제)
     * @param planNo 여행 계획 번호
     * @param userNo 사용자 번호
     * @return 삭제된 행 수
     */
    int deleteMyPlan(@Param("planNo") Long planNo, @Param("userNo") Long userNo);

    /**
     * 사용자별 여행 계획 목록 조회
     * @param userNo 사용자 번호
     * @param offset 페이징 오프셋
     * @param limit 조회 건수
     * @return 여행 계획 목록
     */
    List<MyPlanDto> selectMyPlansByUser(@Param("userNo") Long userNo,
                                        @Param("offset") int offset,
                                        @Param("limit") int limit);

    /**
     * 여행 계획 단건 조회
     * @param planNo 여행 계획 번호
     * @return 여행 계획 정보
     */
    MyPlanDto selectMyPlanById(@Param("planNo") Long planNo);

    /**
     * 여행 계획의 일일 계획 목록 조회
     * @param planNo 여행 계획 번호
     * @return 일일 계획 목록
     */
    List<MyDailyPlanDto> selectDailyPlansByPlanNo(@Param("planNo") Long planNo);

    /**
     * 사용자별 여행 계획 총 개수 조회
     * @param userNo 사용자 번호
     * @return 총 개수
     */
    int countMyPlansByUser(@Param("userNo") Long userNo);
}