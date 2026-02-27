package com.example.jwt.controller;

import com.example.jwt.model.LoginResponse;
import com.example.jwt.model.User;
import com.example.jwt.service.JwtService;
import com.example.jwt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class UserController {

    private final UserService userService;


    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    @PostMapping("login")
    public LoginResponse loginUser(@RequestBody User user) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword()));
        if (authentication.isAuthenticated()) {
            return jwtService.generateTokens(user.getUserName());
        } else {
            return new LoginResponse(null,null,0);
        }
    }

    @PostMapping("register")
    public User registerUser(@RequestBody User user) {
        return userService.addUser(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}
