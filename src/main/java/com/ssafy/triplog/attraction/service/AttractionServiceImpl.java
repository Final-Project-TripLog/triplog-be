package com.ssafy.triplog.attraction.service;

import com.ssafy.triplog.attraction.dto.AttractionDto;
import com.ssafy.triplog.attraction.dto.AttractionFileDto;
import com.ssafy.triplog.attraction.dto.AttractionImageDto;
import com.ssafy.triplog.attraction.dto.AttractionRequest;
import com.ssafy.triplog.attraction.repository.AttractionImageRepository;
import com.ssafy.triplog.attraction.repository.AttractionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttractionServiceImpl implements AttractionService {

    private final AttractionRepository attractionRepository;
    private final AttractionImageRepository attractionImageRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public Long createAttractionWithImages(AttractionRequest request) {
        // 1. AttractionDto 생성자 방식 매핑
        AttractionDto attractionDto = new AttractionDto(
                null, // no (PK) - DB에서 생성됨
                request.getTitle(),
                request.getOverview(),
                request.getMapLevel(),
                request.getLatitude(),
                request.getLongitude(),
                request.getTel(),
                request.getAddress(),
                request.getAddressDetail(),
                request.getHomepage(),
                request.getApiId(),
                request.getContentId(),
                request.getThumbnail(),
                request.getRating(),
                request.getGugunNo(),
                request.getSidoNo(),
                request.getAttractionTypeNo()
        );

        // 2. 관광지 저장
        attractionRepository.insertAttraction(attractionDto);
        Long attractionNo = attractionDto.getNo(); // DB에서 생성된 no

        // 3. 이미지 저장
        if (request.getImages() != null) {
            for (AttractionFileDto fileDto : request.getImages()) {
                String imageUrl = fileStorageService.save(fileDto.getMultipartFile());
                AttractionImageDto imageDto = new AttractionImageDto(
                        null,                          // 이미지 PK (auto)
                        imageUrl,         // 업로드 후 생성된 URL
                        fileDto.getReviewImageOrder(), // 정렬 순서
                        attractionNo,                  // FK: 등록된 관광지 no
                        null                           // 리뷰와는 무관
                );
                attractionImageRepository.insertImage(imageDto);
            }
//            for (int i = 0; i < request.getFiles().size(); i++) {
//                String imageUrl = fileStorageService.save(request.getFiles().get(i));
//                AttractionImageDto imageDto = new AttractionImageDto(
//                        null,                          // 이미지 PK (auto)
//                        imageUrl,         // 업로드 후 생성된 URL
//                        request.getReviewImageOrders().get(i), // 정렬 순서
//                        attractionNo,                  // FK: 등록된 관광지 no
//                        null                           // 리뷰와는 무관
//                );
//                attractionImageRepository.insertImage(imageDto);
//            }
        }

        return attractionNo;
    }
}