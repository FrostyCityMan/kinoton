package com.kinoton.sales.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

public class UserUpdateRequest {

    @NotBlank
    private String name;

    @Size(max = 100)
    private String position;

    @Size(max = 20)
    private String phoneNumber;

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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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
