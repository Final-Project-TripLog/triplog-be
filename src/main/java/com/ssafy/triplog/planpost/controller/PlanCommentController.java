package com.ssafy.triplog.planpost.controller;

import com.ssafy.triplog.planpost.dto.PlanCommentDto;
import com.ssafy.triplog.planpost.dto.PlanCommentRequestDto;
import com.ssafy.triplog.planpost.service.PlanCommentService;
import com.ssafy.triplog.security.util.AuthenticationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Plan_Comment_Controller", description = "여행 계획 게시글 댓글 관련 API")
@RestController
@RequestMapping("/api/plan-comments")
@RequiredArgsConstructor
@Slf4j
public class PlanCommentController {

    private final PlanCommentService commentService;
    private final AuthenticationUtil authenticationUtil;

    @Operation(summary = "댓글 등록", description = "여행 계획 게시글에 댓글을 등록합니다. --> 리턴값 : 계획 게시글 no")
    @PostMapping
    public ResponseEntity<Long> createComment(@Valid @RequestBody PlanCommentRequestDto requestDto) {
        log.debug("createComment -----> request : {}", requestDto);

        // RequestDto를 내부 DTO로 변환
        PlanCommentDto commentDto = new PlanCommentDto();
        commentDto.setContent(requestDto.getContent());
        commentDto.setPlanPostNo(requestDto.getPlanPostNo());
        commentDto.setParentNo(requestDto.getParentNo());

        // JWT에서 현재 사용자 정보 추출
        Long userNo = authenticationUtil.getCurrentUserNo();
        String userNickname = authenticationUtil.getCurrentUserNickname();

        // 사용자 정보 설정
        commentDto.setUserNo(userNo);
        commentDto.setUserNickname(userNickname);

        Long postNo = commentService.createComment(commentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(postNo);
    }

    @Operation(summary = "댓글 삭제", description = "여행 계획 게시글의 특정 댓글을 삭제합니다. --> 리턴값 : 계획 게시글 no")
    @DeleteMapping("/{commentNo}")
    public ResponseEntity<Long> deleteComment(@PathVariable Long commentNo) {
        log.debug("deleteComment -----> commentNo : {}", commentNo);

        // JWT에서 현재 사용자 정보 추출
        Long userNo = authenticationUtil.getCurrentUserNo();

        // 권한 확인
        if (!commentService.isCommentOwner(commentNo, userNo) && !authenticationUtil.isCurrentUserAdmin()) {
            log.warn("댓글 삭제 권한 없음 - commentNo: {}, userNo: {}", commentNo, userNo);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Long postNo = commentService.deleteComment(commentNo);
        return ResponseEntity.ok(postNo);
    }

    @Operation(summary = "댓글 수정", description = "여행 계획 게시글의 특정 댓글 내용을 수정합니다. --> 리턴값 : 계획 게시글 no")
    @PutMapping("/{commentNo}")
    public ResponseEntity<Long> updateComment(
            @PathVariable Long commentNo,
            @RequestBody @NotBlank @Size(min = 1, max = 1000) String content) {

        log.debug("updateComment -----> commentNo: {}, content: {}", commentNo, content);

        // JWT에서 현재 사용자 정보 추출
        Long userNo = authenticationUtil.getCurrentUserNo();

        // 권한 확인
        if (!commentService.isCommentOwner(commentNo, userNo)) {
            log.warn("댓글 수정 권한 없음 - commentNo: {}, userNo: {}", commentNo, userNo);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // 업데이트를 위한 DTO 생성
        PlanCommentDto updateDto = new PlanCommentDto();
        updateDto.setContent(content);

        Long postNo = commentService.updateComment(commentNo, updateDto);
        return ResponseEntity.ok(postNo);
    }

    @Operation(summary = "댓글 목록 조회", description = "여행 계획 게시글의 전체 댓글 목록을 조회합니다.")
    @GetMapping("/post/{postNo}")
    public ResponseEntity<List<PlanCommentDto>> getCommentsByPost(@PathVariable Long postNo,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int size) {
        log.debug("getCommentsByPost -----> postNo : {}, page : {}, size : {}", postNo, page, size);

        List<PlanCommentDto> comments = commentService.getCommentsByPost(postNo, page, size);
        return ResponseEntity.ok(comments);
    }
}