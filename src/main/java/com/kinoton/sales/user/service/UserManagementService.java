package com.kinoton.sales.user.service;

import com.kinoton.sales.user.dto.UserCreateRequest;
import com.kinoton.sales.user.dto.UserEditResponse;
import com.kinoton.sales.user.dto.UserManagementResponse;
import com.kinoton.sales.user.dto.UserOptionDto;
import com.kinoton.sales.user.dto.UserUpdateRequest;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface UserManagementService {

    UserManagementResponse selectUserManagement();

    List<UserOptionDto> selectActiveUserOptionList();

    List<UserOptionDto> selectWritableUserOptionList(Authentication authentication);

    UserOptionDto selectActiveUserOptionDetails(Long userId);

    boolean canActiveUserWriteDepartment(Long userId, String departmentCode);

    List<Long> selectActiveUserIdList(List<Long> userIds);

    UserEditResponse selectUserEdit(Long userId);

    Long insertUser(UserCreateRequest request, Long authenticatedUserId);

    void updateUser(Long userId, UserUpdateRequest request, Long authenticatedUserId);
}
