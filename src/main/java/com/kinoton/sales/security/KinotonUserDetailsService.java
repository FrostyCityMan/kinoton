package com.kinoton.sales.security;

import com.kinoton.sales.user.dao.UserDao;
import com.kinoton.sales.user.dto.AuthUserDto;
import com.kinoton.sales.user.dto.DepartmentPermissionDto;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class KinotonUserDetailsService implements UserDetailsService {

    private final UserDao userDao;

    public KinotonUserDetailsService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AuthUserDto user = userDao.selectUserByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }

        List<SimpleGrantedAuthority> authorities = userDao.selectRoleCodeListByUserId(user.getUserId()).stream()
            .map(roleCode -> new SimpleGrantedAuthority("ROLE_" + roleCode))
            .toList();
        List<DepartmentPermissionDto> departmentPermissions = userDao.selectDepartmentPermissionListByUserId(user.getUserId());

        return new KinotonUserDetails(user, authorities, departmentPermissions);
    }
}
