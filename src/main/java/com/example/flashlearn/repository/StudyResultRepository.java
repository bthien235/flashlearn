package com.example.flashlearn.repository;

import com.example.flashlearn.model.StudyResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudyResultRepository extends JpaRepository<StudyResult, Long> {
    Optional<StudyResult> findFirstByUserIdOrderByCreatedAtDesc(String userId);
}
