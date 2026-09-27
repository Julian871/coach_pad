package com.coachpad.mapper;

import com.coachpad.dto.appointment.response.AppointmentResponse;
import com.coachpad.model.entity.AppointmentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AppointmentMapper {

    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "clientAvatarUrl", source = "client.avatarUrl")
    AppointmentResponse toDto(AppointmentEntity entity);

    List<AppointmentResponse> toDtoList(List<AppointmentEntity> entities);
}