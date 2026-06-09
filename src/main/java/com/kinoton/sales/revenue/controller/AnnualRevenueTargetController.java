package com.kinoton.sales.revenue.controller;

import com.kinoton.sales.common.exception.BusinessException;
import com.kinoton.sales.common.response.ApiResponse;
import com.kinoton.sales.revenue.dto.AnnualRevenueTargetResponse;
import com.kinoton.sales.revenue.dto.AnnualRevenueTargetRowDto;
import com.kinoton.sales.revenue.dto.AnnualRevenueTargetSaveRequest;
import com.kinoton.sales.revenue.dto.AnnualRevenueTargetSaveRow;
import com.kinoton.sales.revenue.service.AnnualRevenueTargetService;
import com.kinoton.sales.security.KinotonUserDetails;
import com.kinoton.sales.year.service.BusinessYearService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AnnualRevenueTargetController {

    private final AnnualRevenueTargetService annualRevenueTargetService;
    private final BusinessYearService businessYearService;

    public AnnualRevenueTargetController(
        AnnualRevenueTargetService annualRevenueTargetService,
        BusinessYearService businessYearService
    ) {
        this.annualRevenueTargetService = annualRevenueTargetService;
        this.businessYearService = businessYearService;
    }

    @GetMapping("/annual-targets")
    public String selectAnnualRevenueTargetPage(
        @RequestParam(value = "businessYear", required = false) Integer businessYear,
        Model model
    ) {
        AnnualRevenueTargetResponse response = annualRevenueTargetService.selectAnnualRevenueTarget(businessYear);
        addAnnualTargetModel(model, response, toSaveRequest(response));
        model.addAttribute("newBusinessYear", response.businessYear() + 1);
        return "revenue/annual";
    }

    @GetMapping("/api/v1/annual-targets")
    @ResponseBody
    public ApiResponse<AnnualRevenueTargetResponse> selectAnnualRevenueTarget(
        @RequestParam(value = "businessYear", required = false) Integer businessYear
    ) {
        return ApiResponse.success(annualRevenueTargetService.selectAnnualRevenueTarget(businessYear));
    }

    @PostMapping("/annual-targets")
    public String saveAnnualRevenueTargetPage(
        @Valid @ModelAttribute("saveRequest") AnnualRevenueTargetSaveRequest request,
        BindingResult bindingResult,
        Model model,
        Authentication authentication,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            AnnualRevenueTargetResponse response = annualRevenueTargetService.selectAnnualRevenueTarget(request.getBusinessYear());
            addAnnualTargetModel(model, response, request);
            model.addAttribute("newBusinessYear", response.businessYear() + 1);
            model.addAttribute("errorMessage", "연간 매출 목표 입력값이 올바르지 않습니다.");
            return "revenue/annual";
        }

        try {
            AnnualRevenueTargetResponse response = annualRevenueTargetService.saveAnnualRevenueTarget(
                request,
                selectAuthenticatedUserId(authentication)
            );
            redirectAttributes.addFlashAttribute("message", "연간 매출 목표가 저장되었습니다.");
            return "redirect:/annual-targets?businessYear=" + response.businessYear();
        } catch (BusinessException exception) {
            AnnualRevenueTargetResponse response = annualRevenueTargetService.selectAnnualRevenueTarget(request.getBusinessYear());
            addAnnualTargetModel(model, response, request);
            model.addAttribute("newBusinessYear", response.businessYear() + 1);
            model.addAttribute("errorMessage", exception.getMessage());
            return "revenue/annual";
        }
    }

    @PutMapping("/api/v1/annual-targets")
    @ResponseBody
    public ApiResponse<AnnualRevenueTargetResponse> saveAnnualRevenueTarget(
        @Valid @RequestBody AnnualRevenueTargetSaveRequest request,
        Authentication authentication
    ) {
        return ApiResponse.success(
            annualRevenueTargetService.saveAnnualRevenueTarget(request, selectAuthenticatedUserId(authentication)),
            "연간 매출 목표가 저장되었습니다."
        );
    }

    @PostMapping("/annual-targets/years")
    public String insertBusinessYearPage(
        @RequestParam Integer businessYear,
        Authentication authentication,
        RedirectAttributes redirectAttributes
    ) {
        try {
            businessYearService.insertBusinessYear(businessYear, selectAuthenticatedUserId(authentication));
            redirectAttributes.addFlashAttribute("message", businessYear + "년이 추가되었습니다.");
            return "redirect:/annual-targets?businessYear=" + businessYear;
        } catch (BusinessException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            return "redirect:/annual-targets";
        }
    }

    private void addAnnualTargetModel(
        Model model,
        AnnualRevenueTargetResponse response,
        AnnualRevenueTargetSaveRequest saveRequest
    ) {
        model.addAttribute("response", response);
        model.addAttribute("years", response.years());
        model.addAttribute("targets", response.targets());
        model.addAttribute("saveRequest", saveRequest);
    }

    private AnnualRevenueTargetSaveRequest toSaveRequest(AnnualRevenueTargetResponse response) {
        AnnualRevenueTargetSaveRequest request = new AnnualRevenueTargetSaveRequest();
        request.setBusinessYear(response.businessYear());
        List<AnnualRevenueTargetSaveRow> targets = response.targets().stream()
            .map(this::toSaveRow)
            .toList();
        request.setTargets(targets);
        return request;
    }

    private AnnualRevenueTargetSaveRow toSaveRow(AnnualRevenueTargetRowDto target) {
        AnnualRevenueTargetSaveRow row = new AnnualRevenueTargetSaveRow();
        row.setDepartmentId(target.getDepartmentId());
        row.setTargetAmount(target.getTargetAmount());
        return row;
    }

    private Long selectAuthenticatedUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof KinotonUserDetails userDetails) {
            return userDetails.selectUserId();
        }
        return null;
    }
}
