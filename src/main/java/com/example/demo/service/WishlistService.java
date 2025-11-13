package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Transactional(readOnly = true)
    public List<BookResponseDTO> getWishlist(String username) throws Exception {
        User customer = findCustomerByUsername(username);
        List<Wishlist> wishlistItems = wishlistRepository.findByCustomerId(customer.getId());

        return wishlistItems.stream()
                .map(item -> convertToDTO(item.getBook()))
                .collect(Collectors.toList());
    }

    public void addToWishlist(String username, UUID bookId) throws Exception {
        User customer = findCustomerByUsername(username);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new Exception("Book not found"));

        if (wishlistRepository.existsByCustomerIdAndBookId(customer.getId(), bookId)) {
            throw new Exception("Book already in wishlist");
        }

        Wishlist wishlistItem = Wishlist.builder()
                .customer(customer)
                .book(book)
                .build();

        wishlistRepository.save(wishlistItem);
    }

    public void removeFromWishlist(String username, UUID bookId) throws Exception {
        User customer = findCustomerByUsername(username);
        Wishlist wishlistItem = wishlistRepository.findByCustomerIdAndBookId(customer.getId(), bookId)
                .orElseThrow(() -> new Exception("Wishlist item not found"));

        wishlistRepository.delete(wishlistItem);
    }

    public CartResponseDTO moveToCart(String username, UUID bookId, int quantity) throws Exception {
        User customer = findCustomerByUsername(username);
        AddToCartDTO addToCartDTO = new AddToCartDTO(bookId, quantity);
        CartResponseDTO cart = shoppingCartService.addToCart(username, addToCartDTO);
        wishlistRepository.findByCustomerIdAndBookId(customer.getId(), bookId)
                .ifPresent(wishlistRepository::delete);
        return cart;
    }

    private User findCustomerByUsername(String username) throws Exception {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new Exception("Customer not found"));
    }

    private BookResponseDTO convertToDTO(Book book) {
        return BookResponseDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .price(book.getPrice())
                .stockQuantity(book.getStockQuantity())
                .isAvailable(book.getIsAvailable())
                .tenantId(book.getTenantId())
                .createdAt(book.getCreatedAt())
                .build();
    }
}
