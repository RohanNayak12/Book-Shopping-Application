package com.example.demo.service;

import com.example.demo.context.TenantContext;
import com.example.demo.dto.BookRequestDTO;
import com.example.demo.dto.BookResponseDTO;
import com.example.demo.dto.BookUpdateDTO;
import com.example.demo.dto.StockUpdateDTO;
import com.example.demo.entity.Book;
import com.example.demo.entity.Tenant;
import com.example.demo.repo.BookRepository;
import com.example.demo.repo.TenantRepository;
import com.example.demo.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookService {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TenantRepository tenantRepository;

    @Transactional
    public BookResponseDTO createBook(BookRequestDTO bookRequestDTO) throws  Exception {
        UUID tenantId= TenantContext.getTenantId();
        if (tenantId == null) {throw new Exception("Tenant Context not set");}

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new Exception("Tenant not found"));
        Book book = new Book();
        book.setTitle(bookRequestDTO.getTitle());
        book.setAuthor(bookRequestDTO.getAuthor());
        book.setPrice(bookRequestDTO.getPrice());
        book.setStockQuantity(bookRequestDTO.getStockQuantity());
        book.setTenantId(tenant.getId());
        book.setIsAvailable(bookRequestDTO.isAvailable());
        Book savedBook=bookRepository.save(book);
        return dtoMaker(savedBook);
    }

    @Transactional
    public List<BookResponseDTO> getAllBooksForShopKeeper() throws Exception{
        UUID tenantId= TenantContext.getTenantId();
        if (tenantId == null) {throw new Exception("Tenant Context not set");}

        List<Book> bookList=bookRepository.findByTenantId(tenantId);
        return bookList
                .stream()
                .map(this::dtoMaker)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookResponseDTO getBookByIdForShopkeeper(UUID id) throws Exception {
        UUID tenantId= TenantContext.getTenantId();
        if (tenantId == null) throw new Exception("Tenant Context not set");
        Book book=bookRepository.findById(id).orElseThrow(()->new Exception("Book not found"));
        if(!book.getTenantId().equals(tenantId)) throw new Exception("You don't have permission to access this book");
        return dtoMaker(book);
    }

    @Transactional
    public BookResponseDTO updateBook(UUID bookId, BookUpdateDTO bookUpdateDTO) throws Exception {
        UUID tenantId= TenantContext.getTenantId();
        if (tenantId == null) {throw new Exception("Tenant Context not set");}
        Book book=bookRepository.findById(bookId).orElseThrow(()->new Exception("Book not found"));
        if(!book.getTenantId().equals(tenantId)) throw new Exception("You don't have permission to access this book");

        if(bookUpdateDTO.getTitle()!=null) book.setTitle(bookUpdateDTO.getTitle());
        if(bookUpdateDTO.getAuthor()!=null) book.setAuthor(bookUpdateDTO.getAuthor());
        if(bookUpdateDTO.getPrice()!=null) book.setPrice(bookUpdateDTO.getPrice());
        if(bookUpdateDTO.getStockQuantity()!=null) book.setStockQuantity(bookUpdateDTO.getStockQuantity());
        if(bookUpdateDTO.getIsAvailable()!=null) book.setIsAvailable(bookUpdateDTO.getIsAvailable());

        Book savedBook=bookRepository.save(book);
        return dtoMaker(savedBook);
    }

    @Transactional
    public BookResponseDTO stockUpdate(UUID bookId, StockUpdateDTO stockUpdateDTO) throws Exception {
        UUID tenantId= TenantContext.getTenantId();
        if (tenantId == null) {throw new Exception("Tenant Context not set");}
        Book book=bookRepository.findById(bookId).orElseThrow(()->new Exception("Book not found"));
        if (!book.getTenantId().equals(tenantId)) throw new Exception("You don't have permission to access this book");

        book.setStockQuantity(stockUpdateDTO.getQuantity());
        book.setIsAvailable(book.getStockQuantity()>0);
        Book savedBook=bookRepository.save(book);
        return dtoMaker(savedBook);
    }

    @Transactional
    public void deleteBook(UUID bookId) throws Exception {
        UUID tenantId= TenantContext.getTenantId();
        if (tenantId == null) {throw new Exception("Tenant Context not set");}
        Book book=bookRepository.findById(bookId).orElseThrow(()->new Exception("Book not found"));
        if (!book.getTenantId().equals(tenantId)) throw new Exception("You don't have permission to access this book");

        bookRepository.delete(book);
    }

    @Transactional(readOnly=true)
    public List<BookResponseDTO> searchBook(String title,String author,Boolean isAvailable) throws Exception {
        UUID tenantId= TenantContext.getTenantId();
        if (tenantId == null) {throw new Exception("Tenant Context not set");}
        List<Book> bookList=bookRepository.findByTenantId(tenantId);
        return bookList.stream()
                .filter(book -> title == null || book.getTitle().toLowerCase().contains(title.toLowerCase()))
                .filter(book -> author == null || (book.getAuthor() != null &&
                        book.getAuthor().toLowerCase().contains(author.toLowerCase())))
                .filter(book -> isAvailable == null || book.getIsAvailable().equals(isAvailable))
                .map(this::dtoMaker)
                .collect(Collectors.toList());
    }

    private BookResponseDTO dtoMaker(Book savedBook) {
        return BookResponseDTO.builder()
                .id(savedBook.getId())
                .price(savedBook.getPrice())
                .isAvailable(savedBook.getIsAvailable())
                .createdAt(savedBook.getCreatedAt())
                .author(savedBook.getAuthor())
                .stockQuantity(savedBook.getStockQuantity())
                .title(savedBook.getTitle())
                .tenantId(savedBook.getTenantId() !=null ? savedBook.getTenantId():null)
                .build();
    }
}
