package com.sakshi.mockinterview.controller;

import com.sakshi.mockinterview.entity.Category;
import com.sakshi.mockinterview.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // Create category
    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        return categoryService.saveCategory(category);
    }

    // Get all categories
    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.getRootCategories();
    }



    // Get subcategories
    @GetMapping("/{parentId}/sub")
    public List<Category> getSubCategories(@PathVariable Long parentId) {
        return categoryService.getSubCategories(parentId);
    }
}