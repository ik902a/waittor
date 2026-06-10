package by.klihal.waittor.data.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    private Long id;

    private String token;
    private Long userId;
    private Instant expiryDate;

    public RefreshToken() {
    }

    public RefreshToken(Long id, String token, Long userId, Instant expiryDate) {
        this.id = id;
        this.token = token;
        this.userId = userId;
        this.expiryDate = expiryDate;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
