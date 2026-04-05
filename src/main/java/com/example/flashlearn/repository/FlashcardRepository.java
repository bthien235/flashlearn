package com.example.flashlearn.repository;

import com.example.flashlearn.model.Flashcard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FlashcardRepository extends JpaRepository<Flashcard, Long> {
    List<Flashcard> findByDeckIdOrderByIdAsc(Long deckId);
    int countByDeckId(Long deckId);
    long countByDeckOwnerId(String ownerId);
    void deleteByDeckId(Long deckId);
}
