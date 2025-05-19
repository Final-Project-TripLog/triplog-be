package com.ssafy.triplog.user.repository;

import com.ssafy.triplog.user.dto.UserDto;
import com.ssafy.triplog.user.dto.UserServiceDto;

public interface UserRepository {
    UserDto findByEmail(String email);
    boolean existsByEmail(String email);
    Long save(UserServiceDto userServiceDto);
}