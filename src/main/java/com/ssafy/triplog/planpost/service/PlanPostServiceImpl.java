package com.ssafy.triplog.planpost.service;
import com.ssafy.triplog.planpost.service.PlanPostService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ssafy.triplog.myplan.dto.MyDailyPlanDto;
import com.ssafy.triplog.myplan.dto.MyPlanDto;
import com.ssafy.triplog.myplan.service.MyPlanService;
import com.ssafy.triplog.planpost.controller.PlanPostController.PlanPostFromMyPlanRequest;
import com.ssafy.triplog.planpost.dto.*;
import com.ssafy.triplog.planpost.mapper.PlanPostMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 여행 계획 게시글 서비스 구현체
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PlanPostServiceImpl implements PlanPostService {

    private final PlanPostMapper planPostMapper;
    private final MyPlanService myPlanService;

    // ===== MyPlan 기반 게시글 생성 =====

    @Override
    public Long createPlanPostFromMyPlan(Long myPlanNo, Long userNo, String userNickname,
                                         PlanPostFromMyPlanRequest request) {
        log.info("MyPlan으로부터 게시글 생성 시작 - myPlanNo: {}, userNo: {}", myPlanNo, userNo);

        try {
            // 1. MyPlan 정보 조회
            List<MyDailyPlanDto> dailyPlans = myPlanService.getMyPlanDetail(myPlanNo, userNo);
            if (dailyPlans.isEmpty()) {
                throw new RuntimeException("개인 여행 계획의 상세 정보를 찾을 수 없습니다.");
            }

            // MyPlan 기본 정보도 필요하므로 별도 조회 (MyPlanService에 메서드 추가 필요)
            // 임시로 dailyPlans에서 기본 정보 추출
            MyDailyPlanDto firstPlan = dailyPlans.get(0);

            // 2. MyPlan → PlanPost 변환
            PlanPostDto planPostDto = convertMyPlanToPlanPost(dailyPlans, userNo, userNickname, request);

            // 3. 게시글 저장
            int result = planPostMapper.insertPlanPost(planPostDto);
            if (result == 0 || planPostDto.getNo() == null) {
                throw new RuntimeException("게시글 저장에 실패했습니다.");
            }

            Long postNo = planPostDto.getNo();
            log.info("게시글 기본 정보 저장 완료 - postNo: {}", postNo);

            // 4. 관광지 세부 계획 변환 및 저장
            List<PlanAttractionDetailDto> attractionDetails = convertDailyPlansToAttractionDetails(dailyPlans, postNo);
            if (!attractionDetails.isEmpty()) {
                planPostMapper.insertPlanAttractionDetails(attractionDetails);
                log.info("관광지 세부 계획 {} 개 저장 완료", attractionDetails.size());
            }

            // 5. 태그 저장
            if (request.getTags() != null && !request.getTags().isEmpty()) {
                List<PlanPostTagDto> tags = request.getTags().stream()
                        .filter(StringUtils::hasText)
                        .map(tagName -> {
                            PlanPostTagDto tag = new PlanPostTagDto();
                            tag.setPlanPostNo(postNo);
                            tag.setName(tagName.trim());
                            return tag;
                        })
                        .collect(Collectors.toList());

                if (!tags.isEmpty()) {
                    planPostMapper.insertPlanPostTags(tags);
                    log.info("태그 {} 개 저장 완료", tags.size());
                }
            }

            log.info("MyPlan으로부터 게시글 생성 완료 - postNo: {}", postNo);
            return postNo;

        } catch (Exception e) {
            log.error("MyPlan으로부터 게시글 생성 중 오류 발생", e);
            throw new RuntimeException("게시글 생성 실패: " + e.getMessage(), e);
        }
    }

    /**
     * MyPlan 데이터를 PlanPost로 변환
     */
    private PlanPostDto convertMyPlanToPlanPost(List<MyDailyPlanDto> dailyPlans, Long userNo,
                                                String userNickname, PlanPostFromMyPlanRequest request) {
        PlanPostDto planPost = new PlanPostDto();

        planPost.setUserNo(userNo);
        planPost.setUserNickname(userNickname);
        planPost.setTitle(request.getTitle());
        planPost.setDescription(request.getDescription());
        planPost.setThumbnail(request.getThumbnail());

        // 여행 시작일/종료일 계산
        if (!dailyPlans.isEmpty()) {
            LocalDateTime startDay = dailyPlans.stream()
                    .map(MyDailyPlanDto::getVisitedDate)
                    .min(java.time.LocalDate::compareTo)
                    .map(date -> date.atStartOfDay())
                    .orElse(LocalDateTime.now());

            LocalDateTime endDay = dailyPlans.stream()
                    .map(MyDailyPlanDto::getVisitedDate)
                    .max(java.time.LocalDate::compareTo)
                    .map(date -> date.atTime(23, 59, 59))
                    .orElse(LocalDateTime.now());

            planPost.setStartDay(startDay);
            planPost.setEndDay(endDay);
        }

        // 초기값 설정
        planPost.setForkCount(0);
        planPost.setLikedCount(0);
        planPost.setViewCount(0);
        planPost.setTotalMember(1L); // 기본값

        return planPost;
    }

    /**
     * MyDailyPlan을 PlanAttractionDetail로 변환
     */
    private List<PlanAttractionDetailDto> convertDailyPlansToAttractionDetails(List<MyDailyPlanDto> dailyPlans, Long postNo) {
        return dailyPlans.stream().map(dailyPlan -> {
            PlanAttractionDetailDto detail = new PlanAttractionDetailDto();

            detail.setPlanPostNo(postNo);
            detail.setVisitDate(dailyPlan.getVisitedDate());
            detail.setStartTime(dailyPlan.getStartTime());
            detail.setEndTime(dailyPlan.getEndTime());
            detail.setMoveTime(dailyPlan.getMoveTime() != null ? dailyPlan.getMoveTime().intValue() : null);
            detail.setAttractionTitle(dailyPlan.getAttractionTitle());
            detail.setAttractionThumbnail(dailyPlan.getAttractionThumbnail());
            detail.setAttractionRating(dailyPlan.getAttractionRating());
            detail.setAttractionNo(dailyPlan.getAttractionNo());
            detail.setWriterRating(null); // MyPlan에는 작성자 평점이 없음
            detail.setReviewNo(null);

            return detail;
        }).collect(Collectors.toList());
    }

    // ===== 일반 CRUD =====

    @Override
    public Long createPlanPost(PlanPostRequest request) {
        log.info("게시글 직접 생성 - 제목: {}, 사용자: {}", request.getTitle(), request.getUserNickname());

        try {
            // 1. PlanPostRequest → PlanPostDto 변환
            PlanPostDto planPostDto = convertRequestToDto(request);

            // 2. 게시글 저장
            int result = planPostMapper.insertPlanPost(planPostDto);
            if (result == 0 || planPostDto.getNo() == null) {
                throw new RuntimeException("게시글 저장에 실패했습니다.");
            }

            Long postNo = planPostDto.getNo();
            log.info("게시글 저장 완료 - postNo: {}", postNo);

            // 3. 관광지 세부 계획 저장
            if (request.getAttractions() != null && !request.getAttractions().isEmpty()) {
                for (PlanAttractionDetailDto attraction : request.getAttractions()) {
                    attraction.setPlanPostNo(postNo);
                }
                planPostMapper.insertPlanAttractionDetails(request.getAttractions());
                log.info("관광지 세부 계획 {} 개 저장 완료", request.getAttractions().size());
            }

            return postNo;

        } catch (Exception e) {
            log.error("게시글 생성 중 오류 발생", e);
            throw new RuntimeException("게시글 생성 실패: " + e.getMessage(), e);
        }
    }

    private PlanPostDto convertRequestToDto(PlanPostRequest request) {
        PlanPostDto dto = new PlanPostDto();

        dto.setNo(request.getNo());
        dto.setUserNo(request.getUserNo());
        dto.setUserNickname(request.getUserNickname());
        dto.setTitle(request.getTitle());
        dto.setDescription(request.getDescription());
        dto.setThumbnail(request.getThumbnail());
        dto.setStartDay(request.getStartDay());
        dto.setEndDay(request.getEndDay());
        dto.setTotalMember(request.getTotalMember());

        // 초기값 설정 (신규 생성 시)
        if (request.getNo() == null) {
            dto.setForkCount(0);
            dto.setLikedCount(0);
            dto.setViewCount(0);
        } else {
            dto.setForkCount(request.getForkCount());
            dto.setLikedCount(request.getLikedCount());
            dto.setViewCount(request.getViewCount());
        }

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public PlanPostResponse getPlanPostById(Long postNo) {
        log.info("게시글 조회 - postNo: {}", postNo);

        PlanPostDto planPost = planPostMapper.selectPlanPostById(postNo);
        if (planPost == null) {
            throw new RuntimeException("게시글을 찾을 수 없습니다. postNo: " + postNo);
        }

        return convertDtoToResponse(planPost, postNo);
    }

    private PlanPostResponse convertDtoToResponse(PlanPostDto dto, Long postNo) {
        PlanPostResponse response = new PlanPostResponse();

        response.setNo(dto.getNo());
        response.setUserNo(dto.getUserNo());
        response.setUserNickname(dto.getUserNickname());
        response.setTitle(dto.getTitle());
        response.setDescription(dto.getDescription());
        response.setThumbnail(dto.getThumbnail());
        response.setCreatedAt(dto.getCreatedAt());
        response.setUpdatedAt(dto.getUpdatedAt());
        response.setStartDay(dto.getStartDay());
        response.setEndDay(dto.getEndDay());
        response.setTotalMember(dto.getTotalMember());
        response.setForkCount(dto.getForkCount());
        response.setLikedCount(dto.getLikedCount());
        response.setViewCount(dto.getViewCount());

        // 태그 목록 조회
        List<PlanPostTagDto> tags = planPostMapper.selectPlanPostTagsByPostNo(postNo);
        response.setTags(tags != null ? tags : Collections.emptyList());

        return response;
    }

    @Override
    public Long updatePlanPost(PlanPostRequest request) {
        log.info("게시글 수정 - postNo: {}", request.getNo());

        try {
            PlanPostDto planPostDto = convertRequestToDto(request);

            int result = planPostMapper.updatePlanPost(planPostDto);
            if (result == 0) {
                throw new RuntimeException("게시글을 찾을 수 없습니다.");
            }

            // 관광지 세부 계획 수정 (기존 삭제 후 재생성)
            if (request.getAttractions() != null) {
                planPostMapper.deletePlanAttractionDetailsByPostNo(request.getNo());

                if (!request.getAttractions().isEmpty()) {
                    for (PlanAttractionDetailDto attraction : request.getAttractions()) {
                        attraction.setPlanPostNo(request.getNo());
                    }
                    planPostMapper.insertPlanAttractionDetails(request.getAttractions());
                }
            }

            log.info("게시글 수정 완료 - postNo: {}", request.getNo());
            return request.getNo();

        } catch (Exception e) {
            log.error("게시글 수정 중 오류 발생", e);
            throw new RuntimeException("게시글 수정 실패: " + e.getMessage(), e);
        }
    }

    @Override
    public void deletePlanPost(Long postNo) {
        log.info("게시글 삭제 - postNo: {}", postNo);

        try {
            // 관련 데이터 삭제
            planPostMapper.deletePlanAttractionDetailsByPostNo(postNo);
            planPostMapper.deletePlanPostTagsByPostNo(postNo);

            // 게시글 삭제
            int result = planPostMapper.deletePlanPost(postNo);
            if (result == 0) {
                throw new RuntimeException("게시글을 찾을 수 없습니다.");
            }

            log.info("게시글 삭제 완료 - postNo: {}", postNo);

        } catch (Exception e) {
            log.error("게시글 삭제 중 오류 발생", e);
            throw new RuntimeException("게시글 삭제 실패: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPostOwner(Long postNo, Long userNo) {
        PlanPostDto planPost = planPostMapper.selectPlanPostById(postNo);
        return planPost != null && planPost.getUserNo().equals(userNo);
    }

    // ===== 조회 및 검색 =====

    @Override
    @Transactional(readOnly = true)
    public List<PlanPostResponse> getPlanPostList(String sortBy, int page, int size) {
        log.info("게시글 목록 조회 - sortBy: {}, page: {}, size: {}", sortBy, page, size);

        int offset = page * size;
        List<PlanPostDto> posts = planPostMapper.selectPlanPostList(sortBy, offset, size);

        return posts.stream()
                .map(dto -> convertDtoToResponse(dto, dto.getNo()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanPostResponse> searchPlanPostsByLocation(Long sidoNo, Long gugunNo, int page, int size) {
        log.info("장소별 게시글 검색 - sidoNo: {}, gugunNo: {}, page: {}, size: {}", sidoNo, gugunNo, page, size);

        int offset = page * size;
        List<PlanPostDto> posts = planPostMapper.searchPlanPostsByLocation(sidoNo, gugunNo, offset, size);

        return posts.stream()
                .map(dto -> convertDtoToResponse(dto, dto.getNo()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanPostResponse> searchPlanPostsByKeyword(String keyword, Long sidoNo, Long gugunNo, int page, int size) {
        log.info("키워드 게시글 검색 - keyword: {}, page: {}, size: {}", keyword, page, size);

        int offset = page * size;
        List<PlanPostDto> posts = planPostMapper.searchPlanPostsByKeyword(keyword, sidoNo, gugunNo, offset, size);

        return posts.stream()
                .map(dto -> convertDtoToResponse(dto, dto.getNo()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanPostResponse> getPlanPostsByUser(Long userNo, int page, int size) {
        log.info("사용자별 게시글 조회 - userNo: {}, page: {}, size: {}", userNo, page, size);

        int offset = page * size;
        List<PlanPostDto> posts = planPostMapper.selectPlanPostsByUser(userNo, offset, size);

        return posts.stream()
                .map(dto -> convertDtoToResponse(dto, dto.getNo()))
                .collect(Collectors.toList());
    }

    // ===== 좋아요 기능 =====

    @Override
    public void likePlanPost(Long postNo, Long userNo) {
        log.info("좋아요 등록 - postNo: {}, userNo: {}", postNo, userNo);

        // 이미 좋아요 했는지 확인
        if (planPostMapper.existsLikePost(postNo, userNo) > 0) {
            throw new RuntimeException("이미 좋아요를 누른 게시글입니다.");
        }

        try {
            LikePostDto likePost = new LikePostDto();
            likePost.setPlanPostNo(postNo);
            likePost.setUserNo(userNo);
            likePost.setLikedAt(LocalDateTime.now());

            planPostMapper.insertLikePost(likePost);
            planPostMapper.increaseLikedCount(postNo);

            log.info("좋아요 등록 완료 - postNo: {}", postNo);

        } catch (Exception e) {
            log.error("좋아요 등록 중 오류 발생", e);
            throw new RuntimeException("좋아요 등록 실패: " + e.getMessage(), e);
        }
    }

    @Override
    public void unlikePlanPost(Long postNo, Long userNo) {
        log.info("좋아요 취소 - postNo: {}, userNo: {}", postNo, userNo);

        try {
            int result = planPostMapper.deleteLikePost(postNo, userNo);
            if (result > 0) {
                planPostMapper.decreaseLikedCount(postNo);
                log.info("좋아요 취소 완료 - postNo: {}", postNo);
            } else {
                throw new RuntimeException("좋아요 기록을 찾을 수 없습니다.");
            }

        } catch (Exception e) {
            log.error("좋아요 취소 중 오류 발생", e);
            throw new RuntimeException("좋아요 취소 실패: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isLikedByUser(Long postNo, Long userNo) {
        return planPostMapper.existsLikePost(postNo, userNo) > 0;
    }

    // ===== 기타 =====

    @Override
    public void increaseViewCount(Long postNo) {
        planPostMapper.increaseViewCount(postNo);
    }
}