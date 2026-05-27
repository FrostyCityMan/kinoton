package com.kinoton.sales.auth.controller;

import com.kinoton.sales.auth.dto.SignupRequest;
import com.kinoton.sales.auth.service.SignupService;
import com.kinoton.sales.common.exception.BusinessException;
import com.kinoton.sales.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SignupController {

    private final SignupService signupService;

    public SignupController(SignupService signupService) {
        this.signupService = signupService;
    }

    @GetMapping("/signup")
    public String selectSignupPage(Model model) {
        model.addAttribute("signupRequest", new SignupRequest());
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String insertSignupUserPage(
        @Valid @ModelAttribute SignupRequest signupRequest,
        BindingResult bindingResult,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "auth/signup";
        }

        try {
            signupService.insertSignupUser(signupRequest);
        } catch (BusinessException exception) {
            model.addAttribute("errorMessage", exception.getMessage());
            return "auth/signup";
        }

        redirectAttributes.addAttribute("signup", "success");
        return "redirect:/login";
    }

    @PostMapping("/api/v1/signup")
    @ResponseBody
    public ApiResponse<Long> insertSignupUser(@Valid @RequestBody SignupRequest request) {
        return ApiResponse.success(
            signupService.insertSignupUser(request),
            "회원가입 신청이 접수되었습니다. 관리자가 승인하면 로그인할 수 있습니다."
        );
    }
}
