package com.sakshi.mockinterview.entity;

import jakarta.persistence.*;

@Entity
public class InterviewSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String domain;

    private String topic;

    private String difficulty;

    private int questionNumber;

}