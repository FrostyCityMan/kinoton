package com.kinoton.sales.password.service;

import com.kinoton.sales.password.dto.PasswordChangeRequest;
import org.springframework.security.core.Authentication;

public interface PasswordChangeService {

    void changePassword(PasswordChangeRequest request, Authentication authentication);
}
