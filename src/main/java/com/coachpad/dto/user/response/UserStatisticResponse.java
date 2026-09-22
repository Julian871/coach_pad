package com.coachpad.dto.user.response;

public record UserStatisticResponse(
        Long telegramId,
        long totalClient,
        long trainingsThisMonth,
        long trainingsPrevMonth,
        long newClientsThisMonth
) { }
