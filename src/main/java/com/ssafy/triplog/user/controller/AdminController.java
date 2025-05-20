package com.ssafy.triplog.user.controller;

import com.ssafy.triplog.user.dto.UserServiceDto;
import com.ssafy.triplog.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin API", description = "관리자 전용 API")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final UserService userService;

    @Operation(summary = "전체 회원 조회", description = "관리자 권한으로 모든 회원 목록을 조회합니다.")
    @GetMapping("/users")
    public ResponseEntity<List<UserServiceDto>> getAllUsers() {
        log.debug("getAllUsers ----->");
        List<UserServiceDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}