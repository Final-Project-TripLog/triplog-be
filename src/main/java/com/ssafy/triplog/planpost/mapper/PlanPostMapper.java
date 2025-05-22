package com.ssafy.triplog.planpost.mapper;

import com.ssafy.triplog.planpost.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 여행 계획 게시글 매퍼 인터페이스
 */
@Mapper
public interface PlanPostMapper {

    // ===== plan_post 테이블 관련 =====

    /**
     * 게시글 등록
     * @param planPostDto 게시글 정보
     * @return 등록된 행 수
     */
    int insertPlanPost(PlanPostDto planPostDto);

    /**
     * 게시글 단건 조회
     * @param postNo 게시글 번호
     * @return 게시글 정보
     */
    PlanPostDto selectPlanPostById(@Param("postNo") Long postNo);

    /**
     * 게시글 수정
     * @param planPostDto 수정할 게시글 정보
     * @return 수정된 행 수
     */
    int updatePlanPost(PlanPostDto planPostDto);

    /**
     * 게시글 삭제
     * @param postNo 게시글 번호
     * @return 삭제된 행 수
     */
    int deletePlanPost(@Param("postNo") Long postNo);

    /**
     * 게시글 목록 조회 (정렬 기준별)
     * @param sortBy 정렬 기준
     * @param offset 페이징 오프셋
     * @param limit 조회 건수
     * @return 게시글 목록
     */
    List<PlanPostDto> selectPlanPostList(@Param("sortBy") String sortBy,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);

    /**
     * 게시글 총 개수 조회
     * @return 총 개수
     */
    int countPlanPosts();

    /**
     * 장소별 게시글 검색
     * @param sidoNo 시도 번호
     * @param gugunNo 구군 번호
     * @param offset 페이징 오프셋
     * @param limit 조회 건수
     * @return 게시글 목록
     */
    List<PlanPostDto> searchPlanPostsByLocation(@Param("sidoNo") Long sidoNo,
                                                @Param("gugunNo") Long gugunNo,
                                                @Param("offset") int offset,
                                                @Param("limit") int limit);

    /**
     * 장소별 게시글 총 개수
     * @param sidoNo 시도 번호
     * @param gugunNo 구군 번호
     * @return 총 개수
     */
    int countPlanPostsByLocation(@Param("sidoNo") Long sidoNo, @Param("gugunNo") Long gugunNo);

    /**
     * 키워드 기반 게시글 검색
     * @param keyword 검색 키워드
     * @param sidoNo 시도 번호
     * @param gugunNo 구군 번호
     * @param offset 페이징 오프셋
     * @param limit 조회 건수
     * @return 게시글 목록
     */
    List<PlanPostDto> searchPlanPostsByKeyword(@Param("keyword") String keyword,
                                               @Param("sidoNo") Long sidoNo,
                                               @Param("gugunNo") Long gugunNo,
                                               @Param("offset") int offset,
                                               @Param("limit") int limit);

    /**
     * 키워드 기반 게시글 총 개수
     * @param keyword 검색 키워드
     * @param sidoNo 시도 번호
     * @param gugunNo 구군 번호
     * @return 총 개수
     */
    int countPlanPostsByKeyword(@Param("keyword") String keyword,
                                @Param("sidoNo") Long sidoNo,
                                @Param("gugunNo") Long gugunNo);

    /**
     * 사용자별 게시글 목록 조회
     * @param userNo 사용자 번호
     * @param offset 페이징 오프셋
     * @param limit 조회 건수
     * @return 게시글 목록
     */
    List<PlanPostDto> selectPlanPostsByUser(@Param("userNo") Long userNo,
                                            @Param("offset") int offset,
                                            @Param("limit") int limit);

    /**
     * 사용자별 게시글 총 개수
     * @param userNo 사용자 번호
     * @return 총 개수
     */
    int countPlanPostsByUser(@Param("userNo") Long userNo);

    /**
     * 조회수 증가
     * @param postNo 게시글 번호
     * @return 수정된 행 수
     */
    int increaseViewCount(@Param("postNo") Long postNo);

    // ===== plan_attraction_detail 테이블 관련 =====

    /**
     * 관광지 세부 계획 등록
     * @param attractionDetail 관광지 세부 계획
     * @return 등록된 행 수
     */
    int insertPlanAttractionDetail(PlanAttractionDetailDto attractionDetail);

    /**
     * 관광지 세부 계획 일괄 등록
     * @param attractionDetails 관광지 세부 계획 목록
     * @return 등록된 행 수
     */
    int insertPlanAttractionDetails(@Param("attractions") List<PlanAttractionDetailDto> attractionDetails);

    /**
     * 게시글의 관광지 세부 계획 조회
     * @param postNo 게시글 번호
     * @return 관광지 세부 계획 목록
     */
    List<PlanAttractionDetailDto> selectPlanAttractionDetailsByPostNo(@Param("postNo") Long postNo);

    /**
     * 게시글의 모든 관광지 세부 계획 삭제
     * @param postNo 게시글 번호
     * @return 삭제된 행 수
     */
    int deletePlanAttractionDetailsByPostNo(@Param("postNo") Long postNo);

    // ===== plan_post_tag 테이블 관련 =====

    /**
     * 게시글 태그 등록
     * @param tag 태그 정보
     * @return 등록된 행 수
     */
    int insertPlanPostTag(PlanPostTagDto tag);

    /**
     * 게시글 태그 일괄 등록
     * @param tags 태그 목록
     * @return 등록된 행 수
     */
    int insertPlanPostTags(@Param("tags") List<PlanPostTagDto> tags);

    /**
     * 게시글의 태그 목록 조회
     * @param postNo 게시글 번호
     * @return 태그 목록
     */
    List<PlanPostTagDto> selectPlanPostTagsByPostNo(@Param("postNo") Long postNo);

    /**
     * 게시글의 모든 태그 삭제
     * @param postNo 게시글 번호
     * @return 삭제된 행 수
     */
    int deletePlanPostTagsByPostNo(@Param("postNo") Long postNo);

    // ===== like_post 테이블 관련 =====

    /**
     * 좋아요 등록
     * @param likePost 좋아요 정보
     * @return 등록된 행 수
     */
    int insertLikePost(LikePostDto likePost);

    /**
     * 좋아요 삭제
     * @param postNo 게시글 번호
     * @param userNo 사용자 번호
     * @return 삭제된 행 수
     */
    int deleteLikePost(@Param("postNo") Long postNo, @Param("userNo") Long userNo);

    /**
     * 좋아요 존재 여부 확인
     * @param postNo 게시글 번호
     * @param userNo 사용자 번호
     * @return 좋아요 존재 여부 (1: 존재, 0: 없음)
     */
    int existsLikePost(@Param("postNo") Long postNo, @Param("userNo") Long userNo);

    /**
     * 게시글 좋아요 수 증가
     * @param postNo 게시글 번호
     * @return 수정된 행 수
     */
    int increaseLikedCount(@Param("postNo") Long postNo);

    /**
     * 게시글 좋아요 수 감소
     * @param postNo 게시글 번호
     * @return 수정된 행 수
     */
    int decreaseLikedCount(@Param("postNo") Long postNo);
}