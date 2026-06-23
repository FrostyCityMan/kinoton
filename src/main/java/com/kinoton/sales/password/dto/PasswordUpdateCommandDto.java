package com.kinoton.sales.password.dto;

public class PasswordUpdateCommandDto {

    private Long userId;
    private String passwordHash;

    public PasswordUpdateCommandDto(Long userId, String passwordHash) {
        this.userId = userId;
        this.passwordHash = passwordHash;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
