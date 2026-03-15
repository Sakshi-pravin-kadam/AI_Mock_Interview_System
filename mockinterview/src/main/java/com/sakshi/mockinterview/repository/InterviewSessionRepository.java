package com.sakshi.mockinterview.repository;

import com.sakshi.mockinterview.entity.InterviewSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewSessionRepository
        extends JpaRepository<InterviewSession, Long> {
}