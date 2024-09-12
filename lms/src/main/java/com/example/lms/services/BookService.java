package com.example.lms.services;

import com.example.lms.dto.BookInDTO;
import com.example.lms.dto.BookOutDTO;
import com.example.lms.dto.CategoryOutDTO;
import com.example.lms.dto.UserOutDTO;
import com.example.lms.entity.Book;
import com.example.lms.entity.Category;
import com.example.lms.entity.User;
import com.example.lms.exception.ResourceNotFoundException;
import com.example.lms.mapper.BookMapper;
import com.example.lms.repository.BooksRepository;
import com.example.lms.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {

    @Autowired
    private BooksRepository booksRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookMapper bookMapper;

    public BookOutDTO getBookById(Long id){
        Book book = booksRepository.findBookById(id);

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
        Book book = booksRepository.findBookById(id);

        booksRepository.deleteById(book.getId());
        BookOutDTO bookOutDTO = bookMapper.toDTO(book);
        return bookOutDTO;
    }

    public Page<BookOutDTO> getBooksPaginated(int pageNumber, int pageSize, String search){
        Page<Book> page;
        if (search!=null && !search.isEmpty()){
            page = booksRepository.findByTitleContainingIgnoreCase(search, PageRequest.of(pageNumber,pageSize));
        } else {
            page  = booksRepository.findAll(PageRequest.of(pageNumber,pageSize));
        }
        return page.map(book -> bookMapper.toDTO(book));
    }

    public BookOutDTO updateBookById(Long id, BookInDTO bookInDTO){
        Book book = booksRepository.findBookById(id);

        book.setQuantity(bookInDTO.getQuantity());
        book.setTitle(bookInDTO.getTitle());
        book.setAuthor(bookInDTO.getAuthor());
        book.setCurrQty(bookInDTO.getQuantity());
        Book updated = booksRepository.save(book);

        BookOutDTO bookOutDTO = bookMapper.toDTO(updated);
        return bookOutDTO;
    }

    public BookOutDTO createBook(BookInDTO bookInDTO){
        Category category = categoryRepository.findCategoryById(bookInDTO.getCategoryId());
        System.out.println(category);
        Book book = bookMapper.toEntity(bookInDTO, category);
        Book savedBook = booksRepository.save(book);
        return bookMapper.toDTO(savedBook);
    }
}
