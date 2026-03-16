package com.sakshi.mockinterview.repository;

import com.sakshi.mockinterview.entity.InterviewResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewResultRepository extends JpaRepository<InterviewResult, Long> {
}