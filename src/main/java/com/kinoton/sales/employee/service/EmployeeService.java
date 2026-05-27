package com.kinoton.sales.employee.service;

import com.kinoton.sales.employee.dto.EmployeeCreateRequest;
import com.kinoton.sales.employee.dto.EmployeeManagementResponse;
import com.kinoton.sales.employee.dto.EmployeeOptionDto;

import java.util.List;

public interface EmployeeService {

    EmployeeManagementResponse selectEmployeeManagement();

    List<EmployeeOptionDto> selectEmployeeOptionList();

    EmployeeOptionDto selectActiveEmployeeOptionDetails(Long employeeId);

    Long insertEmployee(EmployeeCreateRequest request, Long createdBy);
}
