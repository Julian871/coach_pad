package com.coachpad.service;

import com.coachpad.dto.client.request.CreateClientRequest;
import com.coachpad.dto.client.request.UpdateClientRequest;
import com.coachpad.dto.client.response.ClientResponse;
import com.coachpad.exception.ApiException;
import com.coachpad.mapper.ClientMapper;
import com.coachpad.model.entity.ClientEntity;
import com.coachpad.model.entity.UserEntity;
import com.coachpad.model.enums.Gender;
import com.coachpad.repository.ClientRepository;
import com.coachpad.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final SecurityUtil securityUtil;
    private final ClientMapper clientMapper;

    public void createClient(CreateClientRequest request) {
        UserEntity user = securityUtil.getCurrentUser();

        if(clientRepository.existsByNameAndUserId(request.getName().trim(), user.getId()))
            throw new ApiException("This name exists", HttpStatus.CONFLICT);


        ClientEntity client = ClientEntity.builder()
                .name(request.getName())
                .gender(Gender.valueOf(request.getGender()))
                .user(user)
                .build();

        clientRepository.save(client);
    }

    public ClientResponse getClientById(String clientId) {
        ClientEntity client = clientRepository.findClientWithUserById(UUID.fromString(clientId))
                .orElseThrow(() -> new ApiException("Client not found", HttpStatus.BAD_REQUEST));

        String currentUserEmail = securityUtil.getCurrentUserEmail();

        if(!client.getUser().getEmail().equals(currentUserEmail))
            throw new ApiException("Incorrect client", HttpStatus.FORBIDDEN);

        return clientMapper.toDto(client);
    }

    public List<ClientResponse> getMyClients() {

        String currentUserEmail = securityUtil.getCurrentUserEmail();

        List<ClientEntity> clients = clientRepository.findByUserEmail(currentUserEmail);

        return clientMapper.toDtoList(clients);
    }

    public ClientResponse updateClient(String clientId, UpdateClientRequest request) {
        ClientEntity client = clientRepository.findClientWithUserById(UUID.fromString(clientId))
                .orElseThrow(() -> new ApiException("Client not found", HttpStatus.BAD_REQUEST));

        String currentUserEmail = securityUtil.getCurrentUserEmail();

        if(!client.getUser().getEmail().equals(currentUserEmail))
            throw new ApiException("Incorrect client", HttpStatus.FORBIDDEN);

        clientMapper.updateEntity(client, request);
        clientRepository.save(client);
        return clientMapper.toDto(client);
    }

    public void deleteClientById(String clientId) {
        ClientEntity client = clientRepository.findClientWithUserById(UUID.fromString(clientId))
                .orElseThrow(() -> new ApiException("Client not found", HttpStatus.BAD_REQUEST));

        String currentUserEmail = securityUtil.getCurrentUserEmail();

        if(!client.getUser().getEmail().equals(currentUserEmail))
            throw new ApiException("Incorrect client", HttpStatus.FORBIDDEN);

        clientRepository.delete(client);
    }
}
