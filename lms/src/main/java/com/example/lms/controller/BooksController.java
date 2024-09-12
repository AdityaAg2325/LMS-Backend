package com.example.lms.controller;

import com.example.lms.constants.BookConstants;
import com.example.lms.dto.BookInDTO;
import com.example.lms.dto.BookOutDTO;
import com.example.lms.dto.CategoryOutDTO;
import com.example.lms.dto.ResponseDTO;
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

    @GetMapping("/bookCount")
    public ResponseEntity<Long> countAllBooks() {
        return ResponseEntity.status(HttpStatus.OK).body(bookService.countAllUniqueBooks());
    }

    @GetMapping("/{title}")
    public ResponseEntity<BookOutDTO> getBookByTitle(@PathVariable String title) {
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getBookByTitle(title));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> deleteBookById(@PathVariable Long id) {
        BookOutDTO bookOutDTO = bookService.deleteBookById(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO(HttpStatus.OK.toString(), BookConstants.BOOK_DELETE_MSG));
    }

    @PostMapping("/addBook")
    public ResponseEntity<ResponseDTO> createBook(@RequestBody BookInDTO bookInDTO) {
        BookOutDTO bookOutDTO = bookService.createBook(bookInDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO(HttpStatus.CREATED.toString(), BookConstants.BOOK_CREATE_MSG));
    }

//    @GetMapping("/{categoryName}")
//    public ResponseEntity<List<BookOutDTO>> getBooksByCategory(@PathVariable String categoryName) {
//        return ResponseEntity.status(HttpStatus.OK).body(bookService.getBooksByCategory(categoryName));
//    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO> updateBookById(@PathVariable Long id, @RequestBody BookInDTO bookInDTO){
        BookOutDTO bookOutDTO = bookService.updateBookById(id, bookInDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO(HttpStatus.OK.toString(), BookConstants.BOOK_UPDATE_MSG));
    }
}