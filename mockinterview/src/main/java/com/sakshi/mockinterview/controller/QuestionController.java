package com.sakshi.mockinterview.controller;

import com.sakshi.mockinterview.entity.DifficultyLevel;
import com.sakshi.mockinterview.entity.Question;
import com.sakshi.mockinterview.service.QuestionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    // Create question
    @PostMapping
    public Question createQuestion(@RequestBody Question question) {
        return questionService.saveQuestion(question);
    }

    // Get all questions
    @GetMapping
    public List<Question> getAllQuestions() {
        return questionService.getAllQuestions();
    }

    // Filter by category
    @GetMapping("/category/{categoryId}")
    public List<Question> getByCategory(@PathVariable Long categoryId) {
        return questionService.getByCategory(categoryId);
    }

    // Filter by difficulty
    @GetMapping("/difficulty/{difficulty}")
    public List<Question> getByDifficulty(@PathVariable DifficultyLevel difficulty) {
        return questionService.getByDifficulty(difficulty);
    }

    // Filter by category and difficulty
    @GetMapping("/category/{categoryId}/difficulty/{difficulty}")
    public List<Question> getByCategoryAndDifficulty(
            @PathVariable Long categoryId,
            @PathVariable DifficultyLevel difficulty) {
        return questionService.getByCategoryAndDifficulty(categoryId, difficulty);
    }
}