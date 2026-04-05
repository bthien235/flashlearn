package com.example.flashlearn.controller;

import com.example.flashlearn.service.FlashlearnService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final FlashlearnService flashlearnService;

    public AuthController(FlashlearnService flashlearnService) {
        this.flashlearnService = flashlearnService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(flashlearnService.login(body.get("email"), body.get("password")));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(flashlearnService.register(
                body.get("fullname"),
                body.get("username"),
                body.get("email"),
                body.get("password")
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(flashlearnService.refreshAccessToken(body.get("refreshToken")));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody(required = false) Map<String, String> body) {
        String refreshToken = body == null ? null : body.get("refreshToken");
        return ResponseEntity.ok(flashlearnService.logout(refreshToken));
    }
}
