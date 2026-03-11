package com.sakshi.mockinterview.service;

import com.sakshi.mockinterview.entity.DifficultyLevel;
import com.sakshi.mockinterview.entity.Question;
import com.sakshi.mockinterview.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public Question saveQuestion(Question question) {
        return questionRepository.save(question);
    }

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public List<Question> getByCategory(Long categoryId) {
        return questionRepository.findByCategoryId(categoryId);
    }

    public List<Question> getByDifficulty(DifficultyLevel difficulty) {
        return questionRepository.findByDifficulty(difficulty);
    }

    public List<Question> getByCategoryAndDifficulty(Long categoryId, DifficultyLevel difficulty) {
        return questionRepository.findByCategoryIdAndDifficulty(categoryId, difficulty);
    }
}