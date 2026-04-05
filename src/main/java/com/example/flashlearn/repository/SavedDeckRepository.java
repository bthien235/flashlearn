package com.example.flashlearn.repository;

import com.example.flashlearn.model.SavedDeck;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavedDeckRepository extends JpaRepository<SavedDeck, Long> {
    @EntityGraph(attributePaths = {"deck", "deck.owner"})
    List<SavedDeck> findByUserIdOrderByIdAsc(String userId);
    boolean existsByUserIdAndDeckId(String userId, Long deckId);
    void deleteByUserIdAndDeckId(String userId, Long deckId);
    void deleteByDeckId(Long deckId);
}
