package com.ssafy.triplog.planpost.mapper;

import com.ssafy.triplog.planpost.dto.PlanCommentDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PlanCommentMapper {

    /**
     * 댓글 등록
     * @param commentDto 댓글 정보
     * @return 등록된 행 수
     */
    int insertComment(PlanCommentDto commentDto);

    /**
     * 댓글 수정
     * @param commentDto 수정할 댓글 정보
     * @return 수정된 행 수
     */
    int updateComment(PlanCommentDto commentDto);

    /**
     * 댓글 삭제
     * @param commentNo 댓글 번호
     * @return 삭제된 행 수
     */
    int deleteComment(@Param("commentNo") Long commentNo);

    /**
     * 댓글 단건 조회
     * @param commentNo 댓글 번호
     * @return 댓글 정보
     */
    PlanCommentDto selectCommentById(@Param("commentNo") Long commentNo);

    /**
     * 게시글에 달린 댓글 목록 조회
     * @param postNo 게시글 번호
     * @param offset 페이징 오프셋
     * @param limit 조회 건수
     * @return 댓글 목록
     */
    List<PlanCommentDto> selectCommentsByPostNo(@Param("postNo") Long postNo,
                                                @Param("offset") int offset,
                                                @Param("limit") int limit);

    /**
     * 부모 댓글의 자식 댓글 수 증가
     * @param parentNo 부모 댓글 번호
     * @return 수정된 행 수
     */
    int increaseChildCount(@Param("parentNo") Long parentNo);

    /**
     * 부모 댓글의 자식 댓글 수 감소
     * @param parentNo 부모 댓글 번호
     * @return 수정된 행 수
     */
    int decreaseChildCount(@Param("parentNo") Long parentNo);
}