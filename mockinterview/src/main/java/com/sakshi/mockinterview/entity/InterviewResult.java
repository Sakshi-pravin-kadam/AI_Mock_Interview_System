package com.sakshi.mockinterview.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "interview_result")
public class InterviewResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ ADD THIS (very important for dashboard filtering)
    private Long userId;

    private String sessionId;

    private String domain;
    private String topic;
    private String difficulty;

    private int totalQuestions;
    private int totalScore;

    // ✅ Store percentage directly (makes dashboard easier)
    private double percentage;

    // ✅ Needed for performance trend & recent interviews
    private LocalDateTime createdAt;

    @Column(columnDefinition = "TEXT")
    private String bestTopic;

    @Column(columnDefinition = "TEXT")
    private String weakTopic;

    public InterviewResult() {
        this.createdAt = LocalDateTime.now(); // auto timestamp
    }

    // ---------------- GETTERS & SETTERS ----------------

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getBestTopic() {
        return bestTopic;
    }

    public void setBestTopic(String bestTopic) {
        this.bestTopic = bestTopic;
    }

    public String getWeakTopic() {
        return weakTopic;
    }

    public void setWeakTopic(String weakTopic) {
        this.weakTopic = weakTopic;
    }
}