package com.example.flashlearn.controller;

import com.example.flashlearn.service.FlashlearnService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users/me")
public class UserController {

    private final FlashlearnService flashlearnService;

    public UserController(FlashlearnService flashlearnService) {
        this.flashlearnService = flashlearnService;
    }

    @GetMapping
    public ResponseEntity<?> me(@RequestHeader(value = "Authorization", required = false) String authorization) {
        return ResponseEntity.ok(flashlearnService.me(authorization));
    }

    @PutMapping
    public ResponseEntity<?> updateMe(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody Map<String, String> body
    ) {
        return ResponseEntity.ok(flashlearnService.updateMe(authorization, body));
    }

    @GetMapping("/activities")
    public ResponseEntity<?> activities(@RequestHeader(value = "Authorization", required = false) String authorization) {
        return ResponseEntity.ok(flashlearnService.activities(authorization));
    }
}
