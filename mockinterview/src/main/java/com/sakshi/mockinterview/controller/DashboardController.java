package com.sakshi.mockinterview.controller;

import com.sakshi.mockinterview.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/{userId}")
    public Map<String, Object> getDashboard(
            @PathVariable Long userId) {

        return dashboardService.getDashboardData(userId);
    }
}