package com.kinoton.sales.employee.controller;

import com.kinoton.sales.common.exception.BusinessException;
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
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/employees")
    @PreAuthorize("hasRole('ADMIN')")
    public String selectEmployeeManagementPage(Model model) {
        addEmployeeManagementModel(model, new EmployeeCreateRequest());
        return "employee/list";
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

    @PostMapping("/employees")
    @PreAuthorize("hasRole('ADMIN')")
    public String insertEmployeePage(
        @Valid @ModelAttribute("createRequest") EmployeeCreateRequest request,
        BindingResult bindingResult,
        Model model,
        Authentication authentication,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            addEmployeeManagementModel(model, request);
            return "employee/list";
        }

        try {
            employeeService.insertEmployee(request, selectAuthenticatedUserId(authentication));
            redirectAttributes.addFlashAttribute("message", "직원이 등록되었습니다.");
            return "redirect:/employees";
        } catch (BusinessException exception) {
            addEmployeeManagementModel(model, request);
            model.addAttribute("errorMessage", exception.getMessage());
            return "employee/list";
        }
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

    private void addEmployeeManagementModel(Model model, EmployeeCreateRequest request) {
        EmployeeManagementResponse response = employeeService.selectEmployeeManagement();
        model.addAttribute("employees", response.employees());
        model.addAttribute("departments", response.departments());
        model.addAttribute("createRequest", request);
    }

    private Long selectAuthenticatedUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof KinotonUserDetails userDetails) {
            return userDetails.selectUserId();
        }
        return null;
    }
}
