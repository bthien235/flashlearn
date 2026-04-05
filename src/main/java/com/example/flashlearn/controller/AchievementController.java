package com.example.flashlearn.controller;

import com.example.flashlearn.service.FlashlearnService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/achievements")
public class AchievementController {

    private final FlashlearnService flashlearnService;

    public AchievementController(FlashlearnService flashlearnService) {
        this.flashlearnService = flashlearnService;
    }

    @GetMapping
    public ResponseEntity<?> achievements(@RequestHeader(value = "Authorization", required = false) String authorization) {
        return ResponseEntity.ok(flashlearnService.achievements(authorization));
    }
}
