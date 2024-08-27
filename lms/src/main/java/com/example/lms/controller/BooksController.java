package com.example.lms.controller;

import com.example.lms.dto.BookInDTO;
import com.example.lms.dto.BookOutDTO;
import com.example.lms.dto.CategoryOutDTO;
import com.example.lms.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/books")
public class BooksController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public ResponseEntity<List<BookOutDTO>> getAllBooks() {
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getAllBooks());
    }

    @GetMapping("/paginatedBooks")
    public ResponseEntity<Page<BookOutDTO>> getPaginatedBooks(@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize, @RequestParam(required = false) String search){
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getBooksPaginated(pageNumber, pageSize, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookOutDTO> getBookById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getBookById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BookOutDTO> deleteBookById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(bookService.deleteBookById(id));
    }

    @GetMapping("/bookCount")
    public ResponseEntity<Long> countAllBooks() {
        return ResponseEntity.status(HttpStatus.OK).body(bookService.countAllUniqueBooks());
    }

    @PostMapping("/addBook")
    public ResponseEntity<BookOutDTO> createBook(@RequestBody BookInDTO bookInDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(bookService.createBook(bookInDTO));
    }

    @GetMapping("/{title}")
    public ResponseEntity<BookOutDTO> getBookByTitle(@PathVariable String title) {
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getBookByTitle(title));
    }

    @GetMapping("/{categoryName}")
    public ResponseEntity<List<BookOutDTO>> getBooksByCategory(@PathVariable String categoryName) {
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getBooksByCategory(categoryName));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookOutDTO> updateBookById(@PathVariable Long id, @RequestBody BookInDTO bookInDTO){
        return ResponseEntity.status(HttpStatus.OK).body(bookService.updateBookById(id, bookInDTO));
    }
}