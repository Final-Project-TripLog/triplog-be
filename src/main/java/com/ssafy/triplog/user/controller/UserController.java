package com.ssafy.triplog.user.controller;

import com.ssafy.triplog.user.dto.*;
import com.ssafy.triplog.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User API", description = "사용자 관리 API")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:8080", allowedHeaders = "*")
public class UserController {

    private final UserService userService;

    @Operation(summary = "회원가입", description = "사용자 이메일과 비밀번호 등 기본 정보를 이용하여 회원가입을 처리합니다.")
    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody UserServiceDto request) {
        log.debug("signup -----> request : {} ", request);
        UserResponse response = userService.registerUser(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원탈퇴", description = "인증된 사용자가 자신의 계정을 탈퇴합니다.")
    @DeleteMapping("/{userNo}")
    public ResponseEntity<String> withdrawUser(@PathVariable Long userNo, @RequestBody String password) {
        log.debug("withdrawUser -----> userNo : {}", userNo);

        // 실제 애플리케이션에서는 토큰에서 사용자 ID를 추출하여 사용해야 함
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        // Long authenticatedUserNo = userDetails.getUserNo();

        boolean result = userService.withdrawUser(userNo, password);

        if (result) {
            return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("회원 탈퇴 처리 중 오류가 발생했습니다.");
        }
    }

    // 로그인 API는 LoginFilter에서 처리되므로 컨트롤러에서는 기본 응답만 제공
    @Operation(summary = "로컬 로그인", description = "로컬 디비를 바탕으로 로그인 처리를 합니다.")
    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody UserLoginRequest request) {
        log.debug("login -----> request : {} ", request);
        // 실제 인증은 LoginFilter에서 처리됨
        return ResponseEntity.ok(new UserResponse());
    }

    @Operation(summary = "소셜 로그인", description = "OAuth2 인증 코드를 이용하여 소셜 로그인 처리를 합니다.")
    @PostMapping("/login/{provider}")
    public ResponseEntity<UserResponse> socialLogin(
            @PathVariable String provider,
            @Valid @RequestBody UserSocialLoginRequest request) {
        log.debug("socialLogin -----> provider: {}, request : {} ", provider, request);

        UserResponse response = userService.handleSocialLogin(provider.toLowerCase(), request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원정보 수정", description = "로그인한 사용자가 자신의 회원 정보를 수정합니다.")
    @PutMapping("/{userNo}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long userNo,
            @Valid @RequestBody UserDto request) {
        log.debug("updateUser -----> userNo: {}, request : {} ", userNo, request);

        // 실제 애플리케이션에서는 토큰에서 사용자 ID를 추출하여 사용자 본인 확인 필요
        UserResponse response = userService.updateUser(userNo, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "아이디(이메일) 찾기", description = "전화번호, 이름 등의 정보를 입력받아 등록된 이메일을 찾습니다.")
    @PostMapping("/find-email")
    public ResponseEntity<UserFindEmailResponse> findEmail(@Valid @RequestBody UserFindEmailRequest request) {
        log.debug("findEmail -----> request : {} ", request);
        UserFindEmailResponse response = userService.findUserEmail(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "비밀번호 찾기", description = "등록된 이메일을 기반으로 임시 비밀번호를 전송합니다.")
    @PostMapping("/find-password")
    public ResponseEntity<String> findPassword(@Valid @RequestBody UserFindPasswordRequest request) {
        log.debug("findPassword -----> request : {} ", request);

        // 실제 애플리케이션에서는 임시 비밀번호를 이메일로 전송하고 성공 메시지만 반환
        // 테스트 용도로 임시 비밀번호를 응답으로 반환
        String temporaryPassword = userService.findUserPassword(request);

        return ResponseEntity.ok("임시 비밀번호가 발급되었습니다: " + temporaryPassword);
    }

    @Operation(summary = "회원 정보 조회", description = "지정한 회원 번호에 해당하는 회원의 상세 정보를 조회합니다.")
    @GetMapping("/{userNo}")
    public ResponseEntity<UserServiceDto> getUserById(@PathVariable Long userNo) {
        log.debug("getUserById -----> userNo : {} ", userNo);
        UserServiceDto userDto = userService.getUserDetail(userNo);
        return ResponseEntity.ok(userDto);
    }

    @Operation(summary = "나의 팔로워 조회", description = "userNo 팔로워 list 를 조회합니다.")
    @GetMapping("/{userNo}/follower")
    public ResponseEntity<List<UserFollowInfoResponse>> getAllFollowers(@PathVariable Long userNo) {
        log.debug("getAllFollowers -----> userNo : {} ", userNo);
        List<UserFollowInfoResponse> followers = userService.getUserFollowers(userNo);
        return ResponseEntity.ok(followers);
    }

    @Operation(summary = "나의 팔로우 조회", description = "userNo 팔로우 list 를 조회합니다.")
    @GetMapping("/{userNo}/follow")
    public ResponseEntity<List<UserFollowInfoResponse>> getAllFollows(@PathVariable Long userNo) {
        log.debug("getAllFollows -----> userNo : {} ", userNo);
        List<UserFollowInfoResponse> following = userService.getUserFollowing(userNo);
        return ResponseEntity.ok(following);
    }

    @Operation(summary = "사용자 팔로우", description = "특정 사용자를 팔로우합니다.")
    @PostMapping("/{userNo}/follow/{targetUserNo}")
    public ResponseEntity<String> followUser(
            @PathVariable Long userNo,
            @PathVariable Long targetUserNo) {
        log.debug("followUser -----> userNo: {}, targetUserNo: {}", userNo, targetUserNo);

        boolean result = userService.followUser(userNo, targetUserNo);

        if (result) {
            return ResponseEntity.ok("팔로우가 완료되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("팔로우 처리 중 오류가 발생했습니다.");
        }
    }

    @Operation(summary = "사용자 언팔로우", description = "특정 사용자를 언팔로우합니다.")
    @DeleteMapping("/{userNo}/follow/{targetUserNo}")
    public ResponseEntity<String> unfollowUser(
            @PathVariable Long userNo,
            @PathVariable Long targetUserNo) {
        log.debug("unfollowUser -----> userNo: {}, targetUserNo: {}", userNo, targetUserNo);

        boolean result = userService.unfollowUser(userNo, targetUserNo);

        if (result) {
            return ResponseEntity.ok("언팔로우가 완료되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("언팔로우 처리 중 오류가 발생했습니다.");
        }
    }
}