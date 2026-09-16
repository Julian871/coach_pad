package com.coachpad.repository;

import com.coachpad.model.entity.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {

    boolean existsByClientIdAndDateTime(Long clientId, LocalDateTime dateTime);

    boolean existsByUserIdAndDateTime(Long userId, LocalDateTime dateTime);

    List<AppointmentEntity> findByUserIdAndDateTimeBetween(
            Long userId,
            LocalDateTime start,
            LocalDateTime end
    );

    Optional<AppointmentEntity> findByIdAndUserId(Long id, Long userId);
}