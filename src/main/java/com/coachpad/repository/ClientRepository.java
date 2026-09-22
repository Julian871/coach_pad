package com.coachpad.repository;

import com.coachpad.model.entity.ClientEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, Long> {

    boolean existsByNameAndUserIdAndDeletedFalse(String name, Long userId);

    @EntityGraph(attributePaths = {"user"})
    Optional<ClientEntity> findClientWithUserByIdAndDeletedFalse(Long id);

    List<ClientEntity> findByUserIdAndDeletedFalse(Long userId, Sort sort);

    long countByUserIdAndDeletedFalse(Long userId);

    @Query("SELECT COUNT(c) FROM ClientEntity c " +
            "WHERE c.user.id = :userId " +
            "AND c.deleted = false " +
            "AND c.createdAt >= :from")
    long countCreatedSince(
            @Param("userId") Long userId,
            @Param("from") LocalDateTime from
    );
}
