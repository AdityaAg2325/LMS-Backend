package com.example.lms.mapper;

import com.example.lms.dto.IssuanceInDTO;
import com.example.lms.dto.IssuanceOutDTO;
import com.example.lms.entity.Issuance;
import com.example.lms.entity.User;
import com.example.lms.entity.Book;
import org.springframework.stereotype.Component;

@Component
public class IssuanceMapper {


    public static Issuance toEntity(IssuanceInDTO inDTO, User user, Book book) {
        Issuance issuance = new Issuance();
        issuance.setUser(user);
        issuance.setBook(book);
        issuance.setIssueTime(inDTO.getIssueTime());
        issuance.setStatus(inDTO.getStatus());
        issuance.setType(inDTO.getType());
        return issuance;
    }

    public static IssuanceOutDTO toDTO(Issuance issuance) {
        IssuanceOutDTO outDTO = new IssuanceOutDTO();
        outDTO.setId(issuance.getId());
        outDTO.setUser(UserMapper.toDTO(issuance.getUser()));
        outDTO.setBook(BookMapper.toDTO(issuance.getBook()));
        outDTO.setIssueTime(issuance.getIssueTime());
        outDTO.setReturnTime(issuance.getReturnTime());
        outDTO.setStatus(issuance.getStatus());
        outDTO.setType(issuance.getType());
        return outDTO;
    }
}
