package com.sakshi.mockinterview.repository;

import com.sakshi.mockinterview.entity.InterviewResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewResultRepository
        extends JpaRepository<InterviewResult, Long> {

    // ✅ Get all results of a user (latest first)
    List<InterviewResult> findByUserIdOrderByCreatedAtDesc(Long userId);

    // ✅ Get results oldest → newest (for chart)
    List<InterviewResult> findByUserIdOrderByCreatedAtAsc(Long userId);

    // ✅ Count total interviews of user
    long countByUserId(Long userId);
}