package com.kinoton.sales.employee.controller;

import com.kinoton.sales.common.response.ApiResponse;
import com.kinoton.sales.employee.dto.EmployeeCreateRequest;
import com.kinoton.sales.employee.dto.EmployeeManagementResponse;
import com.kinoton.sales.employee.dto.EmployeeOptionDto;
import com.kinoton.sales.employee.service.EmployeeService;
import com.kinoton.sales.security.KinotonUserDetails;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/employees")
    public String redirectEmployeeManagementPage() {
        return "redirect:/opportunities/new";
    }

    @GetMapping("/api/v1/employees")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ApiResponse<EmployeeManagementResponse> selectEmployeeManagement() {
        return ApiResponse.success(employeeService.selectEmployeeManagement());
    }

    @GetMapping("/api/v1/employees/options")
    @ResponseBody
    public ApiResponse<List<EmployeeOptionDto>> selectEmployeeOptionList() {
        return ApiResponse.success(employeeService.selectEmployeeOptionList());
    }

    @PostMapping("/api/v1/employees")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ApiResponse<Long> insertEmployee(
        @Valid @RequestBody EmployeeCreateRequest request,
        Authentication authentication
    ) {
        return ApiResponse.success(
            employeeService.insertEmployee(request, selectAuthenticatedUserId(authentication)),
            "직원이 등록되었습니다."
        );
    }

    private Long selectAuthenticatedUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof KinotonUserDetails userDetails) {
            return userDetails.selectUserId();
        }
        return null;
    }
}
