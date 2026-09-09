package com.coachpad.mapper;

import com.coachpad.dto.client.request.UpdateClientRequest;
import com.coachpad.dto.client.response.ClientResponse;
import com.coachpad.model.entity.ClientEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ClientMapper {

    ClientResponse toDto(ClientEntity entity);

    List<ClientResponse> toDtoList(List<ClientEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(@MappingTarget ClientEntity entity, UpdateClientRequest request);
}
