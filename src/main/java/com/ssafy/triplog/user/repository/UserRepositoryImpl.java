package com.ssafy.triplog.user.repository;

import com.ssafy.triplog.user.dto.UserDto;
import com.ssafy.triplog.user.dto.UserFollowInfoDto;
import com.ssafy.triplog.user.dto.UserFollowInfoResponse;
import com.ssafy.triplog.user.dto.UserServiceDto;
import com.ssafy.triplog.user.repository.mapper.UserMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Override
    public UserDto findById(Long id) {
        return userMapper.findById(id);
    }

    @Override
    public boolean deleteUser(Long userNo) {
        return userMapper.deleteUser(userNo);
    }

    @Override
    public String findPasswordById(Long userNo) {
        return userMapper.findPasswordById(userNo);
    }

    @Override
    public int updateUser(UserDto userDto) {
        return userMapper.updateUser(userDto);
    }

    @Override
    public List<UserDto> findAllUsers() {
        return userMapper.findAllUsers();
    }

    @Override
    public UserDto findByNameAndPhone(String name, String phone) {
        return userMapper.findByNameAndPhone(name, phone);
    }

    @Override
    public UserDto findByEmailAndNameAndPhone(String email, String name, String phone) {
        return userMapper.findByEmailAndNameAndPhone(email, name, phone);
    }

    @Override
    public void updatePassword(Long userNo, String password) {
        userMapper.updatePassword(userNo, password);
    }

    @Override
    public List<UserFollowInfoDto> findFollowersByUserNo(Long userNo) {
        return userMapper.findFollowersByUserNo(userNo);
    }

    @Override
    public List<UserFollowInfoDto> findFollowingByUserNo(Long userNo) {
        return userMapper.findFollowingByUserNo(userNo);
    }

    @Override
    public UserFollowInfoResponse findUserBasicInfoById(Long userNo) {
        return userMapper.findUserBasicInfoById(userNo);
    }

    @Override
    @Transactional
    public boolean addFollow(Long followId, Long followerId) {
        try {
            userMapper.addFollow(followId, followerId);
            userMapper.increaseFollowCount(followerId);
            userMapper.increaseFollowerCount(followId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public boolean removeFollow(Long followId, Long followerId) {
        try {
            userMapper.removeFollow(followId, followerId);
            userMapper.decreaseFollowCount(followerId);
            userMapper.decreaseFollowerCount(followId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public UserDto findBySocialIdAndType(String socialId, String socialType) {
        return userMapper.findBySocialIdAndType(socialId, socialType);
    }
}