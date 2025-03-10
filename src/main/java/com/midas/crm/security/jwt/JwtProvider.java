package com.midas.crm.security.jwt;


import com.midas.crm.entity.User;
import com.midas.crm.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public interface JwtProvider {

    String generateToken(UserPrincipal auth);

    Authentication getAuthentication(HttpServletRequest request);

    String generateToken(User user);

    boolean isTokenValid(HttpServletRequest request);
}