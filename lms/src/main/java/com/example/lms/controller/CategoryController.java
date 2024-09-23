package com.example.lms.controller;

import com.example.lms.constants.CategoryConstants;
import com.example.lms.dto.CategoryInDTO;
import com.example.lms.dto.CategoryOutDTO;
import com.example.lms.dto.ResponseDTO;
import com.example.lms.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ResponseEntity<Page<CategoryOutDTO>> getPaginatedCategories(@RequestParam(defaultValue = "0") int pageNumber,
                                                                       @RequestParam(defaultValue = "10") int pageSize,
                                                                       @RequestParam(defaultValue = "id") String sortBy,
                                                                       @RequestParam(defaultValue = "desc") String sortDir,
                                                                       @RequestParam(required = false) String search
    ){

        return ResponseEntity.status(HttpStatus.OK).body(categoryService.getCategoriesPaginated(pageNumber, pageSize, sortBy, sortDir, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryOutDTO> getCategoryById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.getCategoryById(id));
    }

    @GetMapping("/categoryCount")
    public ResponseEntity<Long> countAllCategories(){
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.countAllCategories());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> deleteCategoryById(@PathVariable Long id){
        categoryService.deleteCategoryById(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO(HttpStatus.OK.toString(), CategoryConstants.CATEGORY_DELETE_MSG));
    }

    @PostMapping("/addCategory")
    public ResponseEntity<ResponseDTO> createCategory(@RequestBody CategoryInDTO categoryInDTO){
        categoryService.createCategory(categoryInDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO(HttpStatus.CREATED.toString(), CategoryConstants.CATEGORY_CREATE_MSG));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO> updateCategoryById(@PathVariable Long id, @RequestBody CategoryInDTO categoryInDTO){
        categoryService.updateCategoryById(id, categoryInDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO(HttpStatus.OK.toString(), CategoryConstants.CATEGORY_UPDATE_MSG));
    }
}
