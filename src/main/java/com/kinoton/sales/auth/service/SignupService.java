package com.kinoton.sales.auth.service;

import com.kinoton.sales.auth.dto.SignupRequest;

public interface SignupService {

    Long insertSignupUser(SignupRequest request);
}
