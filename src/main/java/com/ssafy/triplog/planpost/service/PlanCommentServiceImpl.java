package com.ssafy.triplog.planpost.service;

import com.ssafy.triplog.planpost.dto.PlanCommentDto;
import com.ssafy.triplog.planpost.mapper.PlanCommentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 여행 계획 게시글 댓글 서비스 구현체
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PlanCommentServiceImpl implements PlanCommentService {

    private final PlanCommentMapper commentMapper;

    @Override
    public Long createComment(PlanCommentDto commentDto) {
        log.info("댓글 등록 - 사용자: {}, 게시글: {}", commentDto.getUserNickname(), commentDto.getPlanPostNo());

        try {
            // 현재 시간 설정
            commentDto.setCreatedAt(LocalDateTime.now());
            commentDto.setUpdatedAt(LocalDateTime.now());

            // 댓글 레벨 설정 (기본값 0 - 최상위 댓글)
            if (commentDto.getLevel() == null) {
                commentDto.setLevel(0);
            }

            // 계층 경로 설정 (초기값은 댓글 번호가 생성된 후 업데이트)
            if (commentDto.getPath() == null) {
                commentDto.setPath("00000");
            }

            // 자식 댓글 수 초기화
            commentDto.setChildCount(0);

            // 대댓글인 경우 부모 댓글의 자식 수 증가
            if (commentDto.getParentNo() != null) {
                // 대댓글 레벨 설정 (부모 + 1)
                PlanCommentDto parentComment = commentMapper.selectCommentById(commentDto.getParentNo());
                if (parentComment != null) {
                    commentDto.setLevel(parentComment.getLevel() + 1);
                    // 부모 댓글 경로를 기반으로 경로 설정 (실제 ID는 저장 후 업데이트)
                    commentDto.setPath(parentComment.getPath());

                    // 부모 댓글의 자식 수 증가
                    commentMapper.increaseChildCount(commentDto.getParentNo());
                }
            }

            // 댓글 저장
            int result = commentMapper.insertComment(commentDto);
            if (result == 0 || commentDto.getNo() == null) {
                throw new RuntimeException("댓글 저장에 실패했습니다.");
            }

            // 댓글 ID가 생성된 후 경로 업데이트 (필요한 경우)
            if (commentDto.getParentNo() == null) {
                // 최상위 댓글인 경우 경로를 댓글 ID로 설정
                String path = String.format("%05d", commentDto.getNo());
                commentDto.setPath(path);
                commentMapper.updateComment(commentDto);
            } else {
                // 대댓글인 경우 부모 경로 + 자신의 ID로 설정
                String path = commentDto.getPath() + "-" + String.format("%05d", commentDto.getNo());
                commentDto.setPath(path);
                commentMapper.updateComment(commentDto);
            }

            log.info("댓글 등록 완료 - commentNo: {}, postNo: {}", commentDto.getNo(), commentDto.getPlanPostNo());
            return commentDto.getPlanPostNo();

        } catch (Exception e) {
            log.error("댓글 등록 중 오류 발생", e);
            throw new RuntimeException("댓글 등록 실패: " + e.getMessage(), e);
        }
    }

    @Override
    public Long updateComment(Long commentNo, PlanCommentDto commentDto) {
        log.info("댓글 수정 - commentNo: {}", commentNo);

        try {
            // 기존 댓글 조회
            PlanCommentDto existingComment = commentMapper.selectCommentById(commentNo);
            if (existingComment == null) {
                throw new RuntimeException("댓글을 찾을 수 없습니다.");
            }

            // 업데이트할 내용만 변경
            existingComment.setContent(commentDto.getContent());
            existingComment.setUpdatedAt(LocalDateTime.now());

            // 댓글 수정
            int result = commentMapper.updateComment(existingComment);
            if (result == 0) {
                throw new RuntimeException("댓글 수정에 실패했습니다.");
            }

            log.info("댓글 수정 완료 - commentNo: {}, postNo: {}", commentNo, existingComment.getPlanPostNo());
            return existingComment.getPlanPostNo();

        } catch (Exception e) {
            log.error("댓글 수정 중 오류 발생", e);
            throw new RuntimeException("댓글 수정 실패: " + e.getMessage(), e);
        }
    }

    @Override
    public Long deleteComment(Long commentNo) {
        log.info("댓글 삭제 - commentNo: {}", commentNo);

        try {
            // 기존 댓글 조회
            PlanCommentDto existingComment = commentMapper.selectCommentById(commentNo);
            if (existingComment == null) {
                throw new RuntimeException("댓글을 찾을 수 없습니다.");
            }

            Long postNo = existingComment.getPlanPostNo();

            // 부모 댓글이 있는 경우 자식 댓글 수 감소
            if (existingComment.getParentNo() != null) {
                commentMapper.decreaseChildCount(existingComment.getParentNo());
            }

            // 댓글 삭제
            int result = commentMapper.deleteComment(commentNo);
            if (result == 0) {
                throw new RuntimeException("댓글 삭제에 실패했습니다.");
            }

            log.info("댓글 삭제 완료 - commentNo: {}, postNo: {}", commentNo, postNo);
            return postNo;

        } catch (Exception e) {
            log.error("댓글 삭제 중 오류 발생", e);
            throw new RuntimeException("댓글 삭제 실패: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanCommentDto> getCommentsByPost(Long postNo, int page, int size) {
        log.info("게시글별 댓글 목록 조회 - postNo: {}, page: {}, size: {}", postNo, page, size);

        int offset = page * size;
        return commentMapper.selectCommentsByPostNo(postNo, offset, size);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCommentOwner(Long commentNo, Long userNo) {
        PlanCommentDto comment = commentMapper.selectCommentById(commentNo);
        return comment != null && comment.getUserNo().equals(userNo);
    }
}