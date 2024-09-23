package com.example.lms.services;

import com.example.lms.dto.BookInDTO;
import com.example.lms.dto.BookOutDTO;
import com.example.lms.entity.Book;
import com.example.lms.entity.Category;
import com.example.lms.exception.ResourceAlreadyExistsException;
import com.example.lms.exception.ResourceNotFoundException;
import com.example.lms.mapper.BookMapper;
import com.example.lms.repository.BooksRepository;
import com.example.lms.repository.CategoryRepository;
import com.example.lms.repository.IssuanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BooksRepository booksRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private IssuanceRepository issuanceRepository;

    @Autowired
    private BookMapper bookMapper;

    public BookOutDTO getBookById(Long id){
        Book book = booksRepository.findBookById(id).orElseThrow(
                () -> new ResourceNotFoundException("Book", "id", id.toString())
        );

        BookOutDTO bookOutDTO = bookMapper.toDTO(book);
        return bookOutDTO;
    }

    public BookOutDTO getBookByTitle(String title){
        Book book = booksRepository.findBookByTitle(title).orElseThrow(
                () -> new ResourceNotFoundException("Book", "title", title)
        );;

        BookOutDTO bookOutDTO = bookMapper.toDTO(book);
        return bookOutDTO;
    }

    public List<BookOutDTO> getBooksByCategory(String categoryName){
        List<Book> books = booksRepository.findBooksByCategoryName(categoryName);
        List<BookOutDTO> bookOutDTOs = new ArrayList<>();
        for(Book book : books){
            bookOutDTOs.add(bookMapper.toDTO(book));
        }
        return bookOutDTOs;
    }

    public Long countAllUniqueBooks(){
        return booksRepository.count();
    }

    public List<BookOutDTO> getAllBooks(){
        List<Book> books = booksRepository.findAll();
        List<BookOutDTO> bookOutDTOs = new ArrayList<>();
        for(Book book: books){
            bookOutDTOs.add(bookMapper.toDTO(book));
        }
        return bookOutDTOs;
    }

    public BookOutDTO deleteBookById(Long id){
        Book book = booksRepository.findBookById(id).orElseThrow(
                () -> new ResourceNotFoundException("Book", "id", id.toString())
        );

        boolean isBookIssued = issuanceRepository.existsByBookIdAndStatus(book.getId(), "Issued");
        if(isBookIssued){
            throw new IllegalStateException("Book is currently issued, so it can't be deleted!");
        }
        issuanceRepository.deleteAllByBookIn(Collections.singletonList(book));
        booksRepository.deleteById(book.getId());
        BookOutDTO bookOutDTO = bookMapper.toDTO(book);
        return bookOutDTO;
    }

    public Page<BookOutDTO> getBooksPaginated(int pageNumber, int pageSize, String sortBy, String sortDir, String search){
        Page<Book> page;
        if (search!=null && !search.isEmpty()){
            page = booksRepository.findByTitleContainingIgnoreCase(search, PageRequest.of(pageNumber,pageSize));
        } else {
            page  = booksRepository.findAll(PageRequest.of(pageNumber,pageSize, Sort.Direction.fromString(sortDir), sortBy));
        }
        return page.map(book -> bookMapper.toDTO(book));
    }

    public BookOutDTO updateBookById(Long id, BookInDTO bookInDTO){

        Book book = booksRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Book", "id", id.toString())
        );

        Optional<Book> optionalBook = booksRepository.findByTitle(bookInDTO.getTitle());

        if (optionalBook.isPresent()) {
            Book otherBook = optionalBook.get();

            if (otherBook.getId() != book.getId()) {
                throw new ResourceAlreadyExistsException("Book already exists with same details");
            }
        }

        Long prevTotalQty = book.getQuantity();
        Long prevAvlQty = book.getCurrQty();

        Category category = categoryRepository.findById(bookInDTO.getCategoryId()).orElseThrow(
                () -> new ResourceNotFoundException("Category", "id", bookInDTO.getCategoryId().toString())
        );

        book = BookMapper.toEntity(bookInDTO, category);
        book.setId(id);

        Long newTotalQty = bookInDTO.getQuantity();
        Long newAvlQty = prevAvlQty + (newTotalQty-prevTotalQty);
        if (newAvlQty < 0) {
            newAvlQty = 0L;
        }
        book.setCurrQty(newAvlQty);

        Book savedBook = booksRepository.save(book);

        BookOutDTO bookOutDTO = BookMapper.toDTO(savedBook);

        return bookOutDTO;
    }

    public BookOutDTO createBook(BookInDTO bookInDTO){
        Optional<Book> optionalBook = booksRepository.findByTitle(bookInDTO.getTitle());
        if(optionalBook.isPresent()){
            throw new ResourceAlreadyExistsException("Book already exists with same details.");
        }
        Category category = categoryRepository.findCategoryById(bookInDTO.getCategoryId());
        Book book = bookMapper.toEntity(bookInDTO, category);
        Book savedBook = booksRepository.save(book);
        return bookMapper.toDTO(savedBook);
    }
}
