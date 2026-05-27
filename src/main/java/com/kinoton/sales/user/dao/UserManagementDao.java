package com.kinoton.sales.user.dao;

import com.kinoton.sales.user.dto.DepartmentPermissionDto;
import com.kinoton.sales.user.dto.ManagedUserDetailsDto;
import com.kinoton.sales.user.dto.ManagedUserListItemDto;
import com.kinoton.sales.user.dto.RoleOptionDto;
import com.kinoton.sales.user.dto.UserCreateCommandDto;
import com.kinoton.sales.user.dto.UserDepartmentOptionDto;
import com.kinoton.sales.user.dto.UserDepartmentPermissionCommandDto;
import com.kinoton.sales.user.dto.UserRoleCommandDto;
import com.kinoton.sales.user.dto.UserUpdateCommandDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserManagementDao {

    List<ManagedUserListItemDto> selectManagedUserList();

    ManagedUserDetailsDto selectManagedUserDetails(Long userId);

    Long selectUserIdByEmail(String email);

    List<RoleOptionDto> selectRoleOptionList();

    List<UserDepartmentOptionDto> selectDepartmentOptionList();

    List<String> selectManagedUserRoleCodeList(Long userId);

    List<DepartmentPermissionDto> selectManagedUserDepartmentPermissionList(Long userId);

    void insertManagedUser(UserCreateCommandDto command);

    void updateManagedUser(UserUpdateCommandDto command);

    void deleteUserRoleList(Long userId);

    void insertUserRole(UserRoleCommandDto command);

    void deleteUserDepartmentPermissionList(Long userId);

    void insertUserDepartmentPermission(UserDepartmentPermissionCommandDto command);
}
