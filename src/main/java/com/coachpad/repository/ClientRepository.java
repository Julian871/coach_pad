package com.coachpad.repository;

import com.coachpad.model.entity.ClientEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, Long> {

    boolean existsByNameAndUserId(String name, Long userId);

    @EntityGraph(attributePaths = {"user"})
    Optional<ClientEntity> findClientWithUserById(Long id);

    List<ClientEntity> findByUserEmail(String email);
}
