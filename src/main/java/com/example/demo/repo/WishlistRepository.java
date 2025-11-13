package com.example.demo.repo;

import com.example.demo.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, UUID> {
    List<Wishlist> findByCustomerId(UUID customerId);
    Optional<Wishlist> findByCustomerIdAndBookId(UUID customerId, UUID bookId);
    boolean existsByCustomerIdAndBookId(UUID customerId, UUID bookId);
}
