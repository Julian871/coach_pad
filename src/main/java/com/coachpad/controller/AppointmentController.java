package com.coachpad.controller;

import com.coachpad.dto.appointment.request.CreateAppointmentPlanRequest;
import com.coachpad.dto.appointment.request.CreateAppointmentTrainingRequest;
import com.coachpad.dto.appointment.response.AppointmentResponse;
import com.coachpad.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/appointment")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/training")
    @PreAuthorize("hasAuthority('TRAINER')")
    public ResponseEntity<Void> createAppointmentTraining(@Valid @RequestBody CreateAppointmentTrainingRequest request) {
        appointmentService.createAppointmentTraining(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/plan")
    @PreAuthorize("hasAuthority('TRAINER')")
    public ResponseEntity<Void> createAppointmentPlan(@Valid @RequestBody CreateAppointmentPlanRequest request) {
        appointmentService.createAppointmentPlan(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping()
    @PreAuthorize("hasAuthority('TRAINER')")
    public ResponseEntity<List<AppointmentResponse>> getAppointments(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return ResponseEntity.ok(appointmentService.getAppointments(from, to));
    }

    @DeleteMapping("/{appointmentId}")
    @PreAuthorize("hasAuthority('TRAINER')")
    public ResponseEntity<Void> deleteAppointmentById(@PathVariable Long appointmentId) {
        appointmentService.deleteAppointment(appointmentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
