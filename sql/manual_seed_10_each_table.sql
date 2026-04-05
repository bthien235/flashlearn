-- Manual seed file (run manually, not via Flyway migration)
-- Database: flashlearn
-- Safe to run multiple times: uses ON DUPLICATE KEY UPDATE

START TRANSACTION;

-- 1) users (10 rows) — Password for ALL seed users: 123456
INSERT INTO users (id, fullname, username, email, password_hash, exp, level, streak, avatar, joined_at) VALUES
('u001', 'Nguyen Minh Anh', 'minhanh01', 'u001@flashlearn.local', '$2a$10$nxn6H13MpK25dE/jRd8nl.0xyVYE2q2AGLPvjr6Ak1KvYuM48iwHS', 120, 1, 2, '', '2026-01-01'),
('u002', 'Tran Bao Chau', 'baochau02', 'u002@flashlearn.local', '$2a$10$nxn6H13MpK25dE/jRd8nl.0xyVYE2q2AGLPvjr6Ak1KvYuM48iwHS', 240, 1, 1, '', '2026-01-03'),
('u003', 'Le Hoang Duc', 'hoangduc03', 'u003@flashlearn.local', '$2a$10$nxn6H13MpK25dE/jRd8nl.0xyVYE2q2AGLPvjr6Ak1KvYuM48iwHS', 520, 2, 5, '', '2026-01-05'),
('u004', 'Pham Gia Huy', 'giahuy04', 'u004@flashlearn.local', '$2a$10$nxn6H13MpK25dE/jRd8nl.0xyVYE2q2AGLPvjr6Ak1KvYuM48iwHS', 890, 2, 3, '', '2026-01-07'),
('u005', 'Vo Khanh Linh', 'khanhlinh05', 'u005@flashlearn.local', '$2a$10$nxn6H13MpK25dE/jRd8nl.0xyVYE2q2AGLPvjr6Ak1KvYuM48iwHS', 1340, 3, 6, '', '2026-01-10'),
('u006', 'Bui Ngoc Mai', 'ngocmai06', 'u006@flashlearn.local', '$2a$10$nxn6H13MpK25dE/jRd8nl.0xyVYE2q2AGLPvjr6Ak1KvYuM48iwHS', 420, 2, 2, '', '2026-01-12'),
('u007', 'Dang Quoc Nam', 'quocnam07', 'u007@flashlearn.local', '$2a$10$nxn6H13MpK25dE/jRd8nl.0xyVYE2q2AGLPvjr6Ak1KvYuM48iwHS', 1680, 4, 8, '', '2026-01-15'),
('u008', 'Do Thu Trang', 'thutrang08', 'u008@flashlearn.local', '$2a$10$nxn6H13MpK25dE/jRd8nl.0xyVYE2q2AGLPvjr6Ak1KvYuM48iwHS', 730, 2, 4, '', '2026-01-18'),
('u009', 'Huynh Tuan Kiet', 'tuankiet09', 'u009@flashlearn.local', '$2a$10$nxn6H13MpK25dE/jRd8nl.0xyVYE2q2AGLPvjr6Ak1KvYuM48iwHS', 310, 1, 1, '', '2026-01-20'),
('u010', 'Phan Yen Nhi', 'yennhi10', 'u010@flashlearn.local', '$2a$10$nxn6H13MpK25dE/jRd8nl.0xyVYE2q2AGLPvjr6Ak1KvYuM48iwHS', 980, 3, 7, '', '2026-01-22')
ON DUPLICATE KEY UPDATE
  fullname = VALUES(fullname),
  username = VALUES(username),
  email = VALUES(email),
  password_hash = VALUES(password_hash),
  exp = VALUES(exp),
  level = VALUES(level),
  streak = VALUES(streak),
  avatar = VALUES(avatar),
  joined_at = VALUES(joined_at);

-- 2) decks (10 rows)
INSERT INTO decks (id, owner_id, name, description, category, difficulty, library_deck, progress, created_at) VALUES
(1, 'u001', 'Java Core Basics', 'Nen tang Java cho nguoi moi', 'IT', 'EASY', 0, 45, '2026-02-01 08:00:00.000000'),
(2, 'u002', 'SQL Fundamental', 'Lenh SQL co ban den nang cao', 'IT', 'MEDIUM', 0, 30, '2026-02-02 08:10:00.000000'),
(3, 'u003', 'Networking 101', 'Khai niem mang may tinh', 'IT', 'MEDIUM', 1, 20, '2026-02-03 08:20:00.000000'),
(4, 'u004', 'Linux Commands', 'Tong hop lenh Linux thong dung', 'IT', 'EASY', 1, 55, '2026-02-04 08:30:00.000000'),
(5, 'u005', 'TOEIC Vocabulary A', 'Tu vung TOEIC chu de cong viec', 'TOEIC', 'MEDIUM', 1, 60, '2026-02-05 08:40:00.000000'),
(6, 'u006', 'TOEIC Listening', 'Mau cau hoi nghe TOEIC', 'TOEIC', 'HARD', 0, 15, '2026-02-06 08:50:00.000000'),
(7, 'u007', 'Daily English', 'Mau cau giao tiep hang ngay', 'DAILY', 'EASY', 1, 80, '2026-02-07 09:00:00.000000'),
(8, 'u008', 'Travel English', 'Tieng Anh du lich thuc te', 'DAILY', 'EASY', 1, 70, '2026-02-08 09:10:00.000000'),
(9, 'u009', 'Cyber Security Intro', 'Kien thuc nhap mon an toan thong tin', 'IT', 'HARD', 0, 25, '2026-02-09 09:20:00.000000'),
(10, 'u010', 'Data Structures', 'Tong hop cau truc du lieu pho bien', 'IT', 'MEDIUM', 0, 50, '2026-02-10 09:30:00.000000')
ON DUPLICATE KEY UPDATE
  owner_id = VALUES(owner_id),
  name = VALUES(name),
  description = VALUES(description),
  category = VALUES(category),
  difficulty = VALUES(difficulty),
  library_deck = VALUES(library_deck),
  progress = VALUES(progress),
  created_at = VALUES(created_at);

-- 3) flashcards (10 rows)
INSERT INTO flashcards (id, deck_id, front_text, back_text, note, created_at) VALUES
(1, 1, 'JVM', 'Java Virtual Machine', 'Noi bytecode duoc thuc thi', '2026-02-11 10:00:00.000000'),
(2, 2, 'INNER JOIN', 'Ket hop ban ghi khop o hai bang', 'Truy van pho bien', '2026-02-11 10:05:00.000000'),
(3, 3, 'IP Address', 'Dia chi danh danh thiet bi tren mang', 'IPv4 va IPv6', '2026-02-11 10:10:00.000000'),
(4, 4, 'ls -la', 'Liet ke file ke ca an', 'Lenh Linux co ban', '2026-02-11 10:15:00.000000'),
(5, 5, 'Invoice', 'Hoa don', 'Business vocabulary', '2026-02-11 10:20:00.000000'),
(6, 6, 'Announcement', 'Thong bao', 'Listening part 3', '2026-02-11 10:25:00.000000'),
(7, 7, 'How is it going?', 'Moi thu the nao?', 'Giao tiep than mat', '2026-02-11 10:30:00.000000'),
(8, 8, 'Boarding pass', 'The len may bay', 'Du lich hang khong', '2026-02-11 10:35:00.000000'),
(9, 9, 'Phishing', 'Hinh thuc lua dao qua email/link', 'Canh giac bao mat', '2026-02-11 10:40:00.000000'),
(10, 10, 'Queue', 'Hang doi FIFO', 'Data structure', '2026-02-11 10:45:00.000000')
ON DUPLICATE KEY UPDATE
  deck_id = VALUES(deck_id),
  front_text = VALUES(front_text),
  back_text = VALUES(back_text),
  note = VALUES(note),
  created_at = VALUES(created_at);

-- 4) refresh_tokens (10 rows)
INSERT INTO refresh_tokens (id, user_id, token, revoked, expires_at, created_at) VALUES
(1, 'u001', 'seed-refresh-token-u001', 0, '2026-05-01 00:00:00.000000', '2026-04-01 08:00:00.000000'),
(2, 'u002', 'seed-refresh-token-u002', 0, '2026-05-01 00:00:00.000000', '2026-04-01 08:05:00.000000'),
(3, 'u003', 'seed-refresh-token-u003', 1, '2026-05-01 00:00:00.000000', '2026-04-01 08:10:00.000000'),
(4, 'u004', 'seed-refresh-token-u004', 0, '2026-05-01 00:00:00.000000', '2026-04-01 08:15:00.000000'),
(5, 'u005', 'seed-refresh-token-u005', 0, '2026-05-01 00:00:00.000000', '2026-04-01 08:20:00.000000'),
(6, 'u006', 'seed-refresh-token-u006', 1, '2026-05-01 00:00:00.000000', '2026-04-01 08:25:00.000000'),
(7, 'u007', 'seed-refresh-token-u007', 0, '2026-05-01 00:00:00.000000', '2026-04-01 08:30:00.000000'),
(8, 'u008', 'seed-refresh-token-u008', 0, '2026-05-01 00:00:00.000000', '2026-04-01 08:35:00.000000'),
(9, 'u009', 'seed-refresh-token-u009', 0, '2026-05-01 00:00:00.000000', '2026-04-01 08:40:00.000000'),
(10, 'u010', 'seed-refresh-token-u010', 1, '2026-05-01 00:00:00.000000', '2026-04-01 08:45:00.000000')
ON DUPLICATE KEY UPDATE
  user_id = VALUES(user_id),
  token = VALUES(token),
  revoked = VALUES(revoked),
  expires_at = VALUES(expires_at),
  created_at = VALUES(created_at);

-- 5) saved_decks (10 rows)
INSERT INTO saved_decks (id, user_id, deck_id, created_at) VALUES
(1, 'u001', 2, '2026-03-01 09:00:00.000000'),
(2, 'u002', 3, '2026-03-01 09:05:00.000000'),
(3, 'u003', 4, '2026-03-01 09:10:00.000000'),
(4, 'u004', 5, '2026-03-01 09:15:00.000000'),
(5, 'u005', 6, '2026-03-01 09:20:00.000000'),
(6, 'u006', 7, '2026-03-01 09:25:00.000000'),
(7, 'u007', 8, '2026-03-01 09:30:00.000000'),
(8, 'u008', 9, '2026-03-01 09:35:00.000000'),
(9, 'u009', 10, '2026-03-01 09:40:00.000000'),
(10, 'u010', 1, '2026-03-01 09:45:00.000000')
ON DUPLICATE KEY UPDATE
  user_id = VALUES(user_id),
  deck_id = VALUES(deck_id),
  created_at = VALUES(created_at);

-- 6) study_results (10 rows)
INSERT INTO study_results (id, user_id, deck_id, total_cards, correct, wrong, exp_gained, accuracy, current_exp, current_level, created_at) VALUES
(1, 'u001', 1, 20, 16, 4, 40, 80, 160, 1, '2026-03-15 10:00:00.000000'),
(2, 'u002', 2, 25, 19, 6, 45, 76, 285, 1, '2026-03-15 10:10:00.000000'),
(3, 'u003', 3, 18, 14, 4, 35, 78, 555, 2, '2026-03-15 10:20:00.000000'),
(4, 'u004', 4, 22, 18, 4, 44, 82, 934, 2, '2026-03-15 10:30:00.000000'),
(5, 'u005', 5, 30, 24, 6, 60, 80, 1400, 3, '2026-03-15 10:40:00.000000'),
(6, 'u006', 6, 16, 11, 5, 28, 69, 448, 2, '2026-03-15 10:50:00.000000'),
(7, 'u007', 7, 28, 23, 5, 57, 82, 1737, 4, '2026-03-15 11:00:00.000000'),
(8, 'u008', 8, 24, 19, 5, 46, 79, 776, 2, '2026-03-15 11:10:00.000000'),
(9, 'u009', 9, 14, 9, 5, 24, 64, 334, 1, '2026-03-15 11:20:00.000000'),
(10, 'u010', 10, 26, 20, 6, 50, 77, 1030, 3, '2026-03-15 11:30:00.000000')
ON DUPLICATE KEY UPDATE
  user_id = VALUES(user_id),
  deck_id = VALUES(deck_id),
  total_cards = VALUES(total_cards),
  correct = VALUES(correct),
  wrong = VALUES(wrong),
  exp_gained = VALUES(exp_gained),
  accuracy = VALUES(accuracy),
  current_exp = VALUES(current_exp),
  current_level = VALUES(current_level),
  created_at = VALUES(created_at);

-- 7) user_stats (10 rows)
INSERT INTO user_stats (id, user_id, sessions, cards_mastered, minutes, streak) VALUES
(1, 'u001', 12, 45, 220, 2),
(2, 'u002', 14, 51, 260, 1),
(3, 'u003', 21, 78, 410, 5),
(4, 'u004', 19, 70, 360, 3),
(5, 'u005', 27, 96, 520, 6),
(6, 'u006', 13, 49, 240, 2),
(7, 'u007', 33, 120, 650, 8),
(8, 'u008', 17, 66, 330, 4),
(9, 'u009', 11, 40, 200, 1),
(10, 'u010', 23, 84, 460, 7)
ON DUPLICATE KEY UPDATE
  user_id = VALUES(user_id),
  sessions = VALUES(sessions),
  cards_mastered = VALUES(cards_mastered),
  minutes = VALUES(minutes),
  streak = VALUES(streak);

-- 8) activity_logs (10 rows)
INSERT INTO activity_logs (id, user_id, type, title, time_label, exp, created_at) VALUES
(1, 'u001', 'study', 'Hoan thanh deck Java Core Basics', 'Hom nay', 40, '2026-04-01 12:00:00.000000'),
(2, 'u002', 'creation', 'Tao deck SQL Fundamental', 'Hom nay', 20, '2026-04-01 12:05:00.000000'),
(3, 'u003', 'achievement', 'Dat moc 500 EXP', 'Hom qua', 50, '2026-04-01 12:10:00.000000'),
(4, 'u004', 'study', 'On tap Linux Commands', 'Hom qua', 35, '2026-04-01 12:15:00.000000'),
(5, 'u005', 'study', 'Hoan thanh deck TOEIC Vocabulary A', '2 ngay truoc', 60, '2026-04-01 12:20:00.000000'),
(6, 'u006', 'creation', 'Them card vao TOEIC Listening', '2 ngay truoc', 15, '2026-04-01 12:25:00.000000'),
(7, 'u007', 'achievement', 'Dat streak 7 ngay', '3 ngay truoc', 70, '2026-04-01 12:30:00.000000'),
(8, 'u008', 'study', 'Luyen deck Travel English', '3 ngay truoc', 42, '2026-04-01 12:35:00.000000'),
(9, 'u009', 'study', 'On tap Cyber Security Intro', '4 ngay truoc', 24, '2026-04-01 12:40:00.000000'),
(10, 'u010', 'creation', 'Tao deck Data Structures', '4 ngay truoc', 25, '2026-04-01 12:45:00.000000')
ON DUPLICATE KEY UPDATE
  user_id = VALUES(user_id),
  type = VALUES(type),
  title = VALUES(title),
  time_label = VALUES(time_label),
  exp = VALUES(exp),
  created_at = VALUES(created_at);

COMMIT;
