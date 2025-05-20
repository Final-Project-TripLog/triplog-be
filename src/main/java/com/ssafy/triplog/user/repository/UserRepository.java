package com.ssafy.triplog.user.repository;

import com.ssafy.triplog.user.dto.UserDto;
import com.ssafy.triplog.user.dto.UserFollowInfoDto;
import com.ssafy.triplog.user.dto.UserFollowInfoResponse;
import com.ssafy.triplog.user.dto.UserServiceDto;

import java.util.List;

public interface UserRepository {
    // 기존 메서드
    UserDto findByEmail(String email);
    boolean existsByEmail(String email);
    Long save(UserServiceDto userServiceDto);
    UserDto findById(Long id);

    // 새로 추가할 메서드
    boolean deleteUser(Long userNo);
    String findPasswordById(Long userNo);
    int updateUser(UserDto userDto);
    List<UserDto> findAllUsers();
    UserDto findByNameAndPhone(String name, String phone);
    UserDto findByEmailAndNameAndPhone(String email, String name, String phone);
    void updatePassword(Long userNo, String password);

    // 팔로우 관련
    List<UserFollowInfoDto> findFollowersByUserNo(Long userNo);
    List<UserFollowInfoDto> findFollowingByUserNo(Long userNo);
    UserFollowInfoResponse findUserBasicInfoById(Long userNo);
    boolean addFollow(Long followId, Long followerId);
    boolean removeFollow(Long followId, Long followerId);

    // 소셜 로그인
    UserDto findBySocialIdAndType(String socialId, String socialType);
}