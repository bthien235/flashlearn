package com.example.flashlearn.repository;

import com.example.flashlearn.model.UserStat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserStatRepository extends JpaRepository<UserStat, Long> {
    Optional<UserStat> findByUserId(String userId);
}
