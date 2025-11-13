package com.example.demo.controller;


import com.example.demo.dto.BookRequestDTO;
import com.example.demo.dto.BookResponseDTO;
import com.example.demo.dto.BookUpdateDTO;
import com.example.demo.dto.StockUpdateDTO;
import com.example.demo.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/shopkeeper/books")
@PreAuthorize("hasRole('SHOPKEEPER')")
public class ShopKeeperController {

    @Autowired
    private BookService bookService;

    @PostMapping
    public ResponseEntity<BookResponseDTO> createBook(
            @Valid @RequestBody BookRequestDTO bookRequestDTO
    ) throws Exception{
        BookResponseDTO bookResponseDTO = bookService.createBook(bookRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookResponseDTO);
    }

    @GetMapping
    public ResponseEntity<List<BookResponseDTO>> getAllBooks() throws Exception{
        var bookList=bookService.getAllBooksForShopKeeper();
        return ResponseEntity.ok(bookList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDTO> getBook(@PathVariable UUID id) throws Exception{
        BookResponseDTO bookResponseDTO=bookService.getBookByIdForShopkeeper(id);
        return ResponseEntity.status(HttpStatus.OK).body(bookResponseDTO);
    }

    @PostMapping("/{id}")
    public ResponseEntity<BookResponseDTO> updateBook(
            @PathVariable UUID id,
            @Valid @RequestBody BookUpdateDTO bookRequestDTO) throws Exception
    {
        BookResponseDTO bookResponseDTO=bookService.updateBook(id,bookRequestDTO);
        return  ResponseEntity.status(HttpStatus.OK).body(bookResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable UUID id) throws Exception{
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<BookResponseDTO> updateStock(
            @PathVariable UUID id,
            @Valid @RequestBody StockUpdateDTO stockRequestDTO) throws Exception
    {
        BookResponseDTO bookResponseDTO=bookService.stockUpdate(id,stockRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(bookResponseDTO);
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookResponseDTO>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) boolean isAvailable
    ) throws Exception{
        List<BookResponseDTO> bookResponseDTOList=bookService.searchBook(title,author,isAvailable);
        return ResponseEntity.status(HttpStatus.OK).body(bookResponseDTOList);
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<BookResponseDTO>> lowStockBooks(
            @RequestParam(defaultValue = "5") Integer threshold
    ) throws Exception{
        List<BookResponseDTO> all=bookService.getAllBooksForShopKeeper();
        List<BookResponseDTO> lowStockBooks=all.stream()
                .filter(i -> i.getStockQuantity()<=threshold)
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(lowStockBooks);
    }
}
