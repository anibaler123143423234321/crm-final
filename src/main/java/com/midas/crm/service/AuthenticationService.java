package com.midas.crm.service;


import com.midas.crm.entity.User;

public interface AuthenticationService {

    User signInAndReturnJWT(User signInRequest);

}