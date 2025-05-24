package com.kamehoot.kamehoot_backend.controllers;

import org.springframework.http.ResponseEntity;

import com.kamehoot.kamehoot_backend.DTOs.AuthenticateRequest;
import com.kamehoot.kamehoot_backend.DTOs.TokenResponse;

public interface IAuthenticationController {

    ResponseEntity<TokenResponse> login(AuthenticateRequest userLogin);

    ResponseEntity<TokenResponse> register(AuthenticateRequest userRegister);

}