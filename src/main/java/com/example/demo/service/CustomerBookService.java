package com.example.demo.service;


import com.example.demo.dto.BookResponseDTO;
import com.example.demo.dto.BookSearchDTO;
import com.example.demo.entity.Book;
import com.example.demo.repo.BookRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerBookService {

    @Autowired
    private BookRepository bookRepository;

    public Page<BookResponseDTO> browseBooks(
            int page,
            int size,
            String sortBy,
            String sortDir
    ){
        Sort sort=sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable= PageRequest.of(page,size,sort);
        Page<Book> bookPage=bookRepository.findByIsAvailableTrue(pageable);
        return bookPage.map(this::convertDTO);
    }

    public Page<BookResponseDTO> searchBooks( BookSearchDTO bookSearchDTO, int page, int size){
        Pageable pageable=PageRequest.of(page,size);
        Page<Book> bookPage=bookRepository.searchBooks(
                bookSearchDTO.getTitle(),
                bookSearchDTO.getAuthor(),
                bookSearchDTO.getCategory(),
                bookSearchDTO.getMinPrice(),
                bookSearchDTO.getMaxPrice(),
                pageable
        );
        return bookPage.map(this::convertDTO);
    }

    public BookResponseDTO getBookById(UUID bookId) throws Exception{
        Book book=bookRepository.findById(bookId)
                .filter(Book::getIsAvailable)
                .orElseThrow(() -> new Exception("Book not found or not available"));
        return convertDTO(book);
    }

    public Page<BookResponseDTO> getBooksByCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> bookPage = bookRepository.findByCategoryAndIsAvailableTrue(category, pageable);
        return bookPage.map(this::convertDTO);
    }

    public List<BookResponseDTO> getRecommendations(String username){
        List<Book> list=bookRepository.findTop10ByIsAvailableTrueOrderByCreatedAtDesc();
        return list.stream().map(this::convertDTO).collect(Collectors.toList());
    }

    private BookResponseDTO convertDTO(Book book) {
        return BookResponseDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .price(book.getPrice())
                .isAvailable(book.getIsAvailable())
                .stockQuantity(book.getStockQuantity())
                .tenantId(book.getTenantId())
                .createdAt(book.getCreatedAt())
                .build();
    }

}
