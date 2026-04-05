-- ============================================
-- FlashLearn - Initial Schema
-- V1__init_schema.sql
-- ============================================

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(64) NOT NULL,
    fullname VARCHAR(120) NOT NULL,
    username VARCHAR(80) NOT NULL,
    email VARCHAR(160) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    exp INT NOT NULL DEFAULT 0,
    level INT NOT NULL DEFAULT 1,
    streak INT NOT NULL DEFAULT 0,
    avatar VARCHAR(255) NOT NULL DEFAULT '',
    joined_at DATE NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Decks table
CREATE TABLE IF NOT EXISTS decks (
    id BIGINT NOT NULL AUTO_INCREMENT,
    owner_id VARCHAR(64) NOT NULL,
    name VARCHAR(160) NOT NULL,
    description VARCHAR(512) NOT NULL DEFAULT '',
    category VARCHAR(80) NOT NULL DEFAULT 'GENERAL',
    difficulty VARCHAR(32) NOT NULL DEFAULT 'MEDIUM',
    library_deck TINYINT(1) NOT NULL DEFAULT 0,
    progress INT NOT NULL DEFAULT 0,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_decks_owner (owner_id),
    CONSTRAINT fk_decks_owner FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Flashcards table
CREATE TABLE IF NOT EXISTS flashcards (
    id BIGINT NOT NULL AUTO_INCREMENT,
    deck_id BIGINT NOT NULL,
    front_text VARCHAR(255) NOT NULL,
    back_text VARCHAR(255) NOT NULL,
    note VARCHAR(512) NOT NULL DEFAULT '',
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_flashcards_deck (deck_id),
    CONSTRAINT fk_flashcards_deck FOREIGN KEY (deck_id) REFERENCES decks (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Refresh tokens table
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id VARCHAR(64) NOT NULL,
    token VARCHAR(512) NOT NULL,
    revoked TINYINT(1) NOT NULL DEFAULT 0,
    expires_at DATETIME(6) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY idx_refresh_token (token),
    KEY idx_refresh_tokens_user (user_id),
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Saved decks table
CREATE TABLE IF NOT EXISTS saved_decks (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id VARCHAR(64) NOT NULL,
    deck_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_saved_user_deck (user_id, deck_id),
    KEY idx_saved_decks_user (user_id),
    KEY idx_saved_decks_deck (deck_id),
    CONSTRAINT fk_saved_decks_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_saved_decks_deck FOREIGN KEY (deck_id) REFERENCES decks (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Study results table
CREATE TABLE IF NOT EXISTS study_results (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id VARCHAR(64) NOT NULL,
    deck_id BIGINT DEFAULT NULL,
    total_cards INT NOT NULL DEFAULT 0,
    correct INT NOT NULL DEFAULT 0,
    wrong INT NOT NULL DEFAULT 0,
    exp_gained INT NOT NULL DEFAULT 0,
    accuracy INT NOT NULL DEFAULT 0,
    current_exp INT NOT NULL DEFAULT 0,
    current_level INT NOT NULL DEFAULT 1,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_study_results_user (user_id),
    CONSTRAINT fk_study_results_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- User stats table
CREATE TABLE IF NOT EXISTS user_stats (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id VARCHAR(64) NOT NULL,
    sessions INT NOT NULL DEFAULT 0,
    cards_mastered INT NOT NULL DEFAULT 0,
    minutes INT NOT NULL DEFAULT 0,
    streak INT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_stats_user (user_id),
    CONSTRAINT fk_user_stats_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Activity logs table
CREATE TABLE IF NOT EXISTS activity_logs (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id VARCHAR(64) NOT NULL,
    type VARCHAR(40) NOT NULL,
    title VARCHAR(255) NOT NULL,
    time_label VARCHAR(80) NOT NULL,
    exp INT NOT NULL DEFAULT 0,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_activity_logs_user (user_id),
    CONSTRAINT fk_activity_logs_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
