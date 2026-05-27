package com.kinoton.sales.probability.controller;

import com.kinoton.sales.common.response.ApiResponse;
import com.kinoton.sales.probability.dto.ProbabilityStageSaveRequest;
import com.kinoton.sales.probability.dto.ProbabilityStageSaveRow;
import com.kinoton.sales.probability.dto.ProbabilityStageSettingResponse;
import com.kinoton.sales.probability.service.ProbabilityStageService;
import com.kinoton.sales.security.KinotonUserDetails;
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

import java.util.List;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class ProbabilityStageController {

    private final ProbabilityStageService probabilityStageService;

    public ProbabilityStageController(ProbabilityStageService probabilityStageService) {
        this.probabilityStageService = probabilityStageService;
    }

    @GetMapping("/probability-stages")
    public String selectProbabilityStageSettingPage(Model model) {
        ProbabilityStageSettingResponse response = probabilityStageService.selectProbabilityStageSetting();
        model.addAttribute("setting", response);
        model.addAttribute("saveRequest", toSaveRequest(response));
        return "probability/stages";
    }

    @GetMapping("/api/v1/probability-stages")
    @ResponseBody
    public ApiResponse<ProbabilityStageSettingResponse> selectProbabilityStageSetting() {
        return ApiResponse.success(probabilityStageService.selectProbabilityStageSetting());
    }

    @GetMapping("/api/v1/probability-stages/{departmentCode}")
    @ResponseBody
    public ApiResponse<ProbabilityStageSettingResponse> selectProbabilityStageSettingByLegacyDepartment(
        @PathVariable String departmentCode
    ) {
        return ApiResponse.success(probabilityStageService.selectProbabilityStageSetting(departmentCode));
    }

    @PostMapping("/probability-stages")
    public String saveProbabilityStageSettingPage(
        @Valid @ModelAttribute ProbabilityStageSaveRequest request,
        Authentication authentication,
        RedirectAttributes redirectAttributes
    ) {
        probabilityStageService.saveProbabilityStageSetting(request, selectAuthenticatedUserId(authentication));
        redirectAttributes.addFlashAttribute("message", "수주확률 설정이 저장되었습니다.");
        return "redirect:/probability-stages";
    }

    @PostMapping("/probability-stages/{departmentCode}")
    public String saveProbabilityStageSettingPageByLegacyDepartment(
        @PathVariable String departmentCode,
        @Valid @ModelAttribute ProbabilityStageSaveRequest request,
        Authentication authentication,
        RedirectAttributes redirectAttributes
    ) {
        probabilityStageService.saveProbabilityStageSetting(departmentCode, request, selectAuthenticatedUserId(authentication));
        redirectAttributes.addFlashAttribute("message", "수주확률 설정이 저장되었습니다.");
        return "redirect:/probability-stages";
    }

    @PutMapping("/api/v1/probability-stages")
    @ResponseBody
    public ApiResponse<ProbabilityStageSettingResponse> saveProbabilityStageSetting(
        @Valid @RequestBody ProbabilityStageSaveRequest request,
        Authentication authentication
    ) {
        return ApiResponse.success(
            probabilityStageService.saveProbabilityStageSetting(request, selectAuthenticatedUserId(authentication)),
            "수주확률 설정이 저장되었습니다."
        );
    }

    @PutMapping("/api/v1/probability-stages/{departmentCode}")
    @ResponseBody
    public ApiResponse<ProbabilityStageSettingResponse> saveProbabilityStageSettingByLegacyDepartment(
        @PathVariable String departmentCode,
        @Valid @RequestBody ProbabilityStageSaveRequest request,
        Authentication authentication
    ) {
        return ApiResponse.success(
            probabilityStageService.saveProbabilityStageSetting(departmentCode, request, selectAuthenticatedUserId(authentication)),
            "수주확률 설정이 저장되었습니다."
        );
    }

    private ProbabilityStageSaveRequest toSaveRequest(ProbabilityStageSettingResponse response) {
        ProbabilityStageSaveRequest request = new ProbabilityStageSaveRequest();
        List<ProbabilityStageSaveRow> rows = response.stages().stream()
            .map(stage -> {
                ProbabilityStageSaveRow row = new ProbabilityStageSaveRow();
                row.setProbabilityStageId(stage.getProbabilityStageId());
                row.setProbability(stage.getProbability());
                row.setName(stage.getName());
                row.setDescription(stage.getDescription());
                return row;
            })
            .toList();
        request.setStages(rows);
        return request;
    }

    private Long selectAuthenticatedUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof KinotonUserDetails userDetails) {
            return userDetails.selectUserId();
        }
        return null;
    }
}
