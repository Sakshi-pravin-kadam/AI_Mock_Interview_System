package com.sakshi.mockinterview.controller;

import com.sakshi.mockinterview.entity.User;
import com.sakshi.mockinterview.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/register")
    public String registerUser(@RequestBody User user) {

        if(userRepository.findByEmail(user.getEmail()) != null){
            return "Email already exists";
        }

        // Encrypt password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
        return "User registered successfully";
    }

    @PostMapping("/login")
    public String loginUser(@RequestBody User user){

        User existingUser = userRepository.findByEmail(user.getEmail());

        if(existingUser == null){
            return "User not found";
        }

        // Compare encrypted password
        if(passwordEncoder.matches(user.getPassword(), existingUser.getPassword())){
            return "Login successful";
        }else{
            return "Invalid password";
        }
    }

}