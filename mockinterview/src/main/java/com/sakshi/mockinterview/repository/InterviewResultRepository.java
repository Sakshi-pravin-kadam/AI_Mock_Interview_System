package com.sakshi.mockinterview.repository;

import com.sakshi.mockinterview.entity.InterviewResult;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface InterviewResultRepository extends JpaRepository<InterviewResult, Long> {
}