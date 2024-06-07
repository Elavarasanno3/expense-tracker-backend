package com.elavarasanno3.expensetracker.controller;

import com.elavarasanno3.expensetracker.model.SignInRequest;
import com.elavarasanno3.expensetracker.model.User;
import com.elavarasanno3.expensetracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin("http://localhost:3000")
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        if (user.getPassword() == null || !user.getPassword().equals(user.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Passwords do not match");
        }

        if (userService.findByEmailId(user.getEmailId()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already registered");
        }

        userService.saveUser(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/signin")
    public ResponseEntity<Map<String, Object>> signInUser(@RequestBody SignInRequest signInRequest) {
        Optional<User> optionalUser = userService.findByEmailId(signInRequest.getEmailId());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getPassword().equals(signInRequest.getPassword())) {
                // Create a simple map containing id and name
                Map<String, Object> userResponse = new HashMap<>();
                userResponse.put("id", user.getId());
                userResponse.put("name", user.getName());
                return ResponseEntity.ok(userResponse);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "Invalid password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}
