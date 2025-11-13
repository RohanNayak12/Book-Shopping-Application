package com.example.demo.repo;

import com.example.demo.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {

    List<Book> findByTitleContainingIgnoreCase(String title);
    List<Book> findByAuthorContainingIgnoreCase(String author);
    List<Book> findByTenantId(UUID tenantId);
    List<Book> findByIsAvailableTrue();
    Page<Book> findByIsAvailableTrue(Pageable pageable);
    Page<Book> findByCategoryAndIsAvailableTrue(String category, Pageable pageable);
    List<Book> findTop10ByIsAvailableTrueOrderByCreatedAtDesc();

    @Query("SELECT b FROM Book b WHERE " +
            "(:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))) AND " +
            "(:category IS NULL OR b.category = :category) AND " +
            "(:minPrice IS NULL OR b.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR b.price <= :maxPrice) AND " +
            "b.isAvailable = true")
    Page<Book> searchBooks(@Param("title") String title,
                           @Param("author") String author,
                           @Param("category") String category,
                           @Param("minPrice") Double minPrice,
                           @Param("maxPrice") Double maxPrice,
                           Pageable pageable);


}
