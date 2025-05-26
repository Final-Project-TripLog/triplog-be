package com.ssafy.triplog.planpost.service;

import com.ssafy.triplog.planpost.controller.PlanPostController.PlanPostFromMyPlanRequest;
import com.ssafy.triplog.planpost.dto.PlanPostDetailResponse;
import com.ssafy.triplog.planpost.dto.PlanPostRequest;
import com.ssafy.triplog.planpost.dto.PlanPostResponse;
import java.util.List;

/**
 * 여행 계획 게시글 서비스 인터페이스
 */
public interface PlanPostService {

    // ===== MyPlan 기반 게시글 생성 =====

    /**
     * MyPlan으로부터 게시글 생성
     * @param myPlanNo 개인 여행 계획 번호
     * @param userNo 사용자 번호
     * @param userNickname 사용자 닉네임
     * @param request 게시글 추가 정보
     * @return 생성된 게시글 번호
     */
    Long createPlanPostFromMyPlan(Long myPlanNo, Long userNo, String userNickname,
                                  PlanPostFromMyPlanRequest request);

    // ===== 일반 CRUD =====

    /**
     * 게시글 직접 생성
     * @param request 게시글 생성 요청
     * @return 생성된 게시글 번호
     */
    Long createPlanPost(PlanPostRequest request);

    /**
     * 게시글 단건 조회
     * @param postNo 게시글 번호
     * @return 게시글 기본 정보
     */
    PlanPostResponse getPlanPostById(Long postNo);

    /**
     * 게시글 상세 조회 (관광지 세부 계획 포함)
     * @param postNo 게시글 번호
     * @param currentUserNo 현재 사용자 번호 (좋아요 상태 확인용, null 가능)
     * @return 게시글 상세 정보
     */
    PlanPostDetailResponse getPlanPostDetail(Long postNo, Long currentUserNo);

    /**
     * 게시글 수정
     * @param request 게시글 수정 요청
     * @return 수정된 게시글 번호
     */
    Long updatePlanPost(PlanPostRequest request);

    /**
     * 게시글 삭제
     * @param postNo 게시글 번호
     */
    void deletePlanPost(Long postNo);

    /**
     * 게시글 소유자 확인
     * @param postNo 게시글 번호
     * @param userNo 사용자 번호
     * @return 소유자이면 true
     */
    boolean isPostOwner(Long postNo, Long userNo);

    // ===== 조회 및 검색 =====

    /**
     * 게시글 목록 조회 (정렬 기준별)
     * @param sortBy 정렬 기준 (liked_count, fork_count, view_count, created_at)
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 게시글 목록
     */
    List<PlanPostResponse> getPlanPostList(String sortBy, int page, int size);

    /**
     * 장소별 게시글 검색
     * @param sidoNo 시도 번호
     * @param gugunNo 구군 번호
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 게시글 목록
     */
    List<PlanPostResponse> searchPlanPostsByLocation(Long sidoNo, Long gugunNo, int page, int size);

    /**
     * 키워드 기반 게시글 검색
     * @param keyword 검색 키워드
     * @param sidoNo 시도 번호
     * @param gugunNo 구군 번호
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 게시글 목록
     */
    List<PlanPostResponse> searchPlanPostsByKeyword(String keyword, Long sidoNo, Long gugunNo,
                                                    int page, int size);

    /**
     * 사용자별 게시글 목록 조회
     * @param userNo 사용자 번호
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 게시글 목록
     */
    List<PlanPostResponse> getPlanPostsByUser(Long userNo, int page, int size);

    // ===== 좋아요 기능 =====

    /**
     * 게시글 좋아요 등록
     * @param postNo 게시글 번호
     * @param userNo 사용자 번호
     */
    void likePlanPost(Long postNo, Long userNo);

    /**
     * 게시글 좋아요 취소
     * @param postNo 게시글 번호
     * @param userNo 사용자 번호
     */
    void unlikePlanPost(Long postNo, Long userNo);

    /**
     * 사용자의 게시글 좋아요 여부 확인
     * @param postNo 게시글 번호
     * @param userNo 사용자 번호
     * @return 좋아요 상태
     */
    boolean isLikedByUser(Long postNo, Long userNo);

    // ===== 기타 =====

    /**
     * 게시글 조회수 증가
     * @param postNo 게시글 번호
     */
    void increaseViewCount(Long postNo);
}