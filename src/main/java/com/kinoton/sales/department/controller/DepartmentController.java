package com.kinoton.sales.department.controller;

import com.kinoton.sales.common.exception.BusinessException;
import com.kinoton.sales.common.response.ApiResponse;
import com.kinoton.sales.department.dto.DepartmentCreateRequest;
import com.kinoton.sales.department.dto.DepartmentListItemDto;
import com.kinoton.sales.department.dto.DepartmentManagementResponse;
import com.kinoton.sales.department.dto.DepartmentUpdateRequest;
import com.kinoton.sales.department.service.DepartmentService;
import com.kinoton.sales.security.KinotonUserDetails;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
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
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping("/departments")
    public String selectDepartmentManagementPage(Model model) {
        addDepartmentManagementModel(model, new DepartmentCreateRequest());
        return "department/list";
    }

    @GetMapping("/api/v1/departments")
    @ResponseBody
    public ApiResponse<DepartmentManagementResponse> selectDepartmentManagement() {
        return ApiResponse.success(departmentService.selectDepartmentManagement());
    }

    @GetMapping("/departments/{departmentId}")
    public String selectDepartmentEditPage(
        @PathVariable Long departmentId,
        Model model
    ) {
        DepartmentListItemDto department = departmentService.selectDepartmentDetails(departmentId);
        model.addAttribute("department", department);
        model.addAttribute("updateRequest", selectUpdateRequest(department));
        return "department/edit";
    }

    @GetMapping("/api/v1/departments/{departmentId}")
    @ResponseBody
    public ApiResponse<DepartmentListItemDto> selectDepartmentDetails(@PathVariable Long departmentId) {
        return ApiResponse.success(departmentService.selectDepartmentDetails(departmentId));
    }

    @PostMapping("/departments")
    public String insertDepartmentPage(
        @Valid @ModelAttribute("createRequest") DepartmentCreateRequest request,
        BindingResult bindingResult,
        Model model,
        Authentication authentication,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            addDepartmentManagementModel(model, request);
            return "department/list";
        }

        try {
            departmentService.insertDepartment(request, selectAuthenticatedUserId(authentication));
            redirectAttributes.addFlashAttribute("message", "사업본부가 등록되었습니다.");
            return "redirect:/departments";
        } catch (BusinessException exception) {
            addDepartmentManagementModel(model, request);
            model.addAttribute("errorMessage", exception.getMessage());
            return "department/list";
        }
    }

    @PostMapping("/api/v1/departments")
    @ResponseBody
    public ApiResponse<Long> insertDepartment(
        @Valid @RequestBody DepartmentCreateRequest request,
        Authentication authentication
    ) {
        return ApiResponse.success(
            departmentService.insertDepartment(request, selectAuthenticatedUserId(authentication)),
            "사업본부가 등록되었습니다."
        );
    }

    @PostMapping("/departments/{departmentId}")
    public String updateDepartmentPage(
        @PathVariable Long departmentId,
        @Valid @ModelAttribute DepartmentUpdateRequest request,
        BindingResult bindingResult,
        Authentication authentication,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "사업본부 수정 입력값이 올바르지 않습니다.");
            return "redirect:/departments/" + departmentId;
        }

        try {
            departmentService.updateDepartment(departmentId, request, selectAuthenticatedUserId(authentication));
            redirectAttributes.addFlashAttribute("message", "사업본부가 수정되었습니다.");
        } catch (BusinessException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/departments/" + departmentId;
    }

    @PutMapping("/api/v1/departments/{departmentId}")
    @ResponseBody
    public ApiResponse<Void> updateDepartment(
        @PathVariable Long departmentId,
        @Valid @RequestBody DepartmentUpdateRequest request,
        Authentication authentication
    ) {
        departmentService.updateDepartment(departmentId, request, selectAuthenticatedUserId(authentication));
        return ApiResponse.success(null, "사업본부가 수정되었습니다.");
    }

    @PostMapping("/departments/{departmentId}/delete")
    public String deleteDepartmentPage(
        @PathVariable Long departmentId,
        Authentication authentication,
        RedirectAttributes redirectAttributes
    ) {
        try {
            departmentService.deleteDepartment(departmentId, selectAuthenticatedUserId(authentication));
            redirectAttributes.addFlashAttribute("message", "사업본부가 비활성화되었습니다.");
        } catch (BusinessException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/departments";
    }

    @DeleteMapping("/api/v1/departments/{departmentId}")
    @ResponseBody
    public ApiResponse<Void> deleteDepartment(
        @PathVariable Long departmentId,
        Authentication authentication
    ) {
        departmentService.deleteDepartment(departmentId, selectAuthenticatedUserId(authentication));
        return ApiResponse.success(null, "사업본부가 비활성화되었습니다.");
    }

    private void addDepartmentManagementModel(Model model, DepartmentCreateRequest request) {
        model.addAttribute("departments", departmentService.selectDepartmentManagement().departments());
        model.addAttribute("createRequest", request);
    }

    private DepartmentUpdateRequest selectUpdateRequest(DepartmentListItemDto department) {
        DepartmentUpdateRequest request = new DepartmentUpdateRequest();
        request.setCode(department.getCode());
        request.setName(department.getName());
        request.setDisplayOrder(department.getDisplayOrder());
        request.setActive(department.isActive());
        return request;
    }

    private Long selectAuthenticatedUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof KinotonUserDetails userDetails) {
            return userDetails.selectUserId();
        }
        return null;
    }
}
