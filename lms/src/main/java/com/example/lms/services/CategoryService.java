package com.example.lms.services;

import com.example.lms.dto.CategoryInDTO;
import com.example.lms.dto.CategoryOutDTO;
import com.example.lms.entity.Book;
import com.example.lms.entity.Category;
import com.example.lms.exception.ResourceAlreadyExistsException;
import com.example.lms.exception.ResourceNotFoundException;
import com.example.lms.mapper.CategoryMapper;
import com.example.lms.repository.BooksRepository;
import com.example.lms.repository.CategoryRepository;
import com.example.lms.repository.IssuanceRepository;
import com.example.lms.repository.UserRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private IssuanceRepository issuanceRepository;

    @Autowired
    private BooksRepository booksRespository;

    @Autowired
    private CategoryMapper categoryMapper;

    public CategoryOutDTO getCategoryById(Long id){
        Category category = categoryRepository.findCategoryById(id);

        CategoryOutDTO categoryOutDTO = categoryMapper.toDTO(category);
        return categoryOutDTO;
    }

    public Page<CategoryOutDTO> getCategoriesPaginated(int pageNumber, int pageSize, String sortBy, String sortDir, String search){
        Page<Category> page;
        Page<Category> categoryPage;
        if (search != null && !search.isEmpty()) {
            categoryPage = categoryRepository.findByNameContainingIgnoreCase(search, PageRequest.of(pageNumber,pageSize));
        } else {
            categoryPage = categoryRepository.findAll(PageRequest.of(pageNumber, pageSize, Sort.Direction.fromString(sortDir), sortBy));
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

        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Category", "id", id.toString())
        );

        boolean isBookIssued = issuanceRepository.existsByBookCategoryIdAndStatus(category.getId(), "Issued");

        if (isBookIssued) {
            throw new IllegalStateException("Cannot delete category as books from this category are currently issued.");
        }
        List<Book> books = booksRespository.findAllByCategory(category.getId());
        issuanceRepository.deleteAllByBookIn(books);
        booksRespository.deleteAll(books);
        categoryRepository.deleteById(category.getId());
    }

    public CategoryOutDTO updateCategoryById(Long id, CategoryInDTO categoryInDTO){

        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Category", "id", id.toString())
        );
        Optional<Category> thisCategory = categoryRepository.findByName(categoryInDTO.getName());
        if(thisCategory.isPresent()){
            Category newCategory = thisCategory.get();
            if(newCategory.getId() != category.getId()){
                throw new ResourceAlreadyExistsException("A category already exists with same name");
            }
        }

        Category categoryEntity = categoryMapper.toEntity(categoryInDTO);
        categoryEntity.setId(category.getId());

        Category updated = categoryRepository.save(categoryEntity);

        CategoryOutDTO categoryOutDTO = categoryMapper.toDTO(updated);
        return categoryOutDTO;
    }

    public CategoryOutDTO createCategory(CategoryInDTO categoryInDTO){

        Optional<Category> optionalCategory = categoryRepository.findByName(categoryInDTO.getName());
        if (optionalCategory.isPresent()) {
            throw new ResourceAlreadyExistsException("A category already exists with this name!");
        }
        Category category = categoryMapper.toEntity(categoryInDTO);
        Category savedCategory = categoryRepository.save(category);
        CategoryOutDTO categoryOutDTO = categoryMapper.toDTO(savedCategory);
        return categoryOutDTO;
    }
}
