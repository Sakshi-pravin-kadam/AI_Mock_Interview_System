package com.sakshi.mockinterview.controller;

import com.sakshi.mockinterview.entity.InterviewResult;
import com.sakshi.mockinterview.entity.InterviewSession;
import com.sakshi.mockinterview.repository.InterviewResultRepository;
import com.sakshi.mockinterview.repository.InterviewSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@RestController
@RequestMapping("/api")
public class InterviewController {

    private final WebClient webClient = WebClient.create("http://localhost:11434");

    @Autowired
    private InterviewResultRepository resultRepository;

    @Autowired
    private InterviewSessionRepository sessionRepository;

    // START INTERVIEW
    @PostMapping("/start-interview")
    public Map<String, Object> startInterview(@RequestBody Map<String, String> body) {

        String domain = body.get("domain");
        String topic = body.get("topic");
        String difficulty = body.get("difficulty");
        Long userId = Long.parseLong(body.get("userId")); // ✅ NEW

        int totalQuestions = "easy".equalsIgnoreCase(difficulty) ? 3 :
                "hard".equalsIgnoreCase(difficulty) ? 8 : 5;

        int timer = "easy".equalsIgnoreCase(difficulty) ? 10 :
                "hard".equalsIgnoreCase(difficulty) ? 30 : 20;

        String sessionId = UUID.randomUUID().toString();

        InterviewSession session = new InterviewSession();
        session.setSessionId(sessionId);
        session.setUserId(userId);
        session.setDomain(domain);
        session.setTopic(topic);
        session.setDifficulty(difficulty);
        session.setTotalQuestions(totalQuestions);
        session.setCurrentQuestion(1);
        session.setTotalScore(0);

        // ✅ SAVE TO DB
        sessionRepository.save(session);

        // AI prompt
        String prompt = """
            You are a technical interviewer.
            Domain: %s
            Topic: %s
            Difficulty: %s
            Ask the FIRST interview question.
            Return ONLY the question text.
            """.formatted(domain, topic, difficulty);

        Map<String, Object> request = Map.of(
                "model", "llama3",
                "prompt", prompt,
                "stream", false
        );

        Map response = webClient.post()
                .uri("/api/generate")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        String question = ((String) response.get("response")).replace("\"", "").trim();

        Map<String, Object> result = new HashMap<>();
        result.put("sessionId", sessionId);
        result.put("question", question);
        result.put("timer", timer);
        result.put("currentQuestion", 1);
        result.put("totalQuestions", totalQuestions);

        return result;
    }

    // ANSWER + NEXT QUESTION
    @PostMapping("/question")
    public Map<String, Object> nextQuestion(@RequestBody Map<String, String> body) {

        String sessionId = body.get("sessionId");
        String userAnswer = body.get("answer");

        // ✅ FETCH FROM DB
        InterviewSession session = sessionRepository
                .findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        int current = session.getCurrentQuestion();
        int total = session.getTotalQuestions();
        boolean completed = current == total;

        String prompt = completed ?
                """
                You are a technical interviewer.
                Candidate Answer:
                %s
                Evaluate the answer.
                Return STRICT format:
                SCORE:
                <number out of 10>
                FEEDBACK:
                <short feedback>
                """.formatted(userAnswer) :
                """
                You are a technical interviewer.
                Candidate Answer:
                %s
                Evaluate the answer and provide NEXT QUESTION.
                Return STRICT format:
                SCORE:
                <number out of 10>
                FEEDBACK:
                <short feedback>
                QUESTION:
                <new interview question>
                """.formatted(userAnswer);

        Map<String, Object> request = Map.of(
                "model", "llama3",
                "prompt", prompt,
                "stream", false
        );

        Map response = webClient.post()
                .uri("/api/generate")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        String aiResponse = (String) response.get("response");

        int score = 0;
        String feedback = "";
        String nextQuestion = "";

        if (aiResponse != null) {
            int scoreIndex = aiResponse.indexOf("SCORE:");
            int feedbackIndex = aiResponse.indexOf("FEEDBACK:");
            int questionIndex = aiResponse.indexOf("QUESTION:");

            if (scoreIndex != -1 && feedbackIndex != -1) {
                try {
                    String scoreText = aiResponse.substring(scoreIndex + 6, feedbackIndex).trim();
                    if (scoreText.contains("/")) scoreText = scoreText.split("/")[0];
                    score = Integer.parseInt(scoreText.trim());
                    score = Math.max(0, Math.min(score, 10));
                } catch (Exception e) {
                    score = 5;
                }

                feedback = questionIndex != -1 ?
                        aiResponse.substring(feedbackIndex + 9, questionIndex).trim() :
                        aiResponse.substring(feedbackIndex + 9).trim();

                if (!completed && questionIndex != -1) {
                    nextQuestion = aiResponse.substring(questionIndex + 9).replace("\"", "").trim();
                }
            }
        }

        // ✅ UPDATE SESSION
        session.setTotalScore(session.getTotalScore() + score);
        session.setCurrentQuestion(current + 1);
        sessionRepository.save(session);

        Map<String, Object> result = new HashMap<>();
        result.put("feedback", feedback);
        result.put("score", score);
        result.put("completed", completed);
        result.put("currentQuestion", current + 1);

        if (!completed) result.put("nextQuestion", nextQuestion);

        // FINAL RESULT
        if (completed) {

            InterviewResult finalResult = new InterviewResult();
            finalResult.setSessionId(sessionId);
            finalResult.setDomain(session.getDomain());
            finalResult.setTopic(session.getTopic());
            finalResult.setDifficulty(session.getDifficulty());
            finalResult.setTotalQuestions(session.getTotalQuestions());
            finalResult.setTotalScore(session.getTotalScore());

            resultRepository.save(finalResult);

            result.put("finalScore", session.getTotalScore());
        }

        return result;
    }
}