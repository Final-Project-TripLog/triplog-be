package com.ssafy.triplog.user.repository.mapper;

import com.ssafy.triplog.user.dto.UserDto;
import com.ssafy.triplog.user.dto.UserServiceDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    UserDto findByEmail(@Param("email") String email);
    int existsByEmail(@Param("email") String email);
    void save(UserServiceDto userServiceDto);
    Long getLastInsertId();
    // 기타 필요한 메소드들...
}