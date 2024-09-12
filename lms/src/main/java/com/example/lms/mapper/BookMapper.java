package com.example.lms.mapper;

import com.example.lms.dto.BookInDTO;
import com.example.lms.dto.BookOutDTO;
import com.example.lms.entity.Book;
import com.example.lms.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public static Book toEntity(BookInDTO inDTO, Category category){
        Book book = new Book();
        book.setTitle(inDTO.getTitle());
        book.setAuthor(inDTO.getAuthor());
        book.setQuantity(inDTO.getQuantity());
        book.setCurrQty(inDTO.getQuantity());
        book.setImage(inDTO.getImage());
        book.setCategory(category);
        return book;
    }

    public static BookOutDTO toDTO(Book book){
        BookOutDTO outDTO = new BookOutDTO();
        outDTO.setId(book.getId());
        outDTO.setTitle(book.getTitle());
        outDTO.setAuthor(book.getAuthor());
        outDTO.setQuantity(book.getQuantity());
        outDTO.setCurrQty(book.getCurrQty());
        outDTO.setImage(book.getImage());
        outDTO.setCategoryName(book.getCategory().getName());
        return outDTO;
    }
}
