package com.ppm.backend.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @GetMapping
    public String profile() {
        return "Profile data accessible by USER or ADMIN";
    }
}

