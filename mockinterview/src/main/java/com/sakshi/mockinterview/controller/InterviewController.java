package com.sakshi.mockinterview.controller;

import com.sakshi.mockinterview.service.InterviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class InterviewController {

    @Autowired
    private InterviewService interviewService;

    @PostMapping("/start-interview")
    public Map<String, Object> startInterview(@RequestBody Map<String, String> body) {
        return interviewService.startInterview(body);
    }

    @PostMapping("/question")
    public Map<String, Object> nextQuestion(@RequestBody Map<String, String> body) {
        return interviewService.processAnswer(body);
    }

    @PostMapping("/first-question")
    public Map<String, Object> getFirstQuestion(@RequestBody Map<String, String> body) {
        return interviewService.getFirstQuestion(body);
    }
}