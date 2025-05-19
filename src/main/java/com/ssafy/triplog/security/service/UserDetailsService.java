package com.ssafy.triplog.security.service;

import com.ssafy.triplog.security.dto.CustomUserDetails;
import com.ssafy.triplog.user.dto.UserDto;
import com.ssafy.triplog.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDto userDto = userRepository.findByEmail(username);

        if (userDto != null) {
            return new CustomUserDetails(userDto);
        }

        throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
    }
}