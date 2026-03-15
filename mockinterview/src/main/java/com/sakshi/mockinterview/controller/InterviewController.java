package com.sakshi.mockinterview.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class InterviewController {

    private final WebClient webClient =
            WebClient.create("http://localhost:11434");

    @PostMapping("/question")
    public String generateQuestion(@RequestBody Map<String,String> body){

        String domain = body.get("domain");
        String topic = body.get("topic");
        String difficulty = body.get("difficulty");
        String userAnswer = body.get("answer");

        String prompt = """
You are a technical interviewer.

Domain: %s
Topic: %s
Difficulty: %s

The candidate answered:
%s

Ask the NEXT interview question.
Return ONLY the question text.
""".formatted(domain, topic, difficulty, userAnswer);

        Map<String,Object> request = Map.of(
                "model","llama3",
                "prompt",prompt,
                "stream",false
        );

        Map response = webClient.post()
                .uri("/api/generate")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return (String) response.get("response");
    }
    @PostMapping("/start-interview")
    public String startInterview(@RequestBody Map<String,String> body){

        String domain = body.get("domain");
        String topic = body.get("topic");
        String difficulty = body.get("difficulty");

        String prompt = """
You are a technical interviewer.

Domain: %s
Topic: %s
Difficulty: %s

Ask the FIRST interview question.
Return ONLY the question text.
""".formatted(domain, topic, difficulty);

        Map<String,Object> request = Map.of(
                "model","llama3",
                "prompt",prompt,
                "stream",false
        );

        Map response = webClient.post()
                .uri("/api/generate")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return (String) response.get("response");
    }
}