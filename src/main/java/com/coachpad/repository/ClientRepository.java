package com.coachpad.repository;

import com.coachpad.model.entity.ClientEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, UUID> {

    boolean existsByNameAndUserId(String name, UUID userId);

    @EntityGraph(attributePaths = {"user"})
    Optional<ClientEntity> findClientWithUserById(UUID id);

    List<ClientEntity> findByUserEmail(String email);
}
