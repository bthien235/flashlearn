package com.example.flashlearn.controller;

import com.example.flashlearn.service.FlashlearnService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/library/decks")
public class LibraryController {

    private final FlashlearnService flashlearnService;

    public LibraryController(FlashlearnService flashlearnService) {
        this.flashlearnService = flashlearnService;
    }

    @GetMapping
    public ResponseEntity<?> getLibraryDecks(@RequestHeader(value = "Authorization", required = false) String authorization) {
        return ResponseEntity.ok(flashlearnService.listLibraryDecks(authorization));
    }

    @PostMapping("/{deckId}/save")
    public ResponseEntity<?> saveDeck(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable long deckId
    ) {
        return ResponseEntity.ok(flashlearnService.saveLibraryDeck(authorization, deckId));
    }

    @DeleteMapping("/{deckId}/save")
    public ResponseEntity<?> unsaveDeck(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable long deckId
    ) {
        return ResponseEntity.ok(flashlearnService.unsaveLibraryDeck(authorization, deckId));
    }
}
