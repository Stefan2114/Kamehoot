package com.kamehoot.kamehoot_backend.services;

import java.util.List;

import com.kamehoot.kamehoot_backend.models.Category;

public interface ICategoryService {

    List<Category> getCategories();
}
