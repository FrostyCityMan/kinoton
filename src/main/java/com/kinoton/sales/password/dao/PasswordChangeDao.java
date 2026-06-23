package com.kinoton.sales.password.dao;

import com.kinoton.sales.password.dto.PasswordUpdateCommandDto;
import com.kinoton.sales.user.dto.AuthUserDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PasswordChangeDao {

    AuthUserDto selectPasswordUserDetails(Long userId);

    int updatePassword(PasswordUpdateCommandDto command);
}
