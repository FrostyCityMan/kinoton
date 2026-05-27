package com.kinoton.sales.user.service;

import com.kinoton.sales.user.dto.UserCreateRequest;
import com.kinoton.sales.user.dto.UserEditResponse;
import com.kinoton.sales.user.dto.UserManagementResponse;
import com.kinoton.sales.user.dto.UserUpdateRequest;

public interface UserManagementService {

    UserManagementResponse selectUserManagement();

    UserEditResponse selectUserEdit(Long userId);

    Long insertUser(UserCreateRequest request, Long authenticatedUserId);

    void updateUser(Long userId, UserUpdateRequest request, Long authenticatedUserId);
}
