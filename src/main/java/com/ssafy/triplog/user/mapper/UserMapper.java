package com.ssafy.triplog.user.mapper;

import com.ssafy.triplog.user.dto.UserDto;
import com.ssafy.triplog.user.dto.UserFollowInfoDto;
import com.ssafy.triplog.user.dto.UserFollowInfoResponse;
import com.ssafy.triplog.user.dto.UserServiceDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    // 기존 메서드
    UserDto findByEmail(@Param("email") String email);
    int existsByEmail(@Param("email") String email);
    // 닉네임 중복 확인 (추가)
    int existsByNickname(@Param("nickname") String nickname);
    void save(UserServiceDto userServiceDto);
    Long getLastInsertId();
    UserDto findById(@Param("id") Long id);

    boolean deleteUser(@Param("userNo") Long userNo);
    String findPasswordById(@Param("userNo") Long userNo);
    int updateUser(UserDto userDto);
    List<UserDto> findAllUsers();
    UserDto findByNameAndPhone(@Param("name") String name, @Param("phone") String phone);
    UserDto findByEmailAndNameAndPhone(@Param("email") String email, @Param("name") String name, @Param("phone") String phone);
    void updatePassword(@Param("userNo") Long userNo, @Param("password") String password);

    // 팔로우 관련
    List<UserFollowInfoDto> findFollowersByUserNo(@Param("userNo") Long userNo);
    List<UserFollowInfoDto> findFollowingByUserNo(@Param("userNo") Long userNo);
    UserFollowInfoResponse findUserBasicInfoById(@Param("userNo") Long userNo);
    void addFollow(@Param("followId") Long followId, @Param("followerId") Long followerId);
    void removeFollow(@Param("followId") Long followId, @Param("followerId") Long followerId);
    void increaseFollowCount(@Param("userNo") Long userNo);
    void increaseFollowerCount(@Param("userNo") Long userNo);
    void decreaseFollowCount(@Param("userNo") Long userNo);
    void decreaseFollowerCount(@Param("userNo") Long userNo);

    // 소셜 로그인
    UserDto findBySocialIdAndType(@Param("socialId") String socialId, @Param("socialType") String socialType);
}