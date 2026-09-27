package com.coachpad.controller;

import com.coachpad.dto.client.request.CreateClientRequest;
import com.coachpad.dto.client.request.UpdateClientRequest;
import com.coachpad.dto.client.response.ClientResponse;
import com.coachpad.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping()
    @PreAuthorize("hasAuthority('TRAINER')")
    public ResponseEntity<Void> createClient(@Valid @RequestBody CreateClientRequest request) {
        clientService.createClient(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/{clientId}")
    @PreAuthorize("hasAuthority('TRAINER')")
    public ResponseEntity<ClientResponse> getClientById(@PathVariable Long clientId) {
        return ResponseEntity.ok(clientService.getClientById(clientId));
    }

    @GetMapping()
    @PreAuthorize("hasAuthority('TRAINER')")
    public ResponseEntity<List<ClientResponse>> getMyClients() {
        return ResponseEntity.ok(clientService.getMyClients());
    }

    @PutMapping("/{clientId}")
    @PreAuthorize("hasAuthority('TRAINER')")
    public ResponseEntity<ClientResponse> updateClientById(
            @PathVariable Long clientId,
            @Valid @RequestBody UpdateClientRequest request
    ) {
        return ResponseEntity.ok(clientService.updateClient(clientId, request));
    }

    @DeleteMapping("/{clientId}")
    @PreAuthorize("hasAuthority('TRAINER')")
    public ResponseEntity<Void> deleteClientById(
            @PathVariable Long clientId
    ) {
        clientService.deleteClientById(clientId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{clientId}/avatar")
    @PreAuthorize("hasAuthority('TRAINER')")
    public ResponseEntity<ClientResponse> uploadAvatar(
            @PathVariable Long clientId,
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(clientService.uploadAvatar(clientId, file));
    }

    @DeleteMapping("/{clientId}/avatar")
    @PreAuthorize("hasAuthority('TRAINER')")
    public ResponseEntity<Void> deleteAvatar(@PathVariable Long clientId) {
        clientService.deleteAvatar(clientId);
        return ResponseEntity.noContent().build();
    }
}