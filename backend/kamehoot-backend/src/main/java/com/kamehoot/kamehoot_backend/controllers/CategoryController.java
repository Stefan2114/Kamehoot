package com.kamehoot.kamehoot_backend.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/categories")
public class CategoryController implements ICategoryController {

    @GetMapping
    public ResponseEntity<List<String>> getCategories() {
        List<String> categories = new ArrayList<String>();
        categories.add("Math");
        categories.add("Football");

        return ResponseEntity.ok(categories);
    }
}
