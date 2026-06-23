package com.kinoton.sales.password.controller;

import com.kinoton.sales.common.exception.BusinessException;
import com.kinoton.sales.password.dto.PasswordChangeRequest;
import com.kinoton.sales.password.service.PasswordChangeService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PasswordChangeController {

    private final PasswordChangeService passwordChangeService;

    public PasswordChangeController(PasswordChangeService passwordChangeService) {
        this.passwordChangeService = passwordChangeService;
    }

    @GetMapping("/password/change")
    public String selectPasswordChangePage(Model model) {
        if (!model.containsAttribute("changeRequest")) {
            model.addAttribute("changeRequest", new PasswordChangeRequest());
        }
        return "auth/password-change";
    }

    @PostMapping("/password/change")
    public String changePassword(
        @Valid @ModelAttribute("changeRequest") PasswordChangeRequest request,
        BindingResult bindingResult,
        Authentication authentication,
        RedirectAttributes redirectAttributes,
        Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", selectValidationMessage(bindingResult));
            return "auth/password-change";
        }

        try {
            passwordChangeService.changePassword(request, authentication);
        } catch (BusinessException exception) {
            model.addAttribute("errorMessage", exception.getMessage());
            return "auth/password-change";
        }

        redirectAttributes.addFlashAttribute("message", "비밀번호가 변경되었습니다.");
        return "redirect:/dashboard";
    }

    private String selectValidationMessage(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
            .findFirst()
            .map(error -> error.getDefaultMessage() == null ? "입력값을 확인하세요." : error.getDefaultMessage())
            .orElse("입력값을 확인하세요.");
    }
}
