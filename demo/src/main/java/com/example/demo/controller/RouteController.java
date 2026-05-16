package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RouteController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/services")
    public String services() {
        return "services";
    }

    @GetMapping("/gallery")
    public String gallery() {
        return "gallery";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/blog")
    public String blog() {
        return "blog";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

    @GetMapping("/user-list")
    public String userList() {
        return "user-list";
    }

    @GetMapping("/user-form")
    public String userForm() {
        return "user-form";
    }

    @GetMapping("/user-view")
    public String userView() {
        return "user-view";
    }

    @GetMapping("/user-register")
    public String userRegister() {
        return "user-register";
    }
}
