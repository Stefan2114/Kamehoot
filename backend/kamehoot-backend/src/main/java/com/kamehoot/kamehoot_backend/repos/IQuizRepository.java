package com.kamehoot.kamehoot_backend.repos;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kamehoot.kamehoot_backend.models.Quiz;

@Repository
public interface IQuizRepository extends JpaRepository<Quiz, UUID> {

    @Query("SELECT q FROM Quiz q WHERE q.deleted = false ORDER BY q.creationDate DESC")
    List<Quiz> findAllQuizzes();

    @Query("SELECT q FROM Quiz q WHERE q.creator.username = :username AND q.deleted = false ORDER BY q.creationDate DESC")
    List<Quiz> findByCreatorUsername(@Param("username") String username);

}
