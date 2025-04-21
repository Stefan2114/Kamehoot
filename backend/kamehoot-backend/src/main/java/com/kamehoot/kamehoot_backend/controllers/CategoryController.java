package com.kamehoot.kamehoot_backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kamehoot.kamehoot_backend.models.Category;
import com.kamehoot.kamehoot_backend.services.ICategoryService;

@RestController
@RequestMapping("/categories")
public class CategoryController implements ICategoryController {

    private ICategoryService categoryService;

    @Autowired
    public CategoryController(ICategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    @GetMapping
    public ResponseEntity<List<String>> getCategories() {

        return ResponseEntity.ok(this.categoryService.getCategories().stream().map(Category::getName)
                .toList());
    }
}
