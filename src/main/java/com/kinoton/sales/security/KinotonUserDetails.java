package com.kinoton.sales.security;

import com.kinoton.sales.user.dto.AuthUserDto;
import com.kinoton.sales.user.dto.DepartmentPermissionDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class KinotonUserDetails implements UserDetails {

    private final AuthUserDto user;
    private final Collection<? extends GrantedAuthority> authorities;
    private final List<DepartmentPermissionDto> departmentPermissions;

    public KinotonUserDetails(
        AuthUserDto user,
        Collection<? extends GrantedAuthority> authorities,
        List<DepartmentPermissionDto> departmentPermissions
    ) {
        this.user = user;
        this.authorities = authorities;
        this.departmentPermissions = departmentPermissions == null ? List.of() : List.copyOf(departmentPermissions);
    }

    public Long selectUserId() {
        return user.getUserId();
    }

    public String selectName() {
        return user.getName();
    }

    public boolean isPasswordResetRequired() {
        return user.isPasswordResetRequired();
    }

    public boolean hasRole(String roleCode) {
        String expectedAuthority = "ROLE_" + roleCode;
        return authorities.stream()
            .anyMatch(authority -> expectedAuthority.equals(authority.getAuthority()));
    }

    public List<String> selectReadableDepartmentCodes() {
        return departmentPermissions.stream()
            .filter(DepartmentPermissionDto::isCanRead)
            .map(DepartmentPermissionDto::getDepartmentCode)
            .distinct()
            .toList();
    }

    public List<String> selectWritableDepartmentCodes() {
        return departmentPermissions.stream()
            .filter(DepartmentPermissionDto::isCanWrite)
            .map(DepartmentPermissionDto::getDepartmentCode)
            .distinct()
            .toList();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.isActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isActive();
    }
}
