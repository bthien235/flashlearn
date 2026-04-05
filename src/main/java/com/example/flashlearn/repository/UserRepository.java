package com.example.flashlearn.repository;

import com.example.flashlearn.model.AppUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, String> {
    Optional<AppUser> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);

    @Query("SELECT u FROM AppUser u WHERE u.id <> 'system' ORDER BY u.exp DESC")
    List<AppUser> findTopByExpDesc(Pageable pageable);

    @Query("SELECT COUNT(u) FROM AppUser u WHERE u.id <> 'system' AND u.exp > (SELECT u2.exp FROM AppUser u2 WHERE u2.id = :userId)")
    long countUsersWithHigherExp(@Param("userId") String userId);
}
