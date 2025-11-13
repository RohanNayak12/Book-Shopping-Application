package com.example.demo.controller;

import com.example.demo.entity.Book;
import com.example.demo.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/books")
public class BookController {
//
//    @Autowired
//    private BookService bookService;
//
//    // No more tenantId parameters - handled by filter
//
//    @PostMapping
//    public ResponseEntity<Book> createBook(@Valid @RequestBody Book book) {
//        Book createdBook = bookService.createBook(book);
//        return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
//    }
//
//    @GetMapping
//    public ResponseEntity<List<Book>> getAllBooks() {
//        List<Book> books = bookService.getAllBooks();
//        return ResponseEntity.ok(books);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Book> getBookById(@PathVariable UUID id) {
//        Optional<Book> book = bookService.getBookById(id);
//        return book.map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.notFound().build());
//    }
//
//    @GetMapping("/search")
//    public ResponseEntity<List<Book>> searchBooks(
//            @RequestParam(required = false) String title,
//            @RequestParam(required = false) String author) {
//
//        if (title != null) {
//            return ResponseEntity.ok(bookService.searchByTitle(title));
//        } else if (author != null) {
//            return ResponseEntity.ok(bookService.searchByAuthor(author));
//        }
//
//        return ResponseEntity.badRequest().build();
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<Book> updateBook(
//            @PathVariable UUID id,
//            @Valid @RequestBody Book book) {
//
//        Optional<Book> existingBook = bookService.getBookById(id);
//        if (existingBook.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        book.setId(id);
//        Book updatedBook = bookService.updateBook(book);
//        return ResponseEntity.ok(updatedBook);
//    }
//
//    @PatchMapping("/{id}/stock")
//    public ResponseEntity<Book> updateBookStock(
//            @PathVariable UUID id,
//            @RequestParam Integer quantity) {
//
//        Optional<Book> bookOpt = bookService.getBookById(id);
//        if (bookOpt.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        Book book = bookOpt.get();
//        book.setStockQuantity(quantity);
//        Book updatedBook = bookService.updateBook(book);
//        return ResponseEntity.ok(updatedBook);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteBook(@PathVariable UUID id) {
//        Optional<Book> book = bookService.getBookById(id);
//        if (book.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        bookService.deleteBook(id);
//        return ResponseEntity.noContent().build();
//    }
}
