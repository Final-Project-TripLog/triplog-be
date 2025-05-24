package com.ssafy.triplog.planpost.service;

import com.ssafy.triplog.planpost.dto.PlanCommentDto;
import java.util.List;

/**
 * 여행 계획 게시글 댓글 서비스 인터페이스
 */
public interface PlanCommentService {

    /**
     * 댓글 등록
     * @param commentDto 댓글 정보
     * @return 게시글 번호
     */
    Long createComment(PlanCommentDto commentDto);

    /**
     * 댓글 수정
     * @param commentNo 댓글 번호
     * @param commentDto 수정할 댓글 정보
     * @return 게시글 번호
     */
    Long updateComment(Long commentNo, PlanCommentDto commentDto);

    /**
     * 댓글 삭제
     * @param commentNo 댓글 번호
     * @return 게시글 번호
     */
    Long deleteComment(Long commentNo);

    /**
     * 게시글별 댓글 목록 조회
     * @param postNo 게시글 번호
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 댓글 목록
     */
    List<PlanCommentDto> getCommentsByPost(Long postNo, int page, int size);

    /**
     * 댓글 소유자 확인
     * @param commentNo 댓글 번호
     * @param userNo 사용자 번호
     * @return 소유자이면 true
     */
    boolean isCommentOwner(Long commentNo, Long userNo);
}