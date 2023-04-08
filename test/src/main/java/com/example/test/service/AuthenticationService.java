package com.example.test.service;

import com.example.test.security.AuthenticationRequest;
import com.example.test.security.AuthenticationResponce;
import com.example.test.security.RegisterRequest;

public interface AuthenticationService {
    AuthenticationResponce register(RegisterRequest request);

    AuthenticationResponce authenticate(AuthenticationRequest request);
}
