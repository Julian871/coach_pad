package com.coachpad.service;

import com.coachpad.exception.ApiException;
import com.coachpad.model.entity.UserEntity;
import com.coachpad.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new ApiException("User not found", HttpStatus.UNAUTHORIZED)
        );
    }
}
