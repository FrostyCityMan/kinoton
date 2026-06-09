package com.kinoton.sales.customer.controller;

import com.kinoton.sales.common.exception.BusinessException;
import com.kinoton.sales.common.response.ApiResponse;
import com.kinoton.sales.customer.dto.CustomerCreateRequest;
import com.kinoton.sales.customer.dto.CustomerListItemDto;
import com.kinoton.sales.customer.dto.CustomerManagementResponse;
import com.kinoton.sales.customer.dto.CustomerOptionDto;
import com.kinoton.sales.customer.dto.CustomerUpdateRequest;
import com.kinoton.sales.customer.service.CustomerService;
import com.kinoton.sales.security.KinotonUserDetails;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/customers")
    public String selectCustomerListPage(Model model) {
        CustomerManagementResponse response = customerService.selectCustomerManagement();
        model.addAttribute("customers", response.customers());
        model.addAttribute("createRequest", new CustomerCreateRequest());
        return "customer/list";
    }

    @PostMapping("/customers")
    public String insertCustomerPage(
        @Valid @ModelAttribute("createRequest") CustomerCreateRequest request,
        BindingResult bindingResult,
        Model model,
        Authentication authentication,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("customers", customerService.selectCustomerManagement().customers());
            return "customer/list";
        }

        try {
            customerService.insertCustomer(request, selectAuthenticatedUserId(authentication));
            redirectAttributes.addFlashAttribute("message", "고객사가 등록되었습니다.");
            return "redirect:/customers";
        } catch (BusinessException exception) {
            model.addAttribute("customers", customerService.selectCustomerManagement().customers());
            model.addAttribute("errorMessage", exception.getMessage());
            return "customer/list";
        }
    }

    @GetMapping("/customers/{customerId}")
    public String selectCustomerEditPage(@PathVariable Long customerId, Model model) {
        CustomerListItemDto customer = customerService.selectCustomerDetails(customerId);
        CustomerUpdateRequest request = new CustomerUpdateRequest();
        request.setName(customer.getName());
        request.setContactName(customer.getContactName());
        request.setPhone(customer.getPhone());
        request.setEmail(customer.getEmail());
        request.setMemo(customer.getMemo());
        request.setActive(customer.isActive());
        model.addAttribute("customer", customer);
        model.addAttribute("updateRequest", request);
        return "customer/edit";
    }

    @PostMapping("/customers/{customerId}")
    public String updateCustomerPage(
        @PathVariable Long customerId,
        @Valid @ModelAttribute("updateRequest") CustomerUpdateRequest request,
        BindingResult bindingResult,
        Model model,
        Authentication authentication,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("customer", customerService.selectCustomerDetails(customerId));
            return "customer/edit";
        }

        try {
            customerService.updateCustomer(customerId, request, selectAuthenticatedUserId(authentication));
            redirectAttributes.addFlashAttribute("message", "고객사 정보가 수정되었습니다.");
            return "redirect:/customers";
        } catch (BusinessException exception) {
            model.addAttribute("customer", customerService.selectCustomerDetails(customerId));
            model.addAttribute("errorMessage", exception.getMessage());
            return "customer/edit";
        }
    }

    @PostMapping("/customers/{customerId}/delete")
    public String deleteCustomerPage(
        @PathVariable Long customerId,
        Authentication authentication,
        RedirectAttributes redirectAttributes
    ) {
        try {
            customerService.deleteCustomer(customerId, selectAuthenticatedUserId(authentication));
            redirectAttributes.addFlashAttribute("message", "고객사가 삭제되었습니다.");
        } catch (BusinessException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/customers";
    }

    @GetMapping("/api/v1/customers")
    @ResponseBody
    public ApiResponse<List<CustomerOptionDto>> selectCustomerOptionList() {
        return ApiResponse.success(customerService.selectActiveCustomerOptionList());
    }

    private Long selectAuthenticatedUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof KinotonUserDetails userDetails) {
            return userDetails.selectUserId();
        }
        return null;
    }
}
