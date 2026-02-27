package com.example.jwt.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home(HttpServletRequest request) {

        // This prints the current session id
        // since this is stateful this session id will be present till the user logout
        // once the user logout it spring will create a new session id
        return "Your session id: " + request.getSession().getId();
    }

    /** By default in spring all the requests apart from GET
     * Need CSRF token to send the request.
     * we will use this CSRF token and send as a header called X-CSRF-TOKEN for the other request
     * we created a customer order using this CSRF token
     */
    @GetMapping("csrf")
    public CsrfToken getCsrfToken(HttpServletRequest request) {
        return (CsrfToken) request.getAttribute("_csrf");
    }
}
