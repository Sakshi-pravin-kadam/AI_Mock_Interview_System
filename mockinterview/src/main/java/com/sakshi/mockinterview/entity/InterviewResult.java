package com.sakshi.mockinterview.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "interview_result")
public class InterviewResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sessionId;

    private String domain;
    private String topic;
    private String difficulty;

    private int totalQuestions;
    private int totalScore;

    private String bestTopic;
    private String weakTopic;

    public InterviewResult() {}

    public Long getId() {
        return id;
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