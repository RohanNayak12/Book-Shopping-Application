package com.example.demo.service;


import com.example.demo.dto.AddToCartDTO;
import com.example.demo.dto.CartItemDTO;
import com.example.demo.dto.CartResponseDTO;
import com.example.demo.dto.UpdateCartItemDTO;
import com.example.demo.entity.Book;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.ShoppingCart;
import com.example.demo.entity.User;
import com.example.demo.repo.BookRepository;
import com.example.demo.repo.CartItemRepository;
import com.example.demo.repo.ShoppingCartRepository;
import com.example.demo.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ShoppingCartService {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ShoppingCartRepository cartRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public CartResponseDTO getCart(String username) throws  Exception {
        User customer=findCustomerByUsername(username);
        ShoppingCart cart=cartRepository.findByCustomerId(customer.getId())
                .orElseGet(() -> createCart(customer));
        return convertDTO(cart);
    }

    public CartResponseDTO addToCart(String username, AddToCartDTO addToCartDTO) throws Exception{
        User customer=findCustomerByUsername(username);
        ShoppingCart cart=cartRepository.findByCustomerId(customer.getId())
                .orElseGet(()->createCart(customer));
        Book book=bookRepository.findById(addToCartDTO.getBookId())
                .orElseThrow(() -> new Exception("Book not found"));
        if(!book.getIsAvailable()) throw new Exception("Book is not available");
        if(book.getStockQuantity()<addToCartDTO.getQuantity())  throw new Exception("Stock quantity exceeded");
        CartItem existingItem=cart.getItems().stream()
                .filter(item -> item.getBook().getId().equals(book.getId()))
                .findFirst()
                .orElse(null);
        if(existingItem!=null){
            int quantity=existingItem.getQuantity()+addToCartDTO.getQuantity();
            if(book.getStockQuantity()<quantity) throw new Exception("Stock quantity exceeded");
            existingItem.setQuantity(quantity);
        }
        else {
            CartItem cartItem=CartItem.builder()
                    .cart(cart)
                    .book(book)
                    .quantity(addToCartDTO.getQuantity())
                    .unitPrice(book.getPrice())
                    .build();
            cart.addItem(cartItem);
        }
        ShoppingCart savedCart=cartRepository.save(cart);
        return convertDTO(savedCart);
    }

    public CartResponseDTO updateCartItem(String username, UUID itemId, UpdateCartItemDTO updateCartItemDTO) throws Exception{
        User customer=findCustomerByUsername(username);

        CartItem cartItem=cartItemRepository.findById(itemId).orElseThrow(() -> new Exception("Item not found"));

        if(!cartItem.getCart().getCustomer().getId().equals(customer.getId())) throw new Exception("Not authorized for cart changes");

        Book book=cartItem.getBook();
        if(book.getStockQuantity()<updateCartItemDTO.getQuantity())  throw new Exception("Stock quantity exceeded");

        cartItem.setQuantity(updateCartItemDTO.getQuantity());
        cartItemRepository.save(cartItem);
        ShoppingCart cart=cartRepository.findById(cartItem.getCart().getId()).orElseThrow(() -> new Exception("Cart not found"));
        return convertDTO(cart);
    }

    public void removeCartItem(String username, UUID itemId) throws Exception{
        User customer=findCustomerByUsername(username);
        CartItem cartItem=cartItemRepository.findById(itemId).orElseThrow(() -> new Exception("Item not found"));
        if(!cartItem.getCart().getCustomer().getId().equals(customer.getId())) throw new Exception("Not authorized for cart changes");
        cartItemRepository.delete(cartItem);
    }

    public void clearCart(String username) throws Exception {
        User customer = findCustomerByUsername(username);
        ShoppingCart cart = cartRepository.findByCustomerId(customer.getId())
                .orElseThrow(() -> new Exception("Cart not found"));
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private CartResponseDTO convertDTO(ShoppingCart cart) throws Exception {
        List<CartItemDTO> list= cart.getItems()==null? Collections.emptyList() :
                cart.getItems()
                .stream()
                .map(this::convertItemDTO)
                .collect(Collectors.toList());
        BigDecimal totalPrice=list.stream().map(CartItemDTO::getSubtotal).reduce(BigDecimal.ZERO,BigDecimal::add);
        int totalItems=list.stream().mapToInt(CartItemDTO::getQuantity).sum();
        return CartResponseDTO.builder()
                .cartId(cart.getId())
                .customerId(cart.getCustomer().getId())
                .items(list)
                .totalItems(totalItems)
                .totalPrice(totalPrice)
                .build();
    }

    private CartItemDTO convertItemDTO(CartItem cartItem) {
        return CartItemDTO.builder()
                .id(cartItem.getId())
                .bookId(cartItem.getBook().getId())
                .isAvailable(cartItem.getBook().getIsAvailable())
                .unitPrice(cartItem.getUnitPrice())
                .quantity(cartItem.getQuantity())
                .addedAt(cartItem.getAddedAt())
                .bookTitle(cartItem.getBook().getTitle())
                .bookAuthor(cartItem.getBook().getAuthor())
                .subtotal(cartItem.getSubtotal())
                .build();
    }

    private ShoppingCart createCart(User customer) {
        ShoppingCart cart=ShoppingCart
                .builder()
                .customer(customer)
                .build()
                ;
        return cartRepository.save(cart);
    }

    private User findCustomerByUsername(String username) throws Exception{
        return userRepository.findByUsername(username).orElseThrow(() -> new Exception("Customer not found"));
    }

}
