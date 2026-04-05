package com.example.flashlearn.controller;

import com.example.flashlearn.service.FlashlearnService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/decks")
public class DeckController {

    private final FlashlearnService flashlearnService;

    public DeckController(FlashlearnService flashlearnService) {
        this.flashlearnService = flashlearnService;
    }

    @GetMapping
    public ResponseEntity<?> getDecks(@RequestHeader(value = "Authorization", required = false) String authorization) {
        return ResponseEntity.ok(flashlearnService.listDecks(authorization));
    }

    @PostMapping
    public ResponseEntity<?> createDeck(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody Map<String, String> body
    ) {
        return ResponseEntity.ok(flashlearnService.createDeck(
                authorization,
                body.get("name"),
                body.get("description"),
                body.get("category"),
                body.get("difficulty"),
                body.get("visibility")
        ));
    }

    @PutMapping("/{deckId}")
    public ResponseEntity<?> updateDeck(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable long deckId,
            @RequestBody Map<String, String> body
    ) {
        return ResponseEntity.ok(flashlearnService.updateDeck(
                authorization,
                deckId,
                body.get("name"),
                body.get("description"),
                body.get("category"),
                body.get("difficulty"),
                body.get("visibility")
        ));
    }

    @DeleteMapping("/{deckId}")
    public ResponseEntity<?> deleteDeck(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable long deckId
    ) {
        return ResponseEntity.ok(flashlearnService.deleteDeck(authorization, deckId));
    }
}
