package com.example.demo.exception.booking;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handle404(Model model) {
        model.addAttribute("error", "The page you are looking for does not exist.");
        model.addAttribute("status", "404");
        return "error-fallback";
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public String handle405(Model model) {
        model.addAttribute("error", "This action is not supported or the request method is incorrect.");
        model.addAttribute("status", "405");
        return "error-fallback";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception e, Model model) {
        model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
        model.addAttribute("status", "Internal Error");
        return "error-fallback";
    }
}
