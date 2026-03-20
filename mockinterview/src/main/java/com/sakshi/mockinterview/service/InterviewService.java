package com.sakshi.mockinterview.service;

import com.sakshi.mockinterview.entity.InterviewResult;
import com.sakshi.mockinterview.entity.InterviewSession;
import com.sakshi.mockinterview.repository.InterviewResultRepository;
import com.sakshi.mockinterview.repository.InterviewSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
public class InterviewService {

    private final WebClient webClient = WebClient.create("http://localhost:11434");

    @Autowired
    private InterviewSessionRepository sessionRepository;

    @Autowired
    private InterviewResultRepository resultRepository;

    public Map<String, Object> startInterview(Map<String, String> body) {

        String domain = body.get("domain");
        String topic = body.get("topic");
        String difficulty = body.get("difficulty");
        Long userId = Long.parseLong(body.get("userId"));

        int totalQuestions = getTotalQuestions(difficulty);
        int timer = getTimer(difficulty);

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

        sessionRepository.save(session); // ✅ SAVE FIRST

        Map<String, Object> result = new HashMap<>();
        result.put("sessionId", sessionId);
        result.put("timer", timer);
        result.put("currentQuestion", 1);
        result.put("totalQuestions", totalQuestions);

        result.put("domain", domain);
        result.put("topic", topic);
        result.put("difficulty", difficulty);

        return result;
    }

    public Map<String, Object> getFirstQuestion(Map<String, String> body) {

        String sessionId = body.get("sessionId");

        InterviewSession session = sessionRepository
                .findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        String prompt = buildFirstQuestionPrompt(
                session.getDomain(),
                session.getTopic(),
                session.getDifficulty()
        );

        String question = callAI(prompt);

        Map<String, Object> result = new HashMap<>();
        // ✅ ADD THESE
        result.put("question", question);
        result.put("domain", session.getDomain());
        result.put("topic", session.getTopic());
        result.put("difficulty", session.getDifficulty());
        result.put("totalQuestions", session.getTotalQuestions());

        return result;
    }

    public Map<String, Object> processAnswer(Map<String, String> body) {

        String sessionId = body.get("sessionId");
        String userAnswer = body.get("answer");

        InterviewSession session = sessionRepository
                .findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        int current = session.getCurrentQuestion();
        int total = session.getTotalQuestions();
        boolean completed = current == total;

        String prompt = buildEvaluationPrompt(userAnswer, completed);
        String aiResponse = callAI(prompt);

        // ✅ DEBUG LOG
        System.out.println("AI RAW RESPONSE:\n" + aiResponse);

        Map<String, Object> parsed = parseAIResponse(aiResponse, completed);

        int score = (int) parsed.get("score");
        String feedback = (String) parsed.get("feedback");

        session.setTotalScore(session.getTotalScore() + score);
        session.setCurrentQuestion(current + 1);
        sessionRepository.save(session);

        Map<String, Object> result = new HashMap<>();
        result.put("feedback", feedback);
        result.put("score", score);
        result.put("completed", completed);
        result.put("currentQuestion", current + 1);

        if (!completed) {
            result.put("nextQuestion", parsed.get("question"));
        } else {
            Map<String, String> analysis = analyzePerformance(session);

            saveFinalResult(session, analysis.get("bestTopic"), analysis.get("weakTopic"));

            result.put("finalScore", session.getTotalScore());
            result.put("totalQuestions", session.getTotalQuestions());
            result.put("maxScore", session.getTotalQuestions() * 10); // each question = 10 marks

            result.put("bestTopic", analysis.get("bestTopic"));
            result.put("weakTopic", analysis.get("weakTopic"));
        }

        return result;
    }

    private Map<String, String> analyzePerformance(InterviewSession session) {

        String prompt = """
            You are an interview evaluator.
            
            Domain: %s
            Topic: %s
            Total Score: %d out of %d
            
            Return STRICT format (NO explanation, ONLY 1-3 words):
            
            BEST_TOPIC: <only topic name, max 3 words>
            WEAK_TOPIC: <only topic name, max 3 words>
            
            Example:
            BEST_TOPIC: Polymorphism
            WEAK_TOPIC: Abstract Classes
            """.formatted(
                            session.getDomain(),
                            session.getTopic(),
                            session.getTotalScore(),
                            session.getTotalQuestions() * 10
                    );

        String response = callAI(prompt);

        String bestTopic = "N/A";
        String weakTopic = "N/A";

        try {
            int b = response.indexOf("BEST_TOPIC:");
            int w = response.indexOf("WEAK_TOPIC:");

            if (b != -1 && w != -1) {
                bestTopic = response.substring(b + 11, w).trim();
                weakTopic = response.substring(w + 11).trim();

                // ✅ CLEAN: take only first line
                bestTopic = bestTopic.split("\n")[0].trim();
                weakTopic = weakTopic.split("\n")[0].trim();

                // ✅ LIMIT LENGTH (critical)
                if (bestTopic.length() > 50) {
                    bestTopic = bestTopic.substring(0, 50);
                }
                if (weakTopic.length() > 50) {
                    weakTopic = weakTopic.substring(0, 50);
                }
            }
        } catch (Exception e) {
            bestTopic = "General Concepts";
            weakTopic = "Needs Improvement";
        }

        Map<String, String> result = new HashMap<>();
        result.put("bestTopic", bestTopic);
        result.put("weakTopic", weakTopic);

        return result;
    }


    // ---------------- HELPER METHODS ----------------

    private int getTotalQuestions(String difficulty) {
        return "easy".equalsIgnoreCase(difficulty) ? 3 :
                "hard".equalsIgnoreCase(difficulty) ? 8 : 5;
    }

    private int getTimer(String difficulty) {
        return "easy".equalsIgnoreCase(difficulty) ? 10 :
                "hard".equalsIgnoreCase(difficulty) ? 30 : 20;
    }

    private String buildFirstQuestionPrompt(String domain, String topic, String difficulty) {
        return """
            You are a technical interviewer.
            Domain: %s
            Topic: %s
            Difficulty: %s
            Ask the FIRST interview question.
            Return ONLY the question text.
            """.formatted(domain, topic, difficulty);
    }

    private String buildEvaluationPrompt(String answer, boolean completed) {
        return completed ?
                """
                You are a technical interviewer.
                Candidate Answer:
                %s
                Evaluate the answer.
                Return STRICT format:
                SCORE: <number out of 10>
                FEEDBACK: <short feedback>
                """.formatted(answer)
                :
                """
                You are a technical interviewer.
                Candidate Answer:
                %s
                Evaluate and provide NEXT QUESTION.
                Return STRICT format:
                SCORE: <number out of 10>
                FEEDBACK: <short feedback>
                QUESTION: <next question>
                """.formatted(answer);
    }

    private String callAI(String prompt) {
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

        return Optional.ofNullable(response.get("response"))
                .map(Object::toString)
                .orElse("")
                .replace("\"", "")
                .trim();
    }

    private Map<String, Object> parseAIResponse(String aiResponse, boolean completed) {

        Map<String, Object> result = new HashMap<>();

        int score = 5;
        String feedback = "Could not evaluate properly";
        String question = "";

        try {
            String text = aiResponse.toUpperCase();

            int s = text.indexOf("SCORE");
            int f = text.indexOf("FEEDBACK");
            int q = text.indexOf("QUESTION");

            if (s != -1 && f != -1) {

                String scoreText = aiResponse.substring(s, f)
                        .replaceAll("[^0-9]", ""); // extract number safely

                if (!scoreText.isEmpty()) {
                    score = Integer.parseInt(scoreText);
                    score = Math.max(0, Math.min(score, 10));
                }

                if (!completed && q != -1) {
                    feedback = aiResponse.substring(f + 8, q).trim();
                    question = aiResponse.substring(q + 8).trim();
                } else {
                    feedback = aiResponse.substring(f + 8).trim();
                }
            }

        } catch (Exception e) {
            feedback = "AI response format issue";
        }

        result.put("score", score);
        result.put("feedback", feedback);
        result.put("question", question);

        return result;
    }

    private void saveFinalResult(InterviewSession session, String bestTopic, String weakTopic) {

        InterviewResult result = new InterviewResult();

        result.setSessionId(session.getSessionId());
        result.setDomain(session.getDomain());
        result.setTopic(session.getTopic());
        result.setDifficulty(session.getDifficulty());
        result.setTotalQuestions(session.getTotalQuestions());
        result.setTotalScore(session.getTotalScore());

        result.setBestTopic(bestTopic);
        result.setWeakTopic(weakTopic);

        resultRepository.save(result);
    }
}