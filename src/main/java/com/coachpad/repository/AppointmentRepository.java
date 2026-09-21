package com.coachpad.repository;

import com.coachpad.model.entity.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT COUNT(a) FROM AppointmentEntity a " +
            "WHERE a.user.id = :userId " +
            "AND a.type = com.coachpad.model.enums.AppointmentType.TRAINING " +
            "AND a.dateTime >= :from AND a.dateTime < :to")
    long countTrainingsInRange(
            @Param("userId") Long userId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}