package com.example.flashlearn.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_stats", uniqueConstraints = @UniqueConstraint(name = "uk_user_stats_user", columnNames = "user_id"))
public class UserStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(nullable = false)
    private int sessions;

    @Column(nullable = false)
    private int cardsMastered;

    @Column(nullable = false)
    private int minutes;

    @Column(nullable = false)
    private int streak;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public int getSessions() {
        return sessions;
    }

    public void setSessions(int sessions) {
        this.sessions = sessions;
    }

    public int getCardsMastered() {
        return cardsMastered;
    }

    public void setCardsMastered(int cardsMastered) {
        this.cardsMastered = cardsMastered;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }
}
