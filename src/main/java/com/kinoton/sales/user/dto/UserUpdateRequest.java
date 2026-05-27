package com.kinoton.sales.user.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

public class UserUpdateRequest {

    @NotBlank
    private String name;

    private boolean active;
    private boolean passwordResetRequired;
    private List<String> roleCodes = new ArrayList<>();
    private List<String> readableDepartmentCodes = new ArrayList<>();
    private List<String> writableDepartmentCodes = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isPasswordResetRequired() {
        return passwordResetRequired;
    }

    public void setPasswordResetRequired(boolean passwordResetRequired) {
        this.passwordResetRequired = passwordResetRequired;
    }

    public List<String> getRoleCodes() {
        return roleCodes;
    }

    public void setRoleCodes(List<String> roleCodes) {
        this.roleCodes = roleCodes == null ? new ArrayList<>() : roleCodes;
    }

    public List<String> getReadableDepartmentCodes() {
        return readableDepartmentCodes;
    }

    public void setReadableDepartmentCodes(List<String> readableDepartmentCodes) {
        this.readableDepartmentCodes = readableDepartmentCodes == null ? new ArrayList<>() : readableDepartmentCodes;
    }

    public List<String> getWritableDepartmentCodes() {
        return writableDepartmentCodes;
    }

    public void setWritableDepartmentCodes(List<String> writableDepartmentCodes) {
        this.writableDepartmentCodes = writableDepartmentCodes == null ? new ArrayList<>() : writableDepartmentCodes;
    }
}
