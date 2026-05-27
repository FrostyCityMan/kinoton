package com.kinoton.sales.user.controller;

import com.kinoton.sales.common.response.ApiResponse;
import com.kinoton.sales.security.KinotonUserDetails;
import com.kinoton.sales.user.dto.UserCreateRequest;
import com.kinoton.sales.user.dto.UserEditResponse;
import com.kinoton.sales.user.dto.UserManagementResponse;
import com.kinoton.sales.user.dto.UserUpdateRequest;
import com.kinoton.sales.user.service.UserManagementService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class UserManagementController {

    private final UserManagementService userManagementService;

    public UserManagementController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @GetMapping("/users")
    public String selectUserManagementPage(Model model) {
        UserManagementResponse response = userManagementService.selectUserManagement();
        model.addAttribute("users", response.users());
        model.addAttribute("roles", response.roles());
        model.addAttribute("departments", response.departments());
        model.addAttribute("createRequest", new UserCreateRequest());
        return "user/list";
    }

    @GetMapping("/api/v1/users")
    @ResponseBody
    public ApiResponse<UserManagementResponse> selectUserManagement() {
        return ApiResponse.success(userManagementService.selectUserManagement());
    }

    @PostMapping("/users")
    public String insertUserPage(
        @Valid @ModelAttribute UserCreateRequest request,
        Authentication authentication,
        RedirectAttributes redirectAttributes
    ) {
        Long userId = userManagementService.insertUser(request, selectAuthenticatedUserId(authentication));
        redirectAttributes.addFlashAttribute("message", "사용자가 등록되었습니다.");
        return "redirect:/users/" + userId;
    }

    @PostMapping("/api/v1/users")
    @ResponseBody
    public ApiResponse<Long> insertUser(
        @Valid @RequestBody UserCreateRequest request,
        Authentication authentication
    ) {
        return ApiResponse.success(
            userManagementService.insertUser(request, selectAuthenticatedUserId(authentication)),
            "사용자가 등록되었습니다."
        );
    }

    @GetMapping("/users/{userId}")
    public String selectUserEditPage(@PathVariable Long userId, Model model) {
        UserEditResponse response = userManagementService.selectUserEdit(userId);
        model.addAttribute("user", response.user());
        model.addAttribute("selectedRoleCodes", response.selectedRoleCodes());
        model.addAttribute("readableDepartmentCodes", response.readableDepartmentCodes());
        model.addAttribute("writableDepartmentCodes", response.writableDepartmentCodes());
        model.addAttribute("roles", response.roles());
        model.addAttribute("departments", response.departments());
        model.addAttribute("updateRequest", selectUpdateRequest(response));
        return "user/edit";
    }

    @GetMapping("/api/v1/users/{userId}")
    @ResponseBody
    public ApiResponse<UserEditResponse> selectUserEdit(@PathVariable Long userId) {
        return ApiResponse.success(userManagementService.selectUserEdit(userId));
    }

    @PostMapping("/users/{userId}")
    public String updateUserPage(
        @PathVariable Long userId,
        @Valid @ModelAttribute UserUpdateRequest request,
        Authentication authentication,
        RedirectAttributes redirectAttributes
    ) {
        userManagementService.updateUser(userId, request, selectAuthenticatedUserId(authentication));
        redirectAttributes.addFlashAttribute("message", "사용자 권한이 저장되었습니다.");
        return "redirect:/users/" + userId;
    }

    @PutMapping("/api/v1/users/{userId}")
    @ResponseBody
    public ApiResponse<Void> updateUser(
        @PathVariable Long userId,
        @Valid @RequestBody UserUpdateRequest request,
        Authentication authentication
    ) {
        userManagementService.updateUser(userId, request, selectAuthenticatedUserId(authentication));
        return ApiResponse.success(null, "사용자 권한이 저장되었습니다.");
    }

    private UserUpdateRequest selectUpdateRequest(UserEditResponse response) {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setName(response.user().getName());
        request.setActive(response.user().isActive());
        request.setPasswordResetRequired(response.user().isPasswordResetRequired());
        request.setRoleCodes(response.selectedRoleCodes());
        request.setReadableDepartmentCodes(response.readableDepartmentCodes());
        request.setWritableDepartmentCodes(response.writableDepartmentCodes());
        return request;
    }

    private Long selectAuthenticatedUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof KinotonUserDetails userDetails) {
            return userDetails.selectUserId();
        }
        return null;
    }
}
