package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RouteController {

    @GetMapping("/")
    public String home()     { return "index"; }

    @GetMapping("/services")
    public String services() { return "services"; }

    @GetMapping("/about")
    public String about()    { return "about"; }

    @GetMapping("/gallery")
    public String gallery()  { return "gallery"; }

    @GetMapping("/blog")
    public String blog()     { return "blog"; }

    @GetMapping("/contact")
    public String contact()  { return "contact"; }

    @GetMapping("/stylist")
    public String stylist()  { return "stylist"; }
}
