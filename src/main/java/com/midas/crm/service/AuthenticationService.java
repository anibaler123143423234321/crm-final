package com.midas.crm.service;

import com.midas.crm.entity.TokenResponse;
import com.midas.crm.entity.User;

public interface AuthenticationService {
    TokenResponse signInAndReturnJWT(User signInRequest);
    TokenResponse refreshAccessToken(String refreshToken);
}
