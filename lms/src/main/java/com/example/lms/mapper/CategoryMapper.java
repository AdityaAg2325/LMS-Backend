package com.example.lms.mapper;

import com.example.lms.dto.CategoryOutDTO;
import com.example.lms.dto.CategoryInDTO;
import com.example.lms.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category toEntity(CategoryInDTO inDTO){
        Category category = new Category();
        category.setName(inDTO.getName());
        return category;
    }

    public CategoryOutDTO toDTO(Category category){
        CategoryOutDTO outDTO = new CategoryOutDTO();
        outDTO.setId(category.getId());
        outDTO.setName(category.getName());
        return outDTO;
    }
}
