package com.kinoton.sales.user.dao;

import com.kinoton.sales.user.dto.AuthUserDto;
import com.kinoton.sales.user.dto.DepartmentPermissionDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserDao {

    AuthUserDto selectUserByEmail(String email);

    List<String> selectRoleCodeListByUserId(Long userId);

    List<DepartmentPermissionDto> selectDepartmentPermissionListByUserId(Long userId);
}
