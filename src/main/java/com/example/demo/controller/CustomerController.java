package com.example.demo.controller;


import com.example.demo.dto.*;
import com.example.demo.enums.OrderStatus;
import com.example.demo.service.CustomerBookService;
import com.example.demo.service.OrderService;
import com.example.demo.service.ShoppingCartService;
import com.example.demo.service.WishlistService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customer")
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerController {

    @Autowired
    private CustomerBookService customerBookService;
    @Autowired
    private WishlistService wishlistService;
    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/books")
    public ResponseEntity<Page<BookResponseDTO>> browseBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ){
        Page<BookResponseDTO> books=customerBookService.browseBooks(page, size, sortBy, sortDir);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/books/search")
    public ResponseEntity<Page<BookResponseDTO>> searchBooks(
            @Valid @ModelAttribute BookSearchDTO searchDTO,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<BookResponseDTO> books = customerBookService.searchBooks(searchDTO, page, size);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<BookResponseDTO> getBookDetails(@PathVariable UUID id) throws Exception {
        BookResponseDTO book = customerBookService.getBookById(id);
        return ResponseEntity.ok(book);
    }

    @GetMapping("/books/category/{category}")
    public ResponseEntity<Page<BookResponseDTO>> getBooksByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<BookResponseDTO> books = customerBookService.getBooksByCategory(category, page, size);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/books/recommendations")
    public ResponseEntity<List<BookResponseDTO>> getRecommendations(Authentication authentication) {
        String username = authentication.getName();
        List<BookResponseDTO> recommendations = customerBookService.getRecommendations(username);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/cart")
    public ResponseEntity<CartResponseDTO> getCart(Authentication authentication) throws Exception {
        String username = authentication.getName();
        CartResponseDTO cart = shoppingCartService.getCart(username);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/cart/items")
    public ResponseEntity<CartResponseDTO> addToCart(
            @Valid @RequestBody AddToCartDTO addToCartDTO,
            Authentication authentication
    ) throws Exception {
        String username = authentication.getName();
        CartResponseDTO cart = shoppingCartService.addToCart(username, addToCartDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(cart);
    }

    @PutMapping("/cart/items/{itemId}")
    public ResponseEntity<CartResponseDTO> updateCartItem(
            @PathVariable UUID itemId,
            @Valid @RequestBody UpdateCartItemDTO updateDTO,
            Authentication authentication
    ) throws Exception {
        String username = authentication.getName();
        CartResponseDTO cart = shoppingCartService.updateCartItem(username, itemId, updateDTO);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/cart/items/{itemId}")
    public ResponseEntity<Void> removeFromCart(
            @PathVariable UUID itemId,
            Authentication authentication
    ) throws Exception {
        String username = authentication.getName();
        shoppingCartService.removeCartItem(username, itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/cart")
    public ResponseEntity<Void> clearCart(Authentication authentication) throws Exception {
        String username = authentication.getName();
        shoppingCartService.clearCart(username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/wishlist")
    public ResponseEntity<List<BookResponseDTO>> getWishlist(Authentication authentication) throws Exception {
        String username = authentication.getName();
        List<BookResponseDTO> wishlist = wishlistService.getWishlist(username);
        return ResponseEntity.ok(wishlist);
    }

    @PostMapping("/wishlist/{bookId}")
    public ResponseEntity<Void> addToWishlist(
            @PathVariable UUID bookId,
            Authentication authentication
    ) throws Exception {
        String username = authentication.getName();
        wishlistService.addToWishlist(username, bookId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/wishlist/{bookId}")
    public ResponseEntity<Void> removeFromWishlist(
            @PathVariable UUID bookId,
            Authentication authentication
    ) throws Exception {
        String username = authentication.getName();
        wishlistService.removeFromWishlist(username, bookId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/wishlist/{bookId}/move-to-cart")
    public ResponseEntity<CartResponseDTO> moveToCart(
            @PathVariable UUID bookId,
            @RequestParam(defaultValue = "1") int quantity,
            Authentication authentication
    ) throws Exception {
        String username = authentication.getName();
        CartResponseDTO cart = wishlistService.moveToCart(username, bookId, quantity);
        return ResponseEntity.ok(cart);
    }

    @Autowired
    private OrderService orderService;

    @PostMapping("/orders")
    public ResponseEntity<OrderResponseDTO> placeOrder(
            @Valid @RequestBody PlaceOrderDTO placeOrderDTO,
            Authentication authentication
    ) throws Exception {
        String username = authentication.getName();
        OrderResponseDTO order = orderService.placeOrder(username, placeOrderDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @PostMapping("/orders/checkout")
    public ResponseEntity<CheckoutResponseDTO> checkoutCart(
            @Valid @RequestBody CheckoutCartDTO checkoutCartDTO,
            Authentication authentication
    ) throws Exception {
        String username = authentication.getName();
        CheckoutResponseDTO responseDTO=orderService.checkOutCart(username, checkoutCartDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponseDTO>> getOrderHistory(Authentication authentication) throws Exception {
        String username = authentication.getName();
        List<OrderResponseDTO> orders = orderService.getOrderHistory(username);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrder(
            @PathVariable UUID orderId,
            Authentication authentication
    ) throws Exception {
        String username = authentication.getName();
        OrderResponseDTO order = orderService.getOrderById(username, orderId);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/orders/status/{status}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByStatus(
            @PathVariable OrderStatus status,
            Authentication authentication
    ) throws Exception {
        String username = authentication.getName();
        List<OrderResponseDTO> orders = orderService.getOrderByStatus(username, status);
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/orders/{orderId}/cancel")
    public ResponseEntity<OrderResponseDTO> cancelOrder(
            @PathVariable UUID orderId,
            @RequestParam(required = false) String reason,
            Authentication authentication
    ) throws Exception {
        String username = authentication.getName();
        OrderResponseDTO order = orderService.cancelOrder(username, orderId, reason);
        return ResponseEntity.ok(order);
    }

}
