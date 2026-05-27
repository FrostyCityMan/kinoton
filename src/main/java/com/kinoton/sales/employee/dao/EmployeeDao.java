package com.kinoton.sales.employee.dao;

import com.kinoton.sales.employee.dto.EmployeeCreateCommandDto;
import com.kinoton.sales.employee.dto.EmployeeListItemDto;
import com.kinoton.sales.employee.dto.EmployeeOptionDto;
import com.kinoton.sales.probability.dto.DepartmentOptionDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EmployeeDao {

    List<EmployeeListItemDto> selectEmployeeList();

    List<EmployeeOptionDto> selectEmployeeOptionList();

    EmployeeOptionDto selectActiveEmployeeOptionDetails(Long employeeId);

    Long selectDepartmentIdByCode(String departmentCode);

    List<DepartmentOptionDto> selectDepartmentOptionList();

    void insertEmployee(EmployeeCreateCommandDto command);
}
