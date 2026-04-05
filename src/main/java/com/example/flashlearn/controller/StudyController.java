package com.example.flashlearn.controller;

import com.example.flashlearn.service.FlashlearnService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/study")
public class StudyController {

    private final FlashlearnService flashlearnService;

    public StudyController(FlashlearnService flashlearnService) {
        this.flashlearnService = flashlearnService;
    }

    @PostMapping("/result")
    public ResponseEntity<?> saveResult(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody Map<String, Object> body
    ) {
        return ResponseEntity.ok(flashlearnService.saveStudyResult(authorization, body));
    }

    @GetMapping("/result/latest")
    public ResponseEntity<?> latestResult(@RequestHeader(value = "Authorization", required = false) String authorization) {
        return ResponseEntity.ok(flashlearnService.latestStudyResult(authorization));
    }
}
