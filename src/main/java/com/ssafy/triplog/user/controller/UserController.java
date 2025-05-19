package com.ssafy.triplog.user.controller;

//회원가입	POST	/api/users/signup
//회원탈퇴	DELETE	/api/users
//로그인 (로컬)	POST	/api/users/login
//로그인 (소셜)	POST	/api/users/login/{google, kakao}
//회원정보 수정	PUT	/api/users
//회원 전체 조회 (Admin)	GET	/api/admin/users
//아이디(이메일) 찾기	POST	/api/users/find-email
//비밀번호 찾기	POST	/api/users/find-password
//특정 회원 정보 조회	GET	/api/users/{userNo}

import com.ssafy.triplog.user.dto.*;
import com.ssafy.triplog.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @Operation(summary = "회원가입", description = "사용자 이메일과 비밀번호 등 기본 정보를 이용하여 회원가입을 처리합니다.")
    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody UserServiceDto request) {
        log.debug("signup -----> request : {} ", request);
        UserResponse response = userService.registerUser(request);
        return ResponseEntity.ok(response);
    }

//    @Operation(summary = "회원가입", description = "사용자 이메일과 비밀번호 등 기본 정보를 이용하여 회원가입을 처리합니다.")
//    @PostMapping("/signup")
//    public ResponseEntity<UserResponse> signup(@Valid @RequestBody UserServiceDto request) {
//        log.debug("signup -----> request : {} ", request);
//        return ResponseEntity.ok(new UserResponse());
//    }


    @Operation(summary = "회원탈퇴", description = "인증된 사용자가 자신의 계정을 탈퇴합니다. no는 토큰 사용 시 삭제될 예정입니다. ")
    @DeleteMapping()
    public ResponseEntity<String> withdrawUser(@Valid String password, Long no) {
        log.debug("withdrawUser -----> password : {} ", password);
        log.debug("withdrawUser -----> no : {} ", no);
        return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
    }

    @Operation(summary = "로컬 로그인", description = "로컬 디비를 바탕으로 로그인 처리를 합니다.")
    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody UserLoginRequest request) {
        log.debug("login -----> request : {} ", request);
        return ResponseEntity.ok(new UserResponse());
    }


    //4. 소셜 로그인 : POST : /api/users/login/{provider}
//   → summary: "소셜 로그인"
//            → description: "OAuth2 인증 코드를 이용하여 소셜 로그인 처리를 합니다. (provider: google, kakao, naver)"
//            → method name: socialLogin

    // 인풋값 정리할 필요 있어 보임!!! 어떤 로직인지 확인해보고 결정해야할 듯
    ////////이거 나중에 해야지이이이이
    @Operation(summary = "소셜 로그인", description = "OAuth2 인증 코드를 이용하여 소셜 로그인 처리를 합니다. (provider: google, kakao, naver)" +
            "인풋값 정리할 필요 있어 보임!!! 어떤 로직인지 확인해보고 결정해야할 듯"
    )
    @PostMapping("/login/google")
    public ResponseEntity<UserResponse> googleSocialLogin(@Valid @RequestBody UserSocialLoginRequest request) {
        log.debug("googleSocialLogin -----> request : {} ", request);
        return ResponseEntity.ok(new UserResponse());
    }

    @Operation(summary = "회원정보 수정", description = "로그인한 사용자가 자신의 회원 정보를 수정합니다.")
    @PutMapping("/{userNo}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long userNo, @Valid @RequestBody UserDto request) {
        log.debug("updateUser -----> request : {} ", request);
        return ResponseEntity.ok(new UserResponse());
    }


    //6. 회원 전체 조회 (어드민 전용) : GET : /api/admin/users //기능 없음
//   → summary: "전체 회원 조회"
//            → description: "관리자 권한으로 모든 회원 목록을 조회합니다."
//            → method name: getAllUsers


    @Operation(summary = "아이디(이메일) 찾기", description = "전화번호, 이름 등의 정보를 입력받아 등록된 이메일을 찾습니다.")
    @PostMapping("/find-email")
    public ResponseEntity<UserFindEmailResponse> findEmail(@Valid @RequestBody UserFindEmailRequest request) {
        log.debug("findEmail -----> request : {} ", request);
        return ResponseEntity.ok(new UserFindEmailResponse());
    }

    @Operation(summary = "비밀번호 찾기", description = "등록된 이메일을 기반으로 임시 비밀번호를 전송합니다.")
    @PostMapping("/find-password")
    public ResponseEntity<String> findPassword(@Valid @RequestBody UserFindPasswordRequest request) {
        log.debug("findPassword -----> request : {} ", request);
        return ResponseEntity.ok("임시 비밀번호 전송이 완료되었습니다. ");
    }

    @Operation(summary = "회원 정보 조회", description = "지정한 회원 번호에 해당하는 회원의 상세 정보를 조회합니다.")
    @GetMapping("/{userNo}")
    public ResponseEntity<UserServiceDto> getUserById(@PathVariable Long userNo) {
        log.debug("getUserById -----> userNo : {} ", userNo);
        return ResponseEntity.ok(new UserServiceDto());
    }

    @Operation(summary = "나의 팔로워 조회", description = "userNo 팔로워 list 를 조회합니다. ")
    @GetMapping("/{userNo}/follower")
    public ResponseEntity<List<UserFollowInfoResponse>> getAllFollowers(@PathVariable Long userNo) {
        log.debug("getAllFollowers -----> userNo : {} ", userNo);
        return ResponseEntity.ok(List.of(new UserFollowInfoResponse(), new UserFollowInfoResponse()));
    }

    @Operation(summary = "나의 팔로우 조회", description = "userNo 팔로우 list 를 조회합니다. ")
    @GetMapping("/{userNo}/follow")
    public ResponseEntity<List<UserFollowInfoResponse>> getAllFollows(@PathVariable Long userNo) {
        log.debug("getAllFollowers -----> userNo : {} ", userNo);
        return ResponseEntity.ok(List.of(new UserFollowInfoResponse(), new UserFollowInfoResponse()));
    }


//    @Operation(summary = "소셜 로그인", description = "OAuth2 인증 코드를 이용하여 소셜 로그인 처리를 합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "로그인 성공",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = LoginResponse.class))),
//            @ApiResponse(responseCode = "401", description = "OAuth 인증 실패 (잘못된 인가 코드)",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "409", description = "이미 가입된 번호",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "500", description = "서버 오류 발생",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = ErrorResponse.class)))
//    })
//    @PostMapping("/oauth")
//    public ResponseEntity<LoginResponse> socialLogin(@Valid @RequestBody OAuthRequest request) {
//        LoginResponse loginResponse = authService.socialLogin(request);
//        return ResponseEntity.ok(loginResponse);
//    }
}



