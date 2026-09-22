package com.coachpad.service;

import com.coachpad.dto.user.response.UserStatisticResponse;
import com.coachpad.exception.ApiException;
import com.coachpad.model.entity.UserEntity;
import com.coachpad.repository.AppointmentRepository;
import com.coachpad.repository.ClientRepository;
import com.coachpad.repository.UserRepository;
import com.coachpad.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;
    private final ClientRepository clientRepository;
    private final AppointmentRepository appointmentRepository;

    public UserEntity getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new ApiException("User not found", HttpStatus.UNAUTHORIZED)
        );
    }

    public UserStatisticResponse getUserStatistic() {
        Long userId = securityUtil.getCurrentUserId();
        UserEntity user = getUserById(userId);

        LocalDate today = LocalDate.now();
        LocalDateTime thisMonthStart = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime thisMonthEnd = thisMonthStart.plusMonths(1);
        LocalDateTime prevMonthStart = thisMonthStart.minusMonths(1);

        long totalClients = clientRepository.countByUserIdAndDeletedFalse(userId);
        long newClientsThisMonth = clientRepository.countCreatedSince(userId, thisMonthStart);
        long trainingsThisMonth = appointmentRepository.countTrainingsInRange(userId, thisMonthStart, thisMonthEnd);
        long trainingsPrevMonth = appointmentRepository.countTrainingsInRange(userId, prevMonthStart, thisMonthStart);

        return new UserStatisticResponse(
                user.getTelegramId(),
                totalClients,
                trainingsThisMonth,
                trainingsPrevMonth,
                newClientsThisMonth
        );
    }
}
