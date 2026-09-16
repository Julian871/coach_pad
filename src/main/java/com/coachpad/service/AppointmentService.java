package com.coachpad.service;

import com.coachpad.dto.appointment.request.CreateAppointmentPlanRequest;
import com.coachpad.dto.appointment.request.CreateAppointmentTrainingRequest;
import com.coachpad.dto.appointment.response.AppointmentResponse;
import com.coachpad.exception.ApiException;
import com.coachpad.mapper.AppointmentMapper;
import com.coachpad.model.entity.AppointmentEntity;
import com.coachpad.model.entity.ClientEntity;
import com.coachpad.model.entity.UserEntity;
import com.coachpad.model.enums.AppointmentType;
import com.coachpad.repository.AppointmentRepository;
import com.coachpad.repository.ClientRepository;
import com.coachpad.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final SecurityUtil securityUtil;
    private final ClientRepository clientRepository;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;


    public void createAppointmentTraining(CreateAppointmentTrainingRequest request) {
        ClientEntity client = clientRepository.findClientWithUserById(request.getClientId())
                .orElseThrow(() -> new ApiException("Client not found", HttpStatus.BAD_REQUEST));

        UserEntity currentUser = securityUtil.getCurrentUser();

        if(!client.getUser().getEmail().equals(currentUser.getEmail()))
            throw new ApiException("Incorrect client", HttpStatus.FORBIDDEN);

        if(appointmentRepository.existsByClientIdAndDateTime(client.getId(), request.getDateTime()))
            throw new ApiException("Current time exists", HttpStatus.CONFLICT);

        AppointmentEntity appointment = AppointmentEntity.builder()
                .dateTime(request.getDateTime())
                .type(AppointmentType.TRAINING)
                .comment(request.getComment())
                .client(client)
                .plan(client.getName())
                .user(currentUser)
                .build();

        appointmentRepository.save(appointment);
    }

    public void createAppointmentPlan(CreateAppointmentPlanRequest request) {
        UserEntity currentUser = securityUtil.getCurrentUser();

        if(appointmentRepository.existsByUserIdAndDateTime(currentUser.getId(), request.getDateTime()))
            throw new ApiException("Current time exists", HttpStatus.CONFLICT);

        AppointmentEntity appointment = AppointmentEntity.builder()
                .dateTime(request.getDateTime())
                .type(AppointmentType.PLAN)
                .comment(request.getComment())
                .client(null)
                .user(currentUser)
                .plan(request.getPlan())
                .build();

        appointmentRepository.save(appointment);
    }

    public List<AppointmentResponse> getAppointments(LocalDateTime startTime, LocalDateTime endTime) {
        UserEntity user = securityUtil.getCurrentUser();

        List<AppointmentEntity> appointments = appointmentRepository.findByUserIdAndDateTimeBetween(
                user.getId(),
                startTime,
                endTime
        );

        return appointmentMapper.toDtoList(appointments);
    }

    public void deleteAppointment(Long appointmentId) {
        UserEntity user = securityUtil.getCurrentUser();

        AppointmentEntity appointment = appointmentRepository.findByIdAndUserId(appointmentId, user.getId())
                .orElseThrow(() -> new ApiException("Appointment not found", HttpStatus.BAD_REQUEST));

        appointmentRepository.delete(appointment);
    }
}
