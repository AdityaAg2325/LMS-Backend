package com.example.lms.controller;

import com.example.lms.dto.CategoryInDTO;
import com.example.lms.dto.CategoryOutDTO;
import com.example.lms.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryOutDTO>> getAllCategories() {
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.getAllCategories());
    }

    @GetMapping("/paginatedCategories")
    public ResponseEntity<Page<CategoryOutDTO>> getPaginatedCategories(@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize, @RequestParam String search){
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.getCategoriesPaginated(pageNumber, pageSize, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryOutDTO> getCategoryById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.getCategoryById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CategoryOutDTO> deleteCategoryById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.deleteCategoryById(id));
    }

    @GetMapping("/categoryCount")
    public ResponseEntity<Long> countAllCategories(){
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.countAllCategories());
    }

    @PostMapping("/addCategory")
    public ResponseEntity<CategoryOutDTO> createCategory(@RequestBody CategoryInDTO categoryInDTO){
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.createCategory(categoryInDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryOutDTO> updateCategoryById(@PathVariable Long id, @RequestBody CategoryInDTO categoryInDTO){
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.updateCategoryById(id, categoryInDTO));
    }
}
