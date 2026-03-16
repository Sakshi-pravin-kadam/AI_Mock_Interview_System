package com.sakshi.mockinterview.controller;

import com.sakshi.mockinterview.entity.InterviewResult;
import com.sakshi.mockinterview.entity.InterviewSession;
import com.sakshi.mockinterview.repository.InterviewResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@RestController
@RequestMapping("/api")
public class InterviewController {

    private final WebClient webClient = WebClient.create("http://localhost:11434");

    private final Map<String, InterviewSession> activeSessions = new HashMap<>();

    @Autowired
    private InterviewResultRepository resultRepository;

    // START INTERVIEW
    @PostMapping("/start-interview")
    public Map<String, Object> startInterview(@RequestBody Map<String, String> body) {

        String domain = body.get("domain");
        String topic = body.get("topic");
        String difficulty = body.get("difficulty");

        int totalQuestions = "easy".equalsIgnoreCase(difficulty) ? 3 :
                "hard".equalsIgnoreCase(difficulty) ? 8 : 5;

        int timer = "easy".equalsIgnoreCase(difficulty) ? 10 :
                "hard".equalsIgnoreCase(difficulty) ? 30 : 20;

        String sessionId = UUID.randomUUID().toString();

        InterviewSession session = new InterviewSession();
        session.setSessionId(sessionId);
        session.setDomain(domain);
        session.setTopic(topic);
        session.setDifficulty(difficulty);
        session.setTotalQuestions(totalQuestions);
        session.setCurrentQuestion(1);
        session.setTotalScore(0);
        session.setQuestionHistory(new ArrayList<>());
        session.setScores(new ArrayList<>());

        activeSessions.put(sessionId, session);

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

        session.getQuestionHistory().add(question);

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

        InterviewSession session = activeSessions.get(sessionId);

        if (session == null) {
            throw new RuntimeException("Session expired");
        }

        int current = session.getCurrentQuestion();
        int total = session.getTotalQuestions();

        boolean completed = current >= total;

        String previousQuestions = String.join("\n", session.getQuestionHistory());

        String prompt = """
You are a technical interviewer.

Candidate Answer:
%s

Previous Questions:
%s

Evaluate the answer.

Return STRICT format:

SCORE:
<number out of 10>

FEEDBACK:
<short feedback>

QUESTION:
<new interview question different from previous ones>
""".formatted(userAnswer, previousQuestions);

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

            if (scoreIndex != -1 && feedbackIndex != -1 && questionIndex != -1) {

                score = Integer.parseInt(
                        aiResponse.substring(scoreIndex + 6, feedbackIndex).trim()
                );

                feedback = aiResponse
                        .substring(feedbackIndex + 9, questionIndex)
                        .trim();

                nextQuestion = aiResponse
                        .substring(questionIndex + 9)
                        .replace("\"", "")
                        .trim();
            }
        }

        // store score
        session.getScores().add(score);

        // accumulate total score
        session.setTotalScore(session.getTotalScore() + score);

        // adaptive difficulty
        String adaptiveDifficulty = session.getDifficulty();

        if (score >= 8) adaptiveDifficulty = "hard";
        else if (score >= 5) adaptiveDifficulty = "medium";
        else adaptiveDifficulty = "easy";

        session.setDifficulty(adaptiveDifficulty);

        // add to history
        session.getQuestionHistory().add(nextQuestion);

        session.setCurrentQuestion(current + 1);

        Map<String, Object> result = new HashMap<>();
        result.put("feedback", feedback);
        result.put("score", score);
        result.put("nextQuestion", nextQuestion);
        result.put("completed", completed);
        result.put("currentQuestion", current + 1);

        // FINALIZE INTERVIEW
        if (completed) {

            String analysisPrompt = """
Interview topic: %s

Scores: %s

Tell:

BEST_TOPIC:
<topic>

WEAK_TOPIC:
<topic>
""".formatted(session.getTopic(), session.getScores().toString());

            Map<String, Object> analysisRequest = Map.of(
                    "model", "llama3",
                    "prompt", analysisPrompt,
                    "stream", false
            );

            Map analysisResponse = webClient.post()
                    .uri("/api/generate")
                    .bodyValue(analysisRequest)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            String analysis = (String) analysisResponse.get("response");

            String bestTopic = session.getTopic();
            String weakTopic = session.getTopic();

            if (analysis != null) {

                int bestIndex = analysis.indexOf("BEST_TOPIC:");
                int weakIndex = analysis.indexOf("WEAK_TOPIC:");

                if (bestIndex != -1 && weakIndex != -1) {

                    bestTopic = analysis
                            .substring(bestIndex + 11, weakIndex)
                            .trim();

                    weakTopic = analysis
                            .substring(weakIndex + 11)
                            .trim();
                }
            }

            InterviewResult finalResult = new InterviewResult();

            finalResult.setSessionId(sessionId);
            finalResult.setDomain(session.getDomain());
            finalResult.setTopic(session.getTopic());
            finalResult.setDifficulty(session.getDifficulty());
            finalResult.setTotalQuestions(session.getTotalQuestions());
            finalResult.setTotalScore(session.getTotalScore());
            finalResult.setBestTopic(bestTopic);
            finalResult.setWeakTopic(weakTopic);

            resultRepository.save(finalResult);

            activeSessions.remove(sessionId);

            result.put("finalScore", session.getTotalScore());
            result.put("bestTopic", bestTopic);
            result.put("weakTopic", weakTopic);
        }

        return result;
    }
}