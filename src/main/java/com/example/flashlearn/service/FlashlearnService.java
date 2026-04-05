package com.example.flashlearn.service;

import com.example.flashlearn.exception.BadRequestException;
import com.example.flashlearn.exception.ConflictException;
import com.example.flashlearn.exception.NotFoundException;
import com.example.flashlearn.exception.UnauthorizedException;
import com.example.flashlearn.model.*;
import com.example.flashlearn.repository.*;
import com.example.flashlearn.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class FlashlearnService {

    private final UserRepository userRepository;
    private final DeckRepository deckRepository;
    private final FlashcardRepository flashcardRepository;
    private final SavedDeckRepository savedDeckRepository;
    private final UserStatRepository userStatRepository;
    private final ActivityLogRepository activityLogRepository;
    private final StudyResultRepository studyResultRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final long refreshExpirationDays;

    public FlashlearnService(
            UserRepository userRepository,
            DeckRepository deckRepository,
            FlashcardRepository flashcardRepository,
            SavedDeckRepository savedDeckRepository,
            UserStatRepository userStatRepository,
            ActivityLogRepository activityLogRepository,
            StudyResultRepository studyResultRepository,
            RefreshTokenRepository refreshTokenRepository,
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            @Value("${app.jwt.refresh-expiration-days:30}") long refreshExpirationDays
    ) {
        this.userRepository = userRepository;
        this.deckRepository = deckRepository;
        this.flashcardRepository = flashcardRepository;
        this.savedDeckRepository = savedDeckRepository;
        this.userStatRepository = userStatRepository;
        this.activityLogRepository = activityLogRepository;
        this.studyResultRepository = studyResultRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.refreshExpirationDays = refreshExpirationDays;
    }

    @Transactional
    public Map<String, Object> register(String fullname, String username, String email, String password) {
        String normalizedEmail = normalizeEmail(email);
        if (normalizedEmail.isBlank() || isBlank(password) || isBlank(fullname) || isBlank(username)) {
            throw new BadRequestException("Vui long dien day du thong tin!");
        }
        if (password.length() < 6) {
            throw new BadRequestException("Mat khau phai toi thieu 6 ky tu!");
        }
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new ConflictException("Email da ton tai!");
        }

        AppUser user = new AppUser();
        user.setId(generateUserId());
        user.setFullname(fullname.trim());
        user.setUsername(username.trim());
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setExp(0);
        user.setLevel(1);
        user.setAvatar("");
        user.setJoinedAt(LocalDate.now());
        user.setStreak(0);
        userRepository.save(user);

        UserStat stat = new UserStat();
        stat.setUser(user);
        stat.setSessions(0);
        stat.setCardsMastered(0);
        stat.setMinutes(0);
        stat.setStreak(0);
        userStatRepository.save(stat);

        pushActivity(user, "social", "Tao tai khoan FlashLearn", "Vua xong", 10);
        return Map.of("message", "Dang ky thanh cong!", "userId", user.getId());
    }

    @Transactional
    public Map<String, Object> login(String email, String password) {
        String normalizedEmail = normalizeEmail(email);
        if (normalizedEmail.isBlank() || isBlank(password)) {
            throw new BadRequestException("Vui long nhap email va mat khau!");
        }

        AppUser user = userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new UnauthorizedException("Sai email hoac mat khau!"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new UnauthorizedException("Sai email hoac mat khau!");
        }

        String accessToken = jwtService.generateAccessToken(user.getId());
        String refreshToken = jwtService.generateRefreshToken(user.getId());
        persistRefreshToken(user, refreshToken);
        return Map.of("token", accessToken, "refreshToken", refreshToken, "user", userPayload(user));
    }

    @Transactional
    public Map<String, Object> refreshAccessToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new UnauthorizedException("Refresh token khong hop le!");
        }

        String userId = jwtService.extractRefreshUserId(refreshToken);
        if (userId == null) {
            throw new UnauthorizedException("Refresh token het han hoac khong hop le!");
        }

        RefreshToken stored = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new UnauthorizedException("Refresh token het han hoac khong hop le!"));

        if (stored.isRevoked() || stored.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("Refresh token het han hoac khong hop le!");
        }

        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("Nguoi dung khong ton tai!"));

        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        String newAccessToken = jwtService.generateAccessToken(user.getId());
        String newRefreshToken = jwtService.generateRefreshToken(user.getId());
        persistRefreshToken(user, newRefreshToken);

        return Map.of("token", newAccessToken, "refreshToken", newRefreshToken, "user", userPayload(user));
    }

    @Transactional
    public Map<String, Object> logout(String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshTokenRepository.findByToken(refreshToken).ifPresent(token -> {
                token.setRevoked(true);
                refreshTokenRepository.save(token);
            });
        }
        return Map.of("message", "Dang xuat thanh cong!");
    }

    @Transactional
    public Map<String, Object> me(String token) {
        AppUser user = requiredUser(token);

        long deckCount = deckRepository.countByOwnerId(user.getId());
        long cardCount = flashcardRepository.countByDeckOwnerId(user.getId());
        UserStat stats = statOf(user);

        Map<String, Object> payload = new HashMap<>(userPayload(user));
        payload.put("deckCount", deckCount);
        payload.put("cardCount", cardCount);
        payload.put("studySessions", stats.getSessions());
        payload.put("studyMinutes", stats.getMinutes());
        payload.put("cardsMastered", stats.getCardsMastered());
        payload.put("streak", stats.getStreak());
        payload.put("joinedAt", user.getJoinedAt().toString());
        return payload;
    }

    @Transactional
    public Map<String, Object> updateMe(String token, Map<String, String> body) {
        AppUser user = requiredUser(token);

        String fullname = safe(body.get("fullname"), user.getFullname());
        String username = safe(body.get("username"), user.getUsername());
        String email = safe(body.get("email"), user.getEmail());
        String oldPassword = body.getOrDefault("oldPassword", "");
        String newPassword = body.getOrDefault("newPassword", "");

        if (!newPassword.isBlank()) {
            if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
                throw new BadRequestException("Mat khau cu khong dung!");
            }
            if (newPassword.length() < 6) {
                throw new BadRequestException("Mat khau moi phai toi thieu 6 ky tu!");
            }
            user.setPasswordHash(passwordEncoder.encode(newPassword));
        }

        String normalizedEmail = normalizeEmail(email);
        if (!normalizedEmail.equalsIgnoreCase(user.getEmail()) && userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new ConflictException("Email da ton tai!");
        }

        user.setFullname(fullname);
        user.setUsername(username);
        user.setEmail(normalizedEmail);
        userRepository.save(user);

        pushActivity(user, "profile", "Cap nhat thong tin ca nhan", "Vua xong", 0);
        return me(token);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> activities(String token) {
        AppUser user = requiredUser(token);
        List<ActivityLog> logs = activityLogRepository.findTop20ByUserIdOrderByCreatedAtDesc(user.getId());
        List<Map<String, Object>> payload = new ArrayList<>();
        for (ActivityLog log : logs) {
            payload.add(Map.of(
                    "type", log.getType(),
                    "title", log.getTitle(),
                    "time", log.getTimeLabel(),
                    "exp", log.getExp()
            ));
        }
        return payload;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listLibraryDecks(String token) {
        String userId = optionalUserId(token);
        Set<Long> savedIds = userId == null ? Set.of() : savedDeckIds(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Deck deck : deckRepository.findByLibraryDeckTrueOrderByCategoryAscNameAsc()) {
            int count = flashcardRepository.countByDeckId(deck.getId());
            result.add(deckPayload(deck, count, savedIds.contains(deck.getId())));
        }
        return result;
    }

    @Transactional
    public Map<String, Object> saveLibraryDeck(String token, long deckId) {
        AppUser user = requiredUser(token);
        Deck deck = deckRepository.findById(deckId)
                .filter(Deck::isLibraryDeck)
                .orElseThrow(() -> new NotFoundException("Khong tim thay bo the thu vien!"));

        if (!savedDeckRepository.existsByUserIdAndDeckId(user.getId(), deckId)) {
            SavedDeck saved = new SavedDeck();
            saved.setUser(user);
            saved.setDeck(deck);
            saved.setCreatedAt(LocalDateTime.now());
            savedDeckRepository.save(saved);
            pushActivity(user, "library", "Luu bo the \"" + deck.getName() + "\"", "Vua xong", 5);
        }
        return Map.of("message", "Da luu bo the!", "deckId", deckId);
    }

    @Transactional
    public Map<String, Object> unsaveLibraryDeck(String token, long deckId) {
        AppUser user = requiredUser(token);
        savedDeckRepository.deleteByUserIdAndDeckId(user.getId(), deckId);
        return Map.of("message", "Da bo luu bo the!", "deckId", deckId);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listDecks(String token) {
        AppUser user = requiredUser(token);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Deck deck : deckRepository.findByOwnerIdOrderByIdAsc(user.getId())) {
            result.add(deckPayload(deck, flashcardRepository.countByDeckId(deck.getId()), false));
        }

        for (SavedDeck savedDeck : savedDeckRepository.findByUserIdOrderByIdAsc(user.getId())) {
            Deck deck = savedDeck.getDeck();
            if (deck == null) {
                continue;
            }
            result.add(deckPayload(deck, flashcardRepository.countByDeckId(deck.getId()), true));
        }

        result.sort(Comparator.comparing(item -> Long.parseLong(String.valueOf(item.get("id")))));
        return result;
    }

    @Transactional
    public Map<String, Object> createDeck(String token, String name, String description, String category, String difficulty, String visibility) {
        AppUser user = requiredUser(token);
        Deck deck = new Deck();
        deck.setOwner(user);
        deck.setName(safe(name, "Bo the moi"));
        deck.setDescription(safe(description, ""));
        deck.setCategory(safe(category, "GENERAL"));
        deck.setDifficulty(safe(difficulty, "MEDIUM"));
        deck.setLibraryDeck("PUBLIC".equalsIgnoreCase(safe(visibility, "PRIVATE")));
        deck.setProgress(0);
        deck.setCreatedAt(LocalDateTime.now());
        deckRepository.save(deck);

        pushActivity(user, "creation", "Tao bo the \"" + deck.getName() + "\"", "Vua xong", 15);
        return deckPayload(deck, 0, false);
    }

    @Transactional
    public Map<String, Object> updateDeck(String token, long deckId, String name, String description, String category, String difficulty, String visibility) {
        AppUser user = requiredUser(token);
        Deck deck = ownedDeck(user.getId(), deckId);

        deck.setName(safe(name, deck.getName()));
        if (description != null) {
            deck.setDescription(description);
        }
        deck.setCategory(safe(category, deck.getCategory()));
        deck.setDifficulty(safe(difficulty, deck.getDifficulty()));
        if (visibility != null && !visibility.isBlank()) {
            deck.setLibraryDeck("PUBLIC".equalsIgnoreCase(visibility));
        }
        deckRepository.save(deck);
        return deckPayload(deck, flashcardRepository.countByDeckId(deckId), false);
    }

    @Transactional
    public Map<String, Object> deleteDeck(String token, long deckId) {
        AppUser user = requiredUser(token);
        Deck deck = ownedDeck(user.getId(), deckId);

        savedDeckRepository.deleteByDeckId(deckId);
        flashcardRepository.deleteByDeckId(deckId);
        deckRepository.delete(deck);
        pushActivity(user, "delete", "Xoa bo the \"" + deck.getName() + "\"", "Vua xong", 0);
        return Map.of("message", "Xoa bo the thanh cong!", "deckId", deckId);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listFlashcards(String token, long deckId) {
        Deck deck = deckRepository.findById(deckId).orElse(null);
        if (deck == null) {
            return List.of();
        }

        String userId = optionalUserId(token);
        if (!deck.isLibraryDeck()) {
            if (userId == null || !deck.getOwner().getId().equals(userId)) {
                return List.of();
            }
        }

        List<Map<String, Object>> payload = new ArrayList<>();
        for (Flashcard card : flashcardRepository.findByDeckIdOrderByIdAsc(deckId)) {
            payload.add(cardPayload(card));
        }
        return payload;
    }

    @Transactional
    public Map<String, Object> addFlashcard(String token, long deckId, String front, String back, String note) {
        AppUser user = requiredUser(token);
        Deck deck = ownedDeck(user.getId(), deckId);

        Flashcard card = new Flashcard();
        card.setDeck(deck);
        card.setFrontText(safe(front, ""));
        card.setBackText(safe(back, ""));
        card.setNote(safe(note, ""));
        card.setCreatedAt(LocalDateTime.now());
        flashcardRepository.save(card);
        return cardPayload(card);
    }

    @Transactional
    public Map<String, Object> updateFlashcard(String token, long deckId, long cardId, String front, String back, String note) {
        AppUser user = requiredUser(token);
        ownedDeck(user.getId(), deckId);
        Flashcard card = flashcardRepository.findById(cardId)
                .filter(existing -> existing.getDeck().getId().equals(deckId))
                .orElseThrow(() -> new NotFoundException("Khong tim thay the!"));

        card.setFrontText(safe(front, card.getFrontText()));
        card.setBackText(safe(back, card.getBackText()));
        if (note != null) {
            card.setNote(note);
        }
        flashcardRepository.save(card);
        return cardPayload(card);
    }

    @Transactional
    public Map<String, Object> deleteFlashcard(String token, long deckId, long cardId) {
        AppUser user = requiredUser(token);
        ownedDeck(user.getId(), deckId);
        Flashcard card = flashcardRepository.findById(cardId)
                .filter(existing -> existing.getDeck().getId().equals(deckId))
                .orElseThrow(() -> new NotFoundException("Khong tim thay the!"));

        flashcardRepository.delete(card);
        return Map.of("message", "Xoa the thanh cong!", "cardId", cardId, "deckId", deckId);
    }

    @Transactional
    public Map<String, Object> saveStudyResult(String token, Map<String, Object> body) {
        AppUser user = requiredUser(token);

        int totalCards = parseInt(body.get("totalCards"), 0);
        int correct = parseInt(body.get("correct"), 0);
        int wrong = parseInt(body.get("wrong"), 0);
        int expGained = Math.max(parseInt(body.get("expGained"), correct * 10), 0);

        int newExp = user.getExp() + expGained;
        int newLevel = Math.max(1, (newExp / 500) + 1);
        int newStreak = user.getStreak() + 1;

        user.setExp(newExp);
        user.setLevel(newLevel);
        user.setStreak(newStreak);
        userRepository.save(user);

        UserStat stat = statOf(user);
        stat.setSessions(stat.getSessions() + 1);
        stat.setCardsMastered(stat.getCardsMastered() + correct);
        stat.setMinutes(stat.getMinutes() + Math.max(totalCards * 2, 5));
        stat.setStreak(newStreak);
        userStatRepository.save(stat);

        int accuracy = totalCards == 0 ? 0 : (int) Math.round((correct * 100.0) / totalCards);
        StudyResult study = new StudyResult();
        study.setUser(user);
        study.setDeckId(parseLong(body.get("deckId")));
        study.setTotalCards(totalCards);
        study.setCorrect(correct);
        study.setWrong(wrong);
        study.setExpGained(expGained);
        study.setAccuracy(accuracy);
        study.setCurrentExp(newExp);
        study.setCurrentLevel(newLevel);
        study.setCreatedAt(LocalDateTime.now());
        studyResultRepository.save(study);

        pushActivity(user, "study", "Hoan thanh 1 phien hoc", "Vua xong", expGained);

        Map<String, Object> response = new HashMap<>(latestStudyResult(token));
        response.put("message", "Luu ket qua hoc thanh cong!");
        return response;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> latestStudyResult(String token) {
        AppUser user = requiredUser(token);
        Optional<StudyResult> resultOpt = studyResultRepository.findFirstByUserIdOrderByCreatedAtDesc(user.getId());
        if (resultOpt.isEmpty()) {
            return Map.of(
                    "deckId", "",
                    "totalCards", 0,
                    "correct", 0,
                    "wrong", 0,
                    "expGained", 0,
                    "accuracy", 0,
                    "currentExp", user.getExp(),
                    "currentLevel", user.getLevel()
            );
        }

        StudyResult r = resultOpt.get();
        return Map.of(
                "deckId", r.getDeckId() == null ? "" : r.getDeckId(),
                "totalCards", r.getTotalCards(),
                "correct", r.getCorrect(),
                "wrong", r.getWrong(),
                "expGained", r.getExpGained(),
                "accuracy", r.getAccuracy(),
                "currentExp", r.getCurrentExp(),
                "currentLevel", r.getCurrentLevel()
        );
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> leaderboard() {
        List<AppUser> users = userRepository.findTopByExpDesc(PageRequest.of(0, 100));
        List<Map<String, Object>> payload = new ArrayList<>();
        int rank = 1;
        for (AppUser u : users) {
            payload.add(Map.of(
                    "rank", rank++,
                    "fullname", u.getFullname(),
                    "username", u.getUsername(),
                    "exp", u.getExp(),
                    "totalExp", u.getExp(),
                    "level", u.getLevel(),
                    "avatar", u.getAvatar()
            ));
        }
        return payload;
    }

    @Transactional
    public Map<String, Object> achievements(String token) {
        AppUser user = requiredUser(token);
        UserStat stats = statOf(user);
        long deckCount = deckRepository.countByOwnerId(user.getId());
        int topRank = rankOfUser(user.getId());

        List<Map<String, Object>> items = new ArrayList<>();
        items.add(achievement("a1", "learning", "Nguoi hoc cham chi", "Hoan thanh 10 phien hoc", "fa-book-open", 50, stats.getSessions(), 10));
        items.add(achievement("a2", "creation", "Nha sang tao", "Tao 3 bo the dau tien", "fa-plus-circle", 30, (int) deckCount, 3));
        items.add(achievement("a3", "streak", "Ngon lua bat dau", "Hoc 3 ngay lien tiep", "fa-fire", 20, stats.getStreak(), 3));
        items.add(achievement("a4", "learning", "Tri nho sieu pham", "Hoc thuoc 500 the", "fa-brain", 300, stats.getCardsMastered(), 500));
        items.add(achievement("a5", "social", "Top 10", "Lot vao Top 10 bang xep hang", "fa-ranking-star", 200, topRank <= 10 ? 1 : 0, 1));

        int unlockedCount = 0;
        int totalExp = 0;
        for (Map<String, Object> item : items) {
            if (Boolean.TRUE.equals(item.get("unlocked"))) {
                unlockedCount++;
                totalExp += parseInt(item.get("rewardExp"), 0);
            }
        }

        return Map.of(
                "summary", Map.of(
                        "totalUnlocked", unlockedCount,
                        "totalAchievements", items.size(),
                        "totalExpFromAchievement", totalExp
                ),
                "items", items
        );
    }

    private int rankOfUser(String userId) {
        return (int) userRepository.countUsersWithHigherExp(userId) + 1;
    }

    private Map<String, Object> achievement(String id, String category, String name, String desc, String icon, int rewardExp, int current, int target) {
        int safeCurrent = Math.max(0, current);
        int safeTarget = Math.max(1, target);
        boolean unlocked = safeCurrent >= safeTarget;
        int percent = Math.min(100, (int) Math.round((safeCurrent * 100.0) / safeTarget));
        return Map.of(
                "id", id,
                "category", category,
                "name", name,
                "description", desc,
                "icon", icon,
                "rewardExp", rewardExp,
                "unlocked", unlocked,
                "progressCurrent", Math.min(safeCurrent, safeTarget),
                "progressTarget", safeTarget,
                "progressPercent", percent
        );
    }

    private UserStat statOf(AppUser user) {
        return userStatRepository.findByUserId(user.getId()).orElseGet(() -> {
            UserStat stat = new UserStat();
            stat.setUser(user);
            stat.setSessions(0);
            stat.setCardsMastered(0);
            stat.setMinutes(0);
            stat.setStreak(user.getStreak());
            return userStatRepository.save(stat);
        });
    }

    private void pushActivity(AppUser user, String type, String title, String timeLabel, int exp) {
        ActivityLog log = new ActivityLog();
        log.setUser(user);
        log.setType(type);
        log.setTitle(title);
        log.setTimeLabel(timeLabel);
        log.setExp(exp);
        log.setCreatedAt(LocalDateTime.now());
        activityLogRepository.save(log);
    }

    private AppUser requiredUser(String token) {
        String userId = optionalUserId(token);
        if (userId == null) {
            throw new UnauthorizedException("Vui long dang nhap!");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("Vui long dang nhap!"));
    }

    private Deck ownedDeck(String userId, long deckId) {
        Deck deck = deckRepository.findById(deckId).orElseThrow(() -> new NotFoundException("Khong tim thay bo the!"));
        if (!deck.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Khong tim thay bo the!");
        }
        return deck;
    }

    private String optionalUserId(String token) {
        return jwtService.extractAccessUserIdFromBearerToken(token);
    }

    private void persistRefreshToken(AppUser user, String refreshToken) {
        RefreshToken entity = new RefreshToken();
        entity.setUser(user);
        entity.setToken(refreshToken);
        entity.setRevoked(false);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setExpiresAt(LocalDateTime.now().plusDays(refreshExpirationDays));
        refreshTokenRepository.save(entity);
    }

    private Set<Long> savedDeckIds(String userId) {
        Set<Long> result = new HashSet<>();
        for (SavedDeck savedDeck : savedDeckRepository.findByUserIdOrderByIdAsc(userId)) {
            if (savedDeck.getDeck() != null && savedDeck.getDeck().getId() != null) {
                result.add(savedDeck.getDeck().getId());
            }
        }
        return result;
    }

    private String generateUserId() {
        return "user" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    private Map<String, Object> userPayload(AppUser u) {
        return Map.of(
                "id", u.getId(),
                "fullname", u.getFullname(),
                "username", u.getUsername(),
                "email", u.getEmail(),
                "exp", u.getExp(),
                "level", u.getLevel(),
                "avatar", u.getAvatar(),
                "streak", u.getStreak()
        );
    }

    private Map<String, Object> deckPayload(Deck d, int cardCount, boolean saved) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", d.getId());
        payload.put("name", d.getName());
        payload.put("description", d.getDescription());
        payload.put("category", d.getCategory());
        payload.put("difficulty", d.getDifficulty());
        payload.put("library", d.isLibraryDeck());
        payload.put("visibility", d.isLibraryDeck() ? "PUBLIC" : "PRIVATE");
        payload.put("saved", saved);
        payload.put("progress", d.getProgress());
        payload.put("cardCount", cardCount);
        payload.put("createdAt", d.getCreatedAt().toString());
        return payload;
    }

    private Map<String, Object> cardPayload(Flashcard c) {
        return Map.of(
                "id", c.getId(),
                "deckId", c.getDeck().getId(),
                "front", c.getFrontText(),
                "back", c.getBackText(),
                "note", c.getNote(),
                "description", c.getNote(),
                "createdAt", c.getCreatedAt().toString()
        );
    }

    private int parseInt(Object value, int fallback) {
        if (value == null) {
            return fallback;
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }

    private Long parseLong(Object value) {
        if (value == null) {
            return null;
        }
        try {
            String raw = String.valueOf(value).trim();
            if (raw.isEmpty()) {
                return null;
            }
            return Long.parseLong(raw);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            return "";
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String safe(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
