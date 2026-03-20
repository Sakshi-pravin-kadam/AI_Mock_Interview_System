package com.sakshi.mockinterview.service;
import java.util.stream.Collectors;
import com.sakshi.mockinterview.entity.InterviewResult;
import com.sakshi.mockinterview.repository.InterviewResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DashboardService {

    @Autowired
    private InterviewResultRepository resultRepository;

    public Map<String, Object> getDashboardData(Long userId) {

        List<InterviewResult> results =
                resultRepository.findByUserIdOrderByCreatedAtDesc(userId);

        Map<String, Object> response = new HashMap<>();

        if (results.isEmpty()) {
            response.put("totalInterviews", 0);
            response.put("averageScore", 0);
            response.put("bestTopic", "N/A");
            response.put("weakTopic", "N/A");
            response.put("performance", new ArrayList<>());
            response.put("recent", new ArrayList<>());
            return response;
        }

        // ✅ Total Interviews
        long totalInterviews = results.size();

        // ✅ Average Score
        double avg = results.stream()
                .mapToDouble(InterviewResult::getPercentage)
                .average()
                .orElse(0);

        // ✅ Strongest Topic (most frequent bestTopic)
        String bestTopic = results.stream()
                .collect(Collectors.groupingBy(
                        InterviewResult::getBestTopic,
                        Collectors.counting()
                ))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");

        // ✅ Weak Topic (most frequent weakTopic)
        String weakTopic = results.stream()
                .collect(Collectors.groupingBy(
                        InterviewResult::getWeakTopic,
                        Collectors.counting()
                ))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");

        // ✅ Performance Trend (last 6 interviews)
        List<Double> performance = resultRepository
                .findByUserIdOrderByCreatedAtAsc(userId)
                .stream()
                .map(InterviewResult::getPercentage)
                .toList();

        // ✅ Recent Interviews (limit 5)
        List<Map<String, Object>> recent = results.stream()
                .limit(5)
                .map(r -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("domain", r.getDomain());
                    map.put("topic", r.getTopic());
                    map.put("percentage", r.getPercentage());
                    map.put("date", r.getCreatedAt());
                    return map;
                })
                .toList();

        response.put("totalInterviews", totalInterviews);
        response.put("averageScore", Math.round(avg));
        response.put("bestTopic", bestTopic);
        response.put("weakTopic", weakTopic);
        response.put("performance", performance);
        response.put("recent", recent);

        return response;
    }
}