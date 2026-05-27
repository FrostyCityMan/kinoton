package com.kinoton.sales.department.service;

import com.kinoton.sales.department.dto.DepartmentCreateRequest;
import com.kinoton.sales.department.dto.DepartmentListItemDto;
import com.kinoton.sales.department.dto.DepartmentManagementResponse;
import com.kinoton.sales.department.dto.DepartmentUpdateRequest;

public interface DepartmentService {

    DepartmentManagementResponse selectDepartmentManagement();

    DepartmentListItemDto selectDepartmentDetails(Long departmentId);

    Long insertDepartment(DepartmentCreateRequest request, Long actorUserId);

    void updateDepartment(Long departmentId, DepartmentUpdateRequest request, Long actorUserId);

    void deleteDepartment(Long departmentId, Long actorUserId);
}
