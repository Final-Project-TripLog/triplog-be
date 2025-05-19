package com.ssafy.triplog.user.repository;

import com.ssafy.triplog.user.dto.UserDto;
import com.ssafy.triplog.user.dto.UserServiceDto;
import com.ssafy.triplog.user.repository.UserRepository;
import com.ssafy.triplog.user.repository.mapper.UserMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;

    public UserRepositoryImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDto findByEmail(String email) {
        return userMapper.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userMapper.existsByEmail(email) > 0;
    }

    @Override
    public Long save(UserServiceDto userServiceDto) {
        userMapper.save(userServiceDto);
        return userMapper.getLastInsertId();
    }
}