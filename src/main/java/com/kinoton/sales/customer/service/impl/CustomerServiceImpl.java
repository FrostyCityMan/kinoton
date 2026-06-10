package com.kinoton.sales.customer.service.impl;

import com.kinoton.sales.audit.service.AuditLogService;
import com.kinoton.sales.common.exception.BusinessException;
import com.kinoton.sales.common.util.PhoneNumberFormatter;
import com.kinoton.sales.customer.dao.CustomerDao;
import com.kinoton.sales.customer.dto.CustomerCommandDto;
import com.kinoton.sales.customer.dto.CustomerCreateRequest;
import com.kinoton.sales.customer.dto.CustomerListItemDto;
import com.kinoton.sales.customer.dto.CustomerManagementResponse;
import com.kinoton.sales.customer.dto.CustomerOptionDto;
import com.kinoton.sales.customer.dto.CustomerUpdateRequest;
import com.kinoton.sales.customer.service.CustomerService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerDao customerDao;
    private final AuditLogService auditLogService;

    public CustomerServiceImpl(CustomerDao customerDao, AuditLogService auditLogService) {
        this.customerDao = customerDao;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerManagementResponse selectCustomerManagement() {
        return new CustomerManagementResponse(customerDao.selectCustomerList());
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerListItemDto selectCustomerDetails(Long customerId) {
        CustomerListItemDto customer = customerDao.selectCustomerDetails(customerId);
        if (customer == null) {
            throw new BusinessException("고객사를 찾을 수 없습니다.");
        }
        return customer;
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerOptionDto selectActiveCustomerDetails(Long customerId) {
        return customerDao.selectActiveCustomerDetails(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerOptionDto> selectActiveCustomerOptionList() {
        return customerDao.selectActiveCustomerOptionList();
    }

    @Override
    @Transactional
    public Long insertCustomer(CustomerCreateRequest request, Long createdBy) {
        CustomerCommandDto command = selectCommand(request, createdBy);
        try {
            customerDao.insertCustomer(command);
        } catch (DuplicateKeyException exception) {
            throw new BusinessException("이미 등록된 고객사명입니다.");
        }

        auditLogService.insertAuditLog(createdBy, "CUSTOMER", command.getCustomerId(), "INSERT_CUSTOMER", null, selectAuditData(command));
        return command.getCustomerId();
    }

    @Override
    @Transactional
    public void updateCustomer(Long customerId, CustomerUpdateRequest request, Long updatedBy) {
        CustomerListItemDto before = selectCustomerDetails(customerId);
        CustomerCommandDto command = selectCommand(request, updatedBy);
        command.setCustomerId(customerId);
        try {
            customerDao.updateCustomer(command);
        } catch (DuplicateKeyException exception) {
            throw new BusinessException("이미 등록된 고객사명입니다.");
        }

        auditLogService.insertAuditLog(
            updatedBy,
            "CUSTOMER",
            customerId,
            "UPDATE_CUSTOMER",
            selectAuditData(before),
            selectAuditData(command)
        );
    }

    @Override
    @Transactional
    public void deleteCustomer(Long customerId, Long updatedBy) {
        CustomerListItemDto before = selectCustomerDetails(customerId);
        if (before.getOpportunityCount() > 0) {
            throw new BusinessException("영업 사이트에서 사용 중인 고객사는 삭제할 수 없습니다. 비활성 상태로 수정하세요.");
        }
        customerDao.deleteCustomer(customerId);
        auditLogService.insertAuditLog(updatedBy, "CUSTOMER", customerId, "DELETE_CUSTOMER", selectAuditData(before), null);
    }

    private CustomerCommandDto selectCommand(CustomerCreateRequest request, Long updatedBy) {
        CustomerCommandDto command = new CustomerCommandDto();
        command.setName(request.getName().trim());
        command.setContactName(normalizeNullableText(request.getContactName()));
        command.setContactPosition(normalizeNullableText(request.getContactPosition()));
        command.setPhone(PhoneNumberFormatter.normalize(request.getPhone()));
        command.setEmail(normalizeNullableText(request.getEmail()));
        command.setMemo(normalizeNullableText(request.getMemo()));
        command.setActive(request.isActive());
        command.setUpdatedBy(updatedBy);
        return command;
    }

    private String normalizeNullableText(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private Map<String, Object> selectAuditData(CustomerListItemDto customer) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("customerId", customer.getCustomerId());
        data.put("name", customer.getName());
        data.put("contactName", customer.getContactName());
        data.put("contactPosition", customer.getContactPosition());
        data.put("phone", customer.getPhone());
        data.put("email", customer.getEmail());
        data.put("memo", customer.getMemo());
        data.put("active", customer.isActive());
        return data;
    }

    private Map<String, Object> selectAuditData(CustomerCommandDto command) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("customerId", command.getCustomerId());
        data.put("name", command.getName());
        data.put("contactName", command.getContactName());
        data.put("contactPosition", command.getContactPosition());
        data.put("phone", command.getPhone());
        data.put("email", command.getEmail());
        data.put("memo", command.getMemo());
        data.put("active", command.isActive());
        return data;
    }
}
