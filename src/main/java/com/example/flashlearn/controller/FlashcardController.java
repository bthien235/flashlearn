package com.example.flashlearn.controller;

import com.example.flashlearn.service.FlashlearnService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/decks/{deckId}/flashcards")
public class FlashcardController {

    private final FlashlearnService flashlearnService;

    public FlashcardController(FlashlearnService flashlearnService) {
        this.flashlearnService = flashlearnService;
    }

    @PostMapping
    public ResponseEntity<?> addFlashcard(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable long deckId,
            @RequestBody Map<String, String> body
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(flashlearnService.addFlashcard(
                authorization,
                deckId,
                body.get("front"),
                body.get("back"),
                body.get("note")
        ));
    }

    @GetMapping
    public ResponseEntity<?> getFlashcards(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable long deckId
    ) {
        return ResponseEntity.ok(flashlearnService.listFlashcards(authorization, deckId));
    }

    @PutMapping("/{cardId}")
    public ResponseEntity<?> updateFlashcard(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable long deckId,
            @PathVariable long cardId,
            @RequestBody Map<String, String> body
    ) {
        return ResponseEntity.ok(flashlearnService.updateFlashcard(
                authorization,
                deckId,
                cardId,
                body.get("front"),
                body.get("back"),
                body.get("note")
        ));
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<?> deleteFlashcard(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable long deckId,
            @PathVariable long cardId
    ) {
        return ResponseEntity.ok(flashlearnService.deleteFlashcard(authorization, deckId, cardId));
    }
}
