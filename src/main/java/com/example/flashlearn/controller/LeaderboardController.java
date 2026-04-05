package com.example.flashlearn.controller;

import com.example.flashlearn.service.FlashlearnService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    private final FlashlearnService flashlearnService;

    public LeaderboardController(FlashlearnService flashlearnService) {
        this.flashlearnService = flashlearnService;
    }

    @GetMapping
    public ResponseEntity<?> getLeaderboard() {
        return ResponseEntity.ok(flashlearnService.leaderboard());
    }
}
