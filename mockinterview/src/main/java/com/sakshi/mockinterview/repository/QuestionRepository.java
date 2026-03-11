package com.sakshi.mockinterview.repository;

import com.sakshi.mockinterview.entity.Question;
import com.sakshi.mockinterview.entity.DifficultyLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    // Find questions by category
    List<Question> findByCategoryId(Long categoryId);

    // Find by difficulty
    List<Question> findByDifficulty(DifficultyLevel difficulty);

    // Find active questions only
    List<Question> findByActiveTrue();

    // Find by category and difficulty
    List<Question> findByCategoryIdAndDifficulty(Long categoryId, DifficultyLevel difficulty);
}