package com.kamehoot.kamehoot_backend.repos;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class CategoryRepository implements ICategoryRepository {

    private List<String> categories;

    public CategoryRepository() {
        this.categories = new ArrayList<String>();
        categories.add("Math");
        categories.add("Football");
    }

    @Override
    public List<String> getCategories() {
        return this.categories;
    }

}
