package com.sakshi.mockinterview.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(){
        return "Home";
    }
    @GetMapping("/Home.html")
    public String Home(){
        return "Home";
    }

    @GetMapping("/index.html")
    public String index(){
        return "Home";
    }

    @GetMapping("/login.html")
    public String loginPage(){
        return "login";
    }

    @GetMapping("/signup.html")
    public String signup(){
        return "signup";
    }

    @GetMapping("/start_interview.html")
    public String startInterview() {
        return "start_interview";
    }

    @GetMapping("/interview.html")
    public String interview() {
        return "interview";
    }

    @GetMapping("/dashboard.html")
    public String dashboard(){
        return "dashboard";
    }
}