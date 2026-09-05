package com.coachpad.service.handler.auth;

import com.coachpad.dto.auth.request.RegistrationRequest;
import com.coachpad.model.entity.UserEntity;
import com.coachpad.model.enums.UserRole;
import com.coachpad.repository.UserRepository;
import com.coachpad.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationHandler {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void register(RegistrationRequest request) {

        validateEmailUniqueness(request.getEmail());

        UserEntity user = createUser(request);
        userRepository.save(user);

        sendConfirmationToken(user.getEmail(), user.getConfirmationToken());
    }

    private void validateEmailUniqueness(String email) {
        if(userRepository.existsByEmail(email))
            throw new ApiException("Email already registered", HttpStatus.CONFLICT);
    }

    private UserEntity createUser(RegistrationRequest request) {
        return UserEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .confirmed(false)
                .confirmationToken(UUID.randomUUID())
                .role(UserRole.valueOf(request.getRole()))
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();
    }

    private void sendConfirmationToken(String email, UUID token) {
        log.info("Email: {}, Token: {}", email, token);
        // todo: send in email
    }
}
