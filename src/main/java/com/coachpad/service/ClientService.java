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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final UserService userService;
    private final SecurityUtil securityUtil;
    private final ClientMapper clientMapper;

    private static final String CACHE_KEY =
            "T(org.springframework.security.core.context.SecurityContextHolder)" +
                    ".getContext().getAuthentication().getPrincipal().id()";

    @CacheEvict(cacheNames = "clients", key = CACHE_KEY, cacheManager = "clientListCacheManager")
    public void createClient(CreateClientRequest request) {
        Long userId = securityUtil.getCurrentUserId();
        UserEntity user = userService.getUserById(userId);

        if(clientRepository.existsByNameAndUserIdAndDeletedFalse(request.getName().trim(), userId))
            throw new ApiException("This name exists", HttpStatus.CONFLICT);


        ClientEntity client = ClientEntity.builder()
                .name(request.getName())
                .gender(Gender.valueOf(request.getGender()))
                .user(user)
                .build();

        clientRepository.save(client);
    }

    public ClientResponse getClientById(Long clientId) {
        ClientEntity client = clientRepository.findClientWithUserByIdAndDeletedFalse(clientId)
                .orElseThrow(() -> new ApiException("Client not found", HttpStatus.BAD_REQUEST));

        Long userId = securityUtil.getCurrentUserId();

        if(!client.getUser().getId().equals(userId))
            throw new ApiException("Incorrect client", HttpStatus.FORBIDDEN);

        return clientMapper.toDto(client);
    }

    @Cacheable(
            value = "clients",
            key = CACHE_KEY,
            cacheManager = "clientListCacheManager"
    )
    public List<ClientResponse> getMyClients() {

        Long userId = securityUtil.getCurrentUserId();

        List<ClientEntity> clients = clientRepository.findByUserIdAndDeletedFalse(userId, Sort.by(Sort.Direction.ASC, "name"));

        return clientMapper.toDtoList(clients);
    }

    @CacheEvict(
            cacheNames = "clients",
            key = CACHE_KEY,
            cacheManager = "clientListCacheManager"
    )
    @Transactional
    public ClientResponse updateClient(Long clientId, UpdateClientRequest request) {
        ClientEntity client = clientRepository.findClientWithUserByIdAndDeletedFalse(clientId)
                .orElseThrow(() -> new ApiException("Client not found", HttpStatus.BAD_REQUEST));

        Long userId = securityUtil.getCurrentUserId();

        if(!client.getUser().getId().equals(userId))
            throw new ApiException("Incorrect client", HttpStatus.FORBIDDEN);

        clientMapper.updateEntity(client, request);
        clientRepository.save(client);
        return clientMapper.toDto(client);
    }

    @CacheEvict(
            cacheNames = "clients",
            key = CACHE_KEY,
            cacheManager = "clientListCacheManager"
    )
    @Transactional
    public void deleteClientById(Long clientId) {
        ClientEntity client = clientRepository.findClientWithUserByIdAndDeletedFalse(clientId)
                .orElseThrow(() -> new ApiException("Client not found", HttpStatus.BAD_REQUEST));

        Long userId = securityUtil.getCurrentUserId();

        if(!client.getUser().getId().equals(userId))
            throw new ApiException("Incorrect client", HttpStatus.FORBIDDEN);

        client.setDeleted(true);
        clientRepository.save(client);
    }
}
