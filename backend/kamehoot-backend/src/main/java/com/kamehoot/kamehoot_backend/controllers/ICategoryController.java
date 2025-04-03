package com.kamehoot.kamehoot_backend.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;

public interface ICategoryController {

    ResponseEntity<List<String>> getCategories();
}
