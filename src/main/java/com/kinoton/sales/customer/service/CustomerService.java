package com.kinoton.sales.customer.service;

import com.kinoton.sales.customer.dto.CustomerCreateRequest;
import com.kinoton.sales.customer.dto.CustomerListItemDto;
import com.kinoton.sales.customer.dto.CustomerManagementResponse;
import com.kinoton.sales.customer.dto.CustomerOptionDto;
import com.kinoton.sales.customer.dto.CustomerUpdateRequest;

import java.util.List;

public interface CustomerService {

    CustomerManagementResponse selectCustomerManagement();

    CustomerListItemDto selectCustomerDetails(Long customerId);

    CustomerOptionDto selectActiveCustomerDetails(Long customerId);

    List<CustomerOptionDto> selectActiveCustomerOptionList();

    Long insertCustomer(CustomerCreateRequest request, Long createdBy);

    void updateCustomer(Long customerId, CustomerUpdateRequest request, Long updatedBy);

    void deleteCustomer(Long customerId, Long updatedBy);
}
