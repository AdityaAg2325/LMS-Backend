package com.example.lms.services;

import com.example.lms.dto.CategoryInDTO;
import com.example.lms.dto.CategoryOutDTO;
import com.example.lms.entity.Category;
import com.example.lms.mapper.CategoryMapper;
import com.example.lms.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    public CategoryOutDTO getCategoryById(Long id){
        Category category = categoryRepository.findCategoryById(id);

        CategoryOutDTO categoryOutDTO = categoryMapper.toDTO(category);
        return categoryOutDTO;
    }

//    public Page<CategoryOutDTO> getCategoriesPaginated(int pageNumber, int pageSize, String search){
//        Page<Category> page;
//        if(search!=null && !search.isEmpty()){
//            page = categoryRepository.findByNameContainingIgnoreCase(search, PageRequest.of(pageNumber,pageSize));
//        } else {
//            page = categoryRepository.findAll(PageRequest.of(pageNumber,pageSize));
//        }
//
//        return page.map(category -> categoryMapper.toDTO(category));
//    }

    public Page<CategoryOutDTO> getCategoriesPaginated(Pageable pageable, String search){
        Page<Category> page;
        Page<Category> categoryPage;
        if (search != null && !search.isEmpty()) {
            categoryPage = categoryRepository.findByNameContainingIgnoreCase(search, pageable);
        } else {
            categoryPage = categoryRepository.findAll(pageable);
        }
        return categoryPage.map(category -> categoryMapper.toDTO(category));
    }

    public Long countAllCategories(){
        return categoryRepository.count();
    }

    public List<CategoryOutDTO> getAllCategories(){
        List<Category> categories = categoryRepository.findAll();
        List<CategoryOutDTO> categoryDTOs = new ArrayList<>();
        for(Category category: categories){
            categoryDTOs.add(categoryMapper.toDTO(category));
        }
        return categoryDTOs;
    }

    public void deleteCategoryById(Long id){
        Category category = categoryRepository.findCategoryById(id);

        categoryRepository.deleteById(category.getId());
       // CategoryOutDTO categoryOutDTO = categoryMapper.toDTO(category);
    }

    public CategoryOutDTO updateCategoryById(Long id, CategoryInDTO categoryInDTO){
        Category category = categoryRepository.findCategoryById(id);

        Category categoryEntity = categoryMapper.toEntity(categoryInDTO);
        categoryEntity.setId(category.getId());

        Category updated = categoryRepository.save(categoryEntity);

        CategoryOutDTO categoryOutDTO = categoryMapper.toDTO(updated);
        return categoryOutDTO;
    }

    public CategoryOutDTO createCategory(CategoryInDTO categoryInDTO){
        Category category = categoryMapper.toEntity(categoryInDTO);
        Category savedCategory = categoryRepository.save(category);
        CategoryOutDTO categoryOutDTO = categoryMapper.toDTO(savedCategory);
        return categoryOutDTO;
    }
}
