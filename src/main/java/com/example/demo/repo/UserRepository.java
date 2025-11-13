package com.example.demo.repo;

import com.example.demo.entity.User;
import com.example.demo.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByRole(UserRole role);
    boolean existsByUsername(String username);

    @Query(value = "SELECT tenant_id FROM users WHERE id = ?1", nativeQuery = true)
    Optional<UUID> findTenantIdByUserId(UUID userId);


}
