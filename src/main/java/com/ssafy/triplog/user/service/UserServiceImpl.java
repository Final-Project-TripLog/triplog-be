// src/main/java/com/ssafy/triplog/user/service/UserServiceImpl.java
package com.ssafy.triplog.user.service;

import com.ssafy.triplog.user.dto.*;
import com.ssafy.triplog.user.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, BCryptPasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원가입
    @Override
    public UserResponse registerUser(UserServiceDto userServiceDto) {
        // 이메일 중복 확인
        if (userMapper.existsByEmail(userServiceDto.getEmail()) > 0) {
            throw new RuntimeException("이미 등록된 이메일입니다.");
        }

        // 비밀번호 암호화
        userServiceDto.setPassword(passwordEncoder.encode(userServiceDto.getPassword()));

        // 기본 권한 설정
        userServiceDto.setRole("ROLE_USER");

        // 소셜 타입 설정
        if (userServiceDto.getSocialType() == null) {
            userServiceDto.setSocialType("LOCAL");
        }

        // 사용자 DB에 저장
        userMapper.save(userServiceDto);
        Long userNo = userMapper.getLastInsertId();

        // 응답 객체 생성
        UserResponse response = new UserResponse();
        response.setNo(userNo);
        response.setNickname(userServiceDto.getNickname());
        response.setProfileUrl(userServiceDto.getProfileUrl());

        return response;
    }
    // 이메일 중복 확인 (추가)
    @Override
    public boolean checkEmailDuplicate(String email) {
        return userMapper.existsByEmail(email) > 0;
    }

    // 닉네임 중복 확인 (추가)
    @Override
    public boolean checkNicknameDuplicate(String nickname) {
        return userMapper.existsByNickname(nickname) > 0;
    }
    // 사용자 상세 정보 조회
    @Override
    public UserServiceDto getUserDetail(Long userNo) {
        UserDto userDto = userMapper.findById(userNo);
        if (userDto == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다: " + userNo);
        }

        // UserDto를 UserServiceDto로 변환
        UserServiceDto userServiceDto = new UserServiceDto();
        userServiceDto.setEmail(userDto.getEmail());
        userServiceDto.setNickname(userDto.getNickname());
        userServiceDto.setName(userDto.getName());
        userServiceDto.setProfileUrl(userDto.getProfileUrl());
        userServiceDto.setPhone(userDto.getPhone());
        userServiceDto.setAddress(userDto.getAddress());
        userServiceDto.setAddressDetail(userDto.getAddressDetail());
        userServiceDto.setFollowCount(userDto.getFollowCount());
        userServiceDto.setFollowerCount(userDto.getFollowerCount());
        userServiceDto.setRole(userDto.getRole());
        userServiceDto.setSocialType(userDto.getSocialType());

        return userServiceDto;
    }

    // 회원 탈퇴
    @Override
    @Transactional
    public boolean withdrawUser(Long userNo, String password) {
        // 현재 사용자의 비밀번호 확인
        UserDto userDto = userMapper.findById(userNo);
        if (userDto == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        // 비밀번호 검증
        if (!passwordEncoder.matches(password, userDto.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // 사용자 삭제
        return userMapper.deleteUser(userNo);
    }

    // 회원 정보 수정
    @Override
    @Transactional
    public UserResponse updateUser(Long userNo, UserDto userDto) {
        // 사용자 존재 여부 확인
        UserDto existingUser = userMapper.findById(userNo);
        if (existingUser == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        // 비밀번호가 제공된 경우 암호화
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        // 사용자 ID 설정
        userDto.setNo(userNo);

        // 사용자 정보 업데이트
        userMapper.updateUser(userDto);

        // 업데이트된 사용자 정보로 응답 생성
        UserResponse response = new UserResponse();
        response.setNo(userNo);
        response.setNickname(userDto.getNickname());
        response.setProfileUrl(userDto.getProfileUrl());

        return response;
    }

    // 모든 회원 조회 (관리자)
    @Override
    public List<UserServiceDto> getAllUsers() {
        List<UserDto> userDtoList = userMapper.findAllUsers();
        List<UserServiceDto> result = new ArrayList<>();

        for (UserDto userDto : userDtoList) {
            UserServiceDto userServiceDto = new UserServiceDto();
            userServiceDto.setEmail(userDto.getEmail());
            userServiceDto.setNickname(userDto.getNickname());
            userServiceDto.setName(userDto.getName());
            userServiceDto.setProfileUrl(userDto.getProfileUrl());
            userServiceDto.setPhone(userDto.getPhone());
            userServiceDto.setAddress(userDto.getAddress());
            userServiceDto.setAddressDetail(userDto.getAddressDetail());
            userServiceDto.setFollowCount(userDto.getFollowCount());
            userServiceDto.setFollowerCount(userDto.getFollowerCount());
            userServiceDto.setRole(userDto.getRole());
            userServiceDto.setSocialType(userDto.getSocialType());

            result.add(userServiceDto);
        }

        return result;
    }

    // 아이디(이메일) 찾기
    @Override
    public UserFindEmailResponse findUserEmail(UserFindEmailRequest request) {
        UserDto userDto = userMapper.findByNameAndPhone(request.getName(), request.getPhone());
        if (userDto == null) {
            throw new RuntimeException("해당 정보로 등록된 사용자를 찾을 수 없습니다.");
        }

        UserFindEmailResponse response = new UserFindEmailResponse();
        response.setNo(userDto.getNo());
        response.setEmail(userDto.getEmail());

        return response;
    }

    // 비밀번호 찾기 (임시 비밀번호 발급)
    @Override
    @Transactional
    public String findUserPassword(UserFindPasswordRequest request) {
        // 사용자 정보 확인
        UserDto userDto = userMapper.findByEmailAndNameAndPhone(
                request.getEmail(), request.getName(), request.getPhone());

        if (userDto == null) {
            throw new RuntimeException("해당 정보로 등록된 사용자를 찾을 수 없습니다.");
        }

        // 임시 비밀번호 생성 (8자리 랜덤 문자열)
        String temporaryPassword = generateTemporaryPassword();

        // 임시 비밀번호 암호화 후 저장
        userMapper.updatePassword(userDto.getNo(), passwordEncoder.encode(temporaryPassword));

        // 실제 애플리케이션에서는 여기서 이메일로 임시 비밀번호를 전송하는 코드가 추가되어야 함
        // emailService.sendTemporaryPassword(userDto.getEmail(), temporaryPassword);

        return temporaryPassword;
    }

    // 임시 비밀번호 생성 (8자리 랜덤 문자열)
    private String generateTemporaryPassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(8);
        Random random = new Random();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }

        return sb.toString();
    }

    // 팔로워 목록 조회
    @Override
    public List<UserFollowInfoResponse> getUserFollowers(Long userNo) {
        // 사용자 존재 여부 확인
        UserDto userDto = userMapper.findById(userNo);
        if (userDto == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        // 팔로워 정보 조회
        List<UserFollowInfoDto> followers = userMapper.findFollowersByUserNo(userNo);
        List<UserFollowInfoResponse> result = new ArrayList<>();

        // 각 팔로워의 기본 정보 조회 및 변환
        for (UserFollowInfoDto follower : followers) {
            UserFollowInfoResponse followInfo = userMapper.findUserBasicInfoById(follower.getFollower());
            if (followInfo != null) {
                result.add(followInfo);
            }
        }

        return result;
    }

    // 팔로잉 목록 조회
    @Override
    public List<UserFollowInfoResponse> getUserFollowing(Long userNo) {
        // 사용자 존재 여부 확인
        UserDto userDto = userMapper.findById(userNo);
        if (userDto == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        // 팔로잉 정보 조회
        List<UserFollowInfoDto> following = userMapper.findFollowingByUserNo(userNo);
        List<UserFollowInfoResponse> result = new ArrayList<>();

        // 각 팔로잉의 기본 정보 조회 및 변환
        for (UserFollowInfoDto follow : following) {
            UserFollowInfoResponse followInfo = userMapper.findUserBasicInfoById(follow.getFollow());
            if (followInfo != null) {
                result.add(followInfo);
            }
        }

        return result;
    }

    // 팔로우
    @Override
    @Transactional
    public boolean followUser(Long followerId, Long followingId) {
        // 사용자 존재 여부 확인
        UserDto follower = userMapper.findById(followerId);
        UserDto following = userMapper.findById(followingId);

        if (follower == null || following == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        // 자기 자신을 팔로우할 수 없음
        if (followerId.equals(followingId)) {
            throw new RuntimeException("자기 자신을 팔로우할 수 없습니다.");
        }

        // ⭐ 이미 팔로우 중인지 확인
        if (userMapper.existsFollow(followingId, followerId) > 0) {
            throw new RuntimeException("이미 팔로우 중인 사용자입니다.");
        }

        try {
            userMapper.addFollow(followingId, followerId);
            userMapper.increaseFollowCount(followerId);
            userMapper.increaseFollowerCount(followingId);
            return true;
        } catch (Exception e) {
//            log.error("팔로우 처리 중 오류 발생", e);
            throw new RuntimeException("팔로우 처리 중 오류가 발생했습니다.");
        }
    }

    // 언팔로우
    @Override
    @Transactional
    public boolean unfollowUser(Long followerId, Long followingId) {
        // 사용자 존재 여부 확인
        UserDto follower = userMapper.findById(followerId);
        UserDto following = userMapper.findById(followingId);

        if (follower == null || following == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        // ⭐ 팔로우 관계가 존재하는지 확인
        if (userMapper.existsFollow(followingId, followerId) == 0) {
            throw new RuntimeException("팔로우 관계가 존재하지 않습니다.");
        }

        try {
            userMapper.removeFollow(followingId, followerId);
            userMapper.decreaseFollowCount(followerId);
            userMapper.decreaseFollowerCount(followingId);
            return true;
        } catch (Exception e) {
//            log.error("언팔로우 처리 중 오류 발생", e);
            throw new RuntimeException("언팔로우 처리 중 오류가 발생했습니다.");
        }
    }

    // 소셜 로그인 처리
    @Override
    @Transactional
    public UserResponse handleSocialLogin(String provider, UserSocialLoginRequest request) {
        // 여기서는 예시로 간단히 처리
        // 실제로는 provider에 따라 다른 API 요청 및 처리 로직이 필요함

        // 예시 코드 - 실제 구현 필요
        String socialId = "social_" + request.getAuthorizationCode(); // 실제로는 토큰 교환 후 사용자 정보 조회 필요
        String email = "user_" + System.currentTimeMillis() + "@" + provider + ".com"; // 실제로는 API에서 받아와야 함

        UserServiceDto socialUserInfo = new UserServiceDto();
        socialUserInfo.setEmail(email);
        socialUserInfo.setNickname("User_" + provider.substring(0, 1).toUpperCase() + provider.substring(1));
        socialUserInfo.setProfileUrl(null); // 실제로는 소셜 API에서 받아온 프로필 URL

        return processSocialLogin(provider.toUpperCase(), socialId, socialUserInfo);
    }

    // UserServiceImpl.java에 추가할 메서드
    @Override
    public String uploadProfileImage(Long userNo, MultipartFile image, String position) throws IOException {
        // 파일 검증
        if (image.isEmpty()) {
            throw new RuntimeException("파일이 비어있습니다.");
        }

        // 허용된 파일 형식 확인
        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("이미지 파일만 업로드 가능합니다.");
        }

        // 파일 크기 확인 (5MB 제한)
        if (image.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("파일 크기가 5MB를 초과할 수 없습니다.");
        }

        // 사용자 존재 확인
        UserDto user = userMapper.findById(userNo);
        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        // 업로드 디렉토리 생성
        String uploadDir = System.getProperty("user.home") + "/triplog/uploads/profiles/";
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 파일명 생성 (중복 방지)
        String originalFileName = image.getOriginalFilename();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String newFileName = "profile_" + userNo + "_" + System.currentTimeMillis() + fileExtension;

        // 파일 저장
        Path filePath = Paths.get(uploadDir + newFileName);
        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // DB에 프로필 URL 업데이트
        String profileUrl = "/triplog/uploads/profiles/" + newFileName;
        UserDto updateUser = new UserDto();
        updateUser.setNo(userNo);
        updateUser.setProfileUrl(profileUrl);
        updateUser.setNickname(user.getNickname()); // 기존 닉네임 유지
        updateUser.setName(user.getName()); // 기존 이름 유지
        updateUser.setPhone(user.getPhone()); // 기존 전화번호 유지
        updateUser.setAddress(user.getAddress()); // 기존 주소 유지
        updateUser.setAddressDetail(user.getAddressDetail()); // 기존 상세주소 유지

        userMapper.updateUser(updateUser);

        return profileUrl;
    }


    // 소셜 로그인 처리 (내부 메서드)
    private UserResponse processSocialLogin(String socialType, String socialId, UserServiceDto socialUserInfo) {
        // 기존 소셜 계정 확인
        UserDto existingUser = userMapper.findBySocialIdAndType(socialId, socialType);

        // 기존 계정이 있는 경우
        if (existingUser != null) {
            UserResponse response = new UserResponse();
            response.setNo(existingUser.getNo());
            response.setNickname(existingUser.getNickname());
            response.setProfileUrl(existingUser.getProfileUrl());
            return response;
        }

        // 새 계정 생성
        socialUserInfo.setSocialId(socialId);
        socialUserInfo.setSocialType(socialType);
        socialUserInfo.setRole("ROLE_USER");
        // 소셜 로그인은 비밀번호가 없으므로 랜덤 문자열로 설정
        socialUserInfo.setPassword(passwordEncoder.encode(generateTemporaryPassword()));

        userMapper.save(socialUserInfo);
        Long userNo = userMapper.getLastInsertId();

        UserResponse response = new UserResponse();
        response.setNo(userNo);
        response.setNickname(socialUserInfo.getNickname());
        response.setProfileUrl(socialUserInfo.getProfileUrl());

        return response;
    }
}