package com.kinoton.sales.customer.dao;

import com.kinoton.sales.customer.dto.CustomerCommandDto;
import com.kinoton.sales.customer.dto.CustomerListItemDto;
import com.kinoton.sales.customer.dto.CustomerOptionDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CustomerDao {

    List<CustomerListItemDto> selectCustomerList();

    CustomerListItemDto selectCustomerDetails(Long customerId);

    CustomerOptionDto selectActiveCustomerDetails(Long customerId);

    List<CustomerOptionDto> selectActiveCustomerOptionList();

    void insertCustomer(CustomerCommandDto command);

    void updateCustomer(CustomerCommandDto command);

    void deleteCustomer(Long customerId);
}
