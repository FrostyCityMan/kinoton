package com.kinoton.sales.department.dao;

import com.kinoton.sales.department.dto.DepartmentCommandDto;
import com.kinoton.sales.department.dto.DepartmentListItemDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DepartmentDao {

    List<DepartmentListItemDto> selectDepartmentList();

    DepartmentListItemDto selectDepartmentDetails(Long departmentId);

    Long selectDepartmentIdByCode(String code);

    int selectActiveDepartmentCount();

    void insertDepartment(DepartmentCommandDto command);

    void updateDepartment(DepartmentCommandDto command);

    void deleteDepartment(Long departmentId);
}
