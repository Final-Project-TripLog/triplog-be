package com.ssafy.triplog.attraction.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${file.upload.dir}")
    private String uploadDir;

    @Value("${file.access.url.prefix}")
    private String accessUrlPrefix;

    public String save(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("빈 파일입니다.");
        }

        String uuid = UUID.randomUUID().toString();
        String originalName = file.getOriginalFilename();
        String filename = uuid + "_" + originalName;

        String fullPath = uploadDir + filename;

        File dest = new File(fullPath);

        try {
            file.transferTo(dest);
        } catch (IOException e) {
            log.error("파일 저장 실패: {}", e.getMessage());
            throw new RuntimeException("파일 저장 실패");
        }

        log.info("파일 저장 완료: {}", fullPath);

        // 저장 후 접근 가능한 URL 반환
        return accessUrlPrefix + filename;
    }
}