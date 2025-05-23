package com.kamehoot.kamehoot_backend.repos;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kamehoot.kamehoot_backend.models.Category;

@Repository
public interface ICategoryRepository extends JpaRepository<Category, UUID> {

    Category findByName(String category);
}
