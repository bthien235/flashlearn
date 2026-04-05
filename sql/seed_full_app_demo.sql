-- FlashLearn full demo seed
-- Purpose: reset and seed all tables with consistent demo data
-- Password for all seeded users: 123456
-- BCrypt hash for "123456": $2a$10$nxn6H13MpK25dE/jRd8nl.0xyVYE2q2AGLPvjr6Ak1KvYuM48iwHS

START TRANSACTION;

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE activity_logs;
TRUNCATE TABLE study_results;
TRUNCATE TABLE saved_decks;
TRUNCATE TABLE refresh_tokens;
TRUNCATE TABLE flashcards;
TRUNCATE TABLE user_stats;
TRUNCATE TABLE decks;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO users (id, fullname, username, email, password_hash, exp, level, streak, avatar, joined_at) VALUES
('u001', 'Nguyen Minh Anh', 'minhanh01', 'u001@flashlearn.local', '$2a$10$nxn6H13MpK25dE/jRd8nl.0xyVYE2q2AGLPvjr6Ak1KvYuM48iwHS', 1200, 3, 4, '', '2026-01-01'),
('u002', 'Tran Bao Chau', 'baochau02', 'u002@flashlearn.local', '$2a$10$nxn6H13MpK25dE/jRd8nl.0xyVYE2q2AGLPvjr6Ak1KvYuM48iwHS', 760, 2, 3, '', '2026-01-03'),
('u003', 'Le Hoang Duc', 'hoangduc03', 'u003@flashlearn.local', '$2a$10$nxn6H13MpK25dE/jRd8nl.0xyVYE2q2AGLPvjr6Ak1KvYuM48iwHS', 1840, 4, 7, '', '2026-01-07'),
('u004', 'Pham Gia Huy', 'giahuy04', 'u004@flashlearn.local', '$2a$10$nxn6H13MpK25dE/jRd8nl.0xyVYE2q2AGLPvjr6Ak1KvYuM48iwHS', 420, 1, 1, '', '2026-01-12'),
('u005', 'Vo Khanh Linh', 'khanhlinh05', 'u005@flashlearn.local', '$2a$10$nxn6H13MpK25dE/jRd8nl.0xyVYE2q2AGLPvjr6Ak1KvYuM48iwHS', 960, 2, 5, '', '2026-01-15');

INSERT INTO decks (id, owner_id, name, description, category, difficulty, library_deck, progress, created_at) VALUES
(1, 'u001', 'Java Core Basics', 'Nen tang Java cho nguoi moi', 'IT', 'EASY', 1, 65, '2026-02-01 08:00:00'),
(2, 'u001', 'Spring Boot API', 'REST API voi Spring Boot', 'IT', 'MEDIUM', 0, 35, '2026-02-02 08:00:00'),
(3, 'u002', 'SQL Fundamental', 'Cau lenh SQL co ban', 'IT', 'EASY', 1, 50, '2026-02-03 08:00:00'),
(4, 'u003', 'TOEIC Vocabulary', 'Tu vung TOEIC thong dung', 'TOEIC', 'MEDIUM', 1, 70, '2026-02-04 08:00:00'),
(5, 'u004', 'Linux Commands', 'Lenh Linux trong van hanh', 'IT', 'EASY', 0, 20, '2026-02-05 08:00:00'),
(6, 'u005', 'Daily English', 'Mau cau giao tiep hang ngay', 'DAILY', 'EASY', 1, 85, '2026-02-06 08:00:00');

INSERT INTO flashcards (id, deck_id, front_text, back_text, note, created_at) VALUES
(1, 1, 'JVM', 'Java Virtual Machine', 'Noi bytecode duoc thuc thi', '2026-02-10 10:00:00'),
(2, 1, 'JDK', 'Java Development Kit', 'Cong cu phat trien Java', '2026-02-10 10:02:00'),
(3, 1, 'JRE', 'Java Runtime Environment', 'Moi truong chay Java', '2026-02-10 10:04:00'),
(4, 2, '@RestController', 'Controller tra ve JSON', 'Dung cho REST API', '2026-02-10 10:06:00'),
(5, 2, '@Service', 'Danh dau lop service', 'Tang xu ly nghiep vu', '2026-02-10 10:08:00'),
(6, 2, '@Repository', 'Danh dau lop truy cap du lieu', 'Tang persistence', '2026-02-10 10:10:00'),
(7, 3, 'SELECT', 'Lay du lieu tu bang', 'Cau lenh truy van co ban', '2026-02-10 10:12:00'),
(8, 3, 'WHERE', 'Loc du lieu theo dieu kien', 'Ket hop voi SELECT', '2026-02-10 10:14:00'),
(9, 3, 'JOIN', 'Ket hop du lieu nhieu bang', 'INNER/LEFT/RIGHT', '2026-02-10 10:16:00'),
(10, 4, 'Invoice', 'Hoa don', 'Business vocabulary', '2026-02-10 10:18:00'),
(11, 4, 'Contract', 'Hop dong', 'Business vocabulary', '2026-02-10 10:20:00'),
(12, 4, 'Revenue', 'Doanh thu', 'Business vocabulary', '2026-02-10 10:22:00'),
(13, 5, 'ls -la', 'Liet ke file ke ca an', 'Lenh Linux co ban', '2026-02-10 10:24:00'),
(14, 5, 'cd', 'Chuyen thu muc', 'Change directory', '2026-02-10 10:26:00'),
(15, 5, 'grep', 'Tim kiem theo mau', 'Search text', '2026-02-10 10:28:00'),
(16, 6, 'How are you?', 'Ban khoe khong?', 'Greeting', '2026-02-10 10:30:00'),
(17, 6, 'See you later', 'Hen gap lai', 'Farewell', '2026-02-10 10:32:00'),
(18, 6, 'Take care', 'Bao trong nhe', 'Farewell', '2026-02-10 10:34:00');

INSERT INTO user_stats (id, user_id, sessions, cards_mastered, minutes, streak) VALUES
(1, 'u001', 28, 140, 620, 4),
(2, 'u002', 19, 88, 430, 3),
(3, 'u003', 36, 190, 840, 7),
(4, 'u004', 11, 42, 210, 1),
(5, 'u005', 23, 110, 520, 5);

INSERT INTO saved_decks (id, user_id, deck_id, created_at) VALUES
(1, 'u001', 4, '2026-03-01 09:00:00'),
(2, 'u002', 1, '2026-03-01 09:05:00'),
(3, 'u002', 6, '2026-03-01 09:06:00'),
(4, 'u003', 3, '2026-03-01 09:10:00'),
(5, 'u004', 1, '2026-03-01 09:15:00'),
(6, 'u005', 4, '2026-03-01 09:20:00');

INSERT INTO study_results (id, user_id, deck_id, total_cards, correct, wrong, exp_gained, accuracy, current_exp, current_level, created_at) VALUES
(1, 'u001', 1, 20, 17, 3, 50, 85, 1200, 3, '2026-03-15 10:00:00'),
(2, 'u001', 2, 15, 12, 3, 35, 80, 1235, 3, '2026-03-17 10:00:00'),
(3, 'u002', 3, 18, 14, 4, 40, 78, 760, 2, '2026-03-15 10:10:00'),
(4, 'u003', 4, 25, 21, 4, 65, 84, 1840, 4, '2026-03-15 10:20:00'),
(5, 'u004', 5, 10, 7, 3, 20, 70, 420, 1, '2026-03-15 10:30:00'),
(6, 'u005', 6, 22, 18, 4, 55, 82, 960, 2, '2026-03-15 10:40:00');

INSERT INTO activity_logs (id, user_id, type, title, time_label, exp, created_at) VALUES
(1, 'u001', 'study', 'Hoan thanh deck Java Core Basics', 'Hom nay', 50, '2026-04-01 12:00:00'),
(2, 'u001', 'creation', 'Tao deck Spring Boot API', 'Hom qua', 20, '2026-04-01 12:10:00'),
(3, 'u002', 'study', 'On tap SQL Fundamental', 'Hom nay', 40, '2026-04-01 12:20:00'),
(4, 'u003', 'achievement', 'Dat moc level 4', 'Hom qua', 80, '2026-04-01 12:30:00'),
(5, 'u004', 'creation', 'Tao deck Linux Commands', '2 ngay truoc', 15, '2026-04-01 12:40:00'),
(6, 'u005', 'study', 'Luyen Daily English', 'Hom nay', 55, '2026-04-01 12:50:00');

INSERT INTO refresh_tokens (id, user_id, token, revoked, expires_at, created_at) VALUES
(1, 'u001', 'seed-token-u001-active', 0, '2026-12-31 00:00:00', '2026-04-01 08:00:00'),
(2, 'u002', 'seed-token-u002-revoked', 1, '2026-12-31 00:00:00', '2026-04-01 08:05:00'),
(3, 'u003', 'seed-token-u003-active', 0, '2026-12-31 00:00:00', '2026-04-01 08:10:00'),
(4, 'u004', 'seed-token-u004-active', 0, '2026-12-31 00:00:00', '2026-04-01 08:15:00'),
(5, 'u005', 'seed-token-u005-revoked', 1, '2026-12-31 00:00:00', '2026-04-01 08:20:00');

COMMIT;

-- Quick checks:
-- SELECT COUNT(*) FROM users;
-- SELECT COUNT(*) FROM decks;
-- SELECT COUNT(*) FROM flashcards;
