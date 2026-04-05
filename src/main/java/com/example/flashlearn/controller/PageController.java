package com.example.flashlearn.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping({"/", "/index"})
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/library")
    public String library() {
        return "library";
    }

    @GetMapping("/mydeck")
    public String mydeck() {
        return "mydeck";
    }

    @GetMapping("/adddeck")
    public String adddeck() {
        return "adddeck";
    }

    @GetMapping("/addflashcard")
    public String addflashcard() {
        return "addflashcard";
    }

    @GetMapping("/study")
    public String study() {
        return "study";
    }

    @GetMapping("/result")
    public String result() {
        return "result";
    }

    @GetMapping("/leaderboard")
    public String leaderboard() {
        return "leaderboard";
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }

    @GetMapping("/achievement")
    public String achievement() {
        return "achievement";
    }
}
