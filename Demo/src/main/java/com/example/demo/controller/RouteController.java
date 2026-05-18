package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * RouteController - Handles static page routing
 * Routes for Salon Services module
 */
@Controller
public class RouteController {

    @GetMapping("/")
    public String home() {
        return "index";
    }
}
