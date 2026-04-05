package com.example.flashlearn.repository;

import com.example.flashlearn.model.Deck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeckRepository extends JpaRepository<Deck, Long> {
    List<Deck> findByLibraryDeckTrueOrderByCategoryAscNameAsc();
    List<Deck> findByOwnerIdOrderByIdAsc(String ownerId);
    long countByOwnerId(String ownerId);
}
