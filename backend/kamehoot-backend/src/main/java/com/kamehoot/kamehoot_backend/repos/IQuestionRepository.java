package com.kamehoot.kamehoot_backend.repos;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kamehoot.kamehoot_backend.models.Question;

@Repository
public interface IQuestionRepository extends JpaRepository<Question, UUID> {

    @Query("SELECT q FROM Question q ORDER BY q.creationDate DESC")
    List<Question> getAllSortedByCreationDateDesc();

}
