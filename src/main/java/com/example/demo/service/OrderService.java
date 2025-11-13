package com.example.demo.service;


import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.enums.OrderStatus;
import com.example.demo.repo.BookRepository;
import com.example.demo.repo.OrderRepository;
import com.example.demo.repo.ShoppingCartRepository;
import com.example.demo.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private ShoppingCartRepository cartRepository;

    public OrderResponseDTO placeOrder(String username, PlaceOrderDTO placeOrderDTO) throws Exception{
        User customer=findCustomerByUsername(username);
        Book book=bookRepository.findById(placeOrderDTO.getBookId())
                .orElseThrow(() -> new Exception("Book not found"));
        if(!book.getIsAvailable()) throw new Exception("Book is not available");
        if(book.getStockQuantity()<placeOrderDTO.getQuantity()) throw new Exception("Stock quantity less than order quantity");

        BigDecimal unitPrice=book.getPrice();
        BigDecimal totalPrice=unitPrice.multiply(BigDecimal.valueOf(placeOrderDTO.getQuantity()));

        Order order=Order.builder()
                .customer(customer)
                .book(book)
                .quantity(placeOrderDTO.getQuantity())
                .unitPrice(unitPrice)
                .totalPrice(totalPrice)
                .status(OrderStatus.PENDING)
                .build();

        book.setStockQuantity(book.getStockQuantity()-placeOrderDTO.getQuantity());
        if(book.getStockQuantity()==0) book.setIsAvailable(false);
        bookRepository.save(book);
        Order savedOrder=orderRepository.save(order);
        return convertToDTO(savedOrder);
    }

    public CheckoutResponseDTO checkOutCart(String username, CheckoutCartDTO checkoutCartDTO) throws Exception{
        User customer=findCustomerByUsername(username);
        ShoppingCart cart=cartRepository.findByCustomerId(customer.getId())
                .orElseThrow(() -> new Exception("Cart not found"));

        if(cart.getItems().isEmpty()) throw new Exception("Cart is empty");

        List<OrderResponseDTO> orders=new ArrayList<>();
        BigDecimal totalPrice=BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            Book book=cartItem.getBook();
            if(!book.getIsAvailable()) throw new Exception("Book is not available");
            if(book.getStockQuantity()<cartItem.getQuantity()) throw new Exception("Stock quantity less than order quantity");
            BigDecimal itemTotal=cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            Order order=Order.builder()
                    .customer(customer)
                    .book(book)
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getUnitPrice())
                    .totalPrice(itemTotal)
                    .status(OrderStatus.PENDING)
                    .build();
            book.setStockQuantity(book.getStockQuantity()-cartItem.getQuantity());
            if(book.getStockQuantity()==0) book.setIsAvailable(false);
            bookRepository.save(book);
            Order savedOrder=orderRepository.save(order);
            orders.add(convertToDTO(savedOrder));
            totalPrice=totalPrice.add(itemTotal);
        }
        cart.getItems().clear();
        cartRepository.save(cart);
        return CheckoutResponseDTO.builder()
                .orders(orders)
                .totalOrders(orders.size())
                .totalAmount(totalPrice)
                .message("Order has been placed successfully")
                .build();
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrderHistory(String username) throws Exception {
        User customer = findCustomerByUsername(username);
        List<Order> orders = orderRepository.findByCustomerId(customer.getId());

        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderById(String username, UUID orderId) throws Exception {
        User customer = findCustomerByUsername(username);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception("Order not found"));

        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new Exception("Unauthorized access to this order");
        }

        return convertToDTO(order);
    }

    public OrderResponseDTO updateOrderStatus(UUID orderId, UpdateOrderStatusDTO updateDTO) throws Exception {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception("Order not found"));
        validateStatusTransition(order.getStatus(), updateDTO.getStatus());
        order.setStatus(updateDTO.getStatus());
        Order savedOrder = orderRepository.save(order);
        return convertToDTO(savedOrder);
    }

    public OrderResponseDTO cancelOrder(String username, UUID orderId, String reason) throws Exception {
        User customer = findCustomerByUsername(username);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception("Order not found"));

        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new Exception("Unauthorized access to this order");
        }

        if (order.getStatus() != OrderStatus.PENDING &&
                order.getStatus() != OrderStatus.CONFIRMED) {
            throw new Exception("Cannot cancel order in " + order.getStatus() + " status");
        }

        Book book = order.getBook();
        book.setStockQuantity(book.getStockQuantity() + order.getQuantity());
        book.setIsAvailable(true);
        bookRepository.save(book);
        order.setStatus(OrderStatus.CANCELLED);
        order.setCancellationReason(reason);
        Order savedOrder = orderRepository.save(order);

        return convertToDTO(savedOrder);
    }

    @Transactional
    public List<OrderResponseDTO> getOrderByStatus(String username, OrderStatus orderStatus) throws Exception {
        User customer=findCustomerByUsername(username);
        List<Order> orders = orderRepository.findByCustomerId(customer.getId());
        return orders.stream()
                .filter(order -> order.getStatus()==orderStatus)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private User findCustomerByUsername(String username) throws Exception {
        return userRepository.findByUsername(username).orElseThrow(() -> new Exception("Customer not found"));
    }

    private OrderResponseDTO convertToDTO(Order order) {
        return OrderResponseDTO.builder()
                .id(order.getId())
                .customerId(order.getCustomer().getId())
                .customerName(order.getCustomer().getUsername())
                .bookId(order.getBook().getId())
                .bookTitle(order.getBook().getTitle())
                .bookAuthor(order.getBook().getAuthor())
                .quantity(order.getQuantity())
                .unitPrice(order.getUnitPrice())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .orderDate(order.getOrderDate())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    private void validateStatusTransition(OrderStatus curr, OrderStatus next) throws Exception {
        switch (curr) {
            case PENDING:
                if(next != OrderStatus.CONFIRMED && next != OrderStatus.CANCELLED) throw new Exception("Invalid status transition");
                break;
            case CONFIRMED:
                if (next != OrderStatus.SHIPPED && next != OrderStatus.CANCELLED) throw new Exception("Invalid status transition");
                break;
            case SHIPPED:
                if (next != OrderStatus.DELIVERED) throw new Exception("Invalid status transition");
                break;
            case DELIVERED:
            case CANCELLED:
                throw new Exception("Cannot change status");
            default:
                throw new Exception("Unknown status");
        }

    }

}
