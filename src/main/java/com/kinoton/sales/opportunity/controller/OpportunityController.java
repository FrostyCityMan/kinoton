package com.kinoton.sales.opportunity.controller;

import com.kinoton.sales.attachment.service.AttachmentService;
import com.kinoton.sales.common.exception.BusinessException;
import com.kinoton.sales.common.response.ApiResponse;
import com.kinoton.sales.employee.service.EmployeeService;
import com.kinoton.sales.opportunity.dto.OpportunityCreateRequest;
import com.kinoton.sales.opportunity.dto.OpportunityCreateResponse;
import com.kinoton.sales.opportunity.dto.OpportunityDetailsResponse;
import com.kinoton.sales.opportunity.dto.OpportunityExecutiveCommentCreateRequest;
import com.kinoton.sales.opportunity.dto.OpportunityExecutiveCommentCreateResponse;
import com.kinoton.sales.opportunity.dto.OpportunityListItemDto;
import com.kinoton.sales.opportunity.dto.OpportunityListSearchCondition;
import com.kinoton.sales.opportunity.dto.OpportunityProgressCreateRequest;
import com.kinoton.sales.opportunity.dto.OpportunityProgressCreateResponse;
import com.kinoton.sales.opportunity.service.OpportunityService;
import com.kinoton.sales.probability.service.ProbabilityStageService;
import com.kinoton.sales.security.KinotonUserDetails;
import com.kinoton.sales.user.service.UserManagementService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class OpportunityController {

    private final OpportunityService opportunityService;
    private final AttachmentService attachmentService;
    private final ProbabilityStageService probabilityStageService;
    private final EmployeeService employeeService;
    private final UserManagementService userManagementService;

    public OpportunityController(
        OpportunityService opportunityService,
        AttachmentService attachmentService,
        ProbabilityStageService probabilityStageService,
        EmployeeService employeeService,
        UserManagementService userManagementService
    ) {
        this.opportunityService = opportunityService;
        this.attachmentService = attachmentService;
        this.probabilityStageService = probabilityStageService;
        this.employeeService = employeeService;
        this.userManagementService = userManagementService;
    }

    @GetMapping("/opportunities")
    public String selectOpportunityListPage(
        @ModelAttribute OpportunityListSearchCondition condition,
        Model model,
        Authentication authentication
    ) {
        model.addAttribute("condition", condition);
        model.addAttribute("opportunities", opportunityService.selectOpportunityList(condition, authentication));
        return "opportunity/list";
    }

    @GetMapping("/api/v1/opportunities")
    @ResponseBody
    public ApiResponse<List<OpportunityListItemDto>> selectOpportunityList(
        @ModelAttribute OpportunityListSearchCondition condition,
        Authentication authentication
    ) {
        return ApiResponse.success(opportunityService.selectOpportunityList(condition, authentication));
    }

    @GetMapping("/opportunities/new")
    public String selectOpportunityCreatePage(Model model, Authentication authentication) {
        addOpportunityCreateModel(model, new OpportunityCreateRequest(), authentication);
        return "opportunity/create";
    }

    @PostMapping("/opportunities")
    public String insertOpportunityPage(
        @Valid @ModelAttribute("createRequest") OpportunityCreateRequest request,
        BindingResult bindingResult,
        Model model,
        Authentication authentication,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            addOpportunityCreateModel(model, request, authentication);
            return "opportunity/create";
        }

        try {
            OpportunityCreateResponse response = opportunityService.insertOpportunity(
                request,
                selectAuthenticatedUserId(authentication),
                authentication
            );
            redirectAttributes.addFlashAttribute("message", "영업 사이트가 등록되었습니다.");
            return "redirect:/opportunities/" + response.opportunityId();
        } catch (BusinessException exception) {
            addOpportunityCreateModel(model, request, authentication);
            model.addAttribute("errorMessage", exception.getMessage());
            return "opportunity/create";
        }
    }

    @GetMapping("/opportunities/{opportunityId}")
    public String selectOpportunityDetailsPage(
        @PathVariable Long opportunityId,
        Model model,
        Authentication authentication
    ) {
        OpportunityDetailsResponse response = opportunityService.selectOpportunityDetails(opportunityId, authentication);
        model.addAttribute("details", response.details());
        model.addAttribute("progressList", response.progressList());
        model.addAttribute("probabilityStages", response.probabilityStages());
        model.addAttribute("executiveComments", response.executiveComments());
        model.addAttribute("canWriteExecutiveComment", response.canWriteExecutiveComment());
        model.addAttribute("attachments", attachmentService.selectAttachmentList(opportunityId, authentication));
        model.addAttribute("progressRequest", new OpportunityProgressCreateRequest());
        model.addAttribute("commentRequest", new OpportunityExecutiveCommentCreateRequest());
        return "opportunity/detail";
    }

    @GetMapping("/api/v1/opportunities/{opportunityId}")
    @ResponseBody
    public ApiResponse<OpportunityDetailsResponse> selectOpportunityDetails(
        @PathVariable Long opportunityId,
        Authentication authentication
    ) {
        return ApiResponse.success(opportunityService.selectOpportunityDetails(opportunityId, authentication));
    }

    @PostMapping("/api/v1/opportunities")
    @ResponseBody
    public ApiResponse<OpportunityCreateResponse> insertOpportunity(
        @Valid @RequestBody OpportunityCreateRequest request,
        Authentication authentication
    ) {
        return ApiResponse.success(
            opportunityService.insertOpportunity(request, selectAuthenticatedUserId(authentication), authentication),
            "영업 사이트가 등록되었습니다."
        );
    }

    @PostMapping("/opportunities/{opportunityId}/progress")
    public String insertOpportunityProgressPage(
        @PathVariable Long opportunityId,
        @Valid @ModelAttribute OpportunityProgressCreateRequest request,
        Authentication authentication,
        RedirectAttributes redirectAttributes
    ) {
        opportunityService.insertOpportunityProgress(opportunityId, request, selectAuthenticatedUserId(authentication), authentication);
        redirectAttributes.addFlashAttribute("message", "영업 진행 기록이 추가되었습니다.");
        return "redirect:/opportunities/" + opportunityId;
    }

    @PostMapping("/api/v1/opportunities/{opportunityId}/progress")
    @ResponseBody
    public ApiResponse<OpportunityProgressCreateResponse> insertOpportunityProgress(
        @PathVariable Long opportunityId,
        @Valid @RequestBody OpportunityProgressCreateRequest request,
        Authentication authentication
    ) {
        return ApiResponse.success(
            opportunityService.insertOpportunityProgress(opportunityId, request, selectAuthenticatedUserId(authentication), authentication),
            "영업 진행 기록이 추가되었습니다."
        );
    }

    @PostMapping("/opportunities/{opportunityId}/executive-comments")
    public String insertOpportunityExecutiveCommentPage(
        @PathVariable Long opportunityId,
        @Valid @ModelAttribute("commentRequest") OpportunityExecutiveCommentCreateRequest request,
        Authentication authentication,
        RedirectAttributes redirectAttributes
    ) {
        opportunityService.insertOpportunityExecutiveComment(
            opportunityId,
            request,
            selectAuthenticatedUserId(authentication),
            authentication
        );
        redirectAttributes.addFlashAttribute("message", "임원 코멘트를 등록했습니다.");
        return "redirect:/opportunities/" + opportunityId;
    }

    @PostMapping("/api/v1/opportunities/{opportunityId}/executive-comments")
    @ResponseBody
    public ApiResponse<OpportunityExecutiveCommentCreateResponse> insertOpportunityExecutiveComment(
        @PathVariable Long opportunityId,
        @Valid @RequestBody OpportunityExecutiveCommentCreateRequest request,
        Authentication authentication
    ) {
        return ApiResponse.success(
            opportunityService.insertOpportunityExecutiveComment(
                opportunityId,
                request,
                selectAuthenticatedUserId(authentication),
                authentication
            ),
            "임원 코멘트를 등록했습니다."
        );
    }

    private Long selectAuthenticatedUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof KinotonUserDetails userDetails) {
            return userDetails.selectUserId();
        }
        return null;
    }

    private void addOpportunityCreateModel(Model model, OpportunityCreateRequest request, Authentication authentication) {
        model.addAttribute("createRequest", request);
        model.addAttribute("departments", probabilityStageService.selectWritableDepartmentOptionList(authentication));
        model.addAttribute("employees", employeeService.selectWritableEmployeeOptionList(authentication));
        model.addAttribute("allowedUsers", userManagementService.selectActiveUserOptionList());
        model.addAttribute("probabilityStages", probabilityStageService.selectProbabilityStageSetting().stages());
    }
}
