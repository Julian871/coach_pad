package com.coachpad.mapper;

import com.coachpad.dto.appointment.response.AppointmentResponse;
import com.coachpad.model.entity.AppointmentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AppointmentMapper {

    List<AppointmentResponse> toDtoList(List<AppointmentEntity> entities);
}
