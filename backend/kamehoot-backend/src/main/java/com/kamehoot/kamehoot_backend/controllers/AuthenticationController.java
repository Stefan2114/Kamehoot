package com.kamehoot.kamehoot_backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.kamehoot.kamehoot_backend.DTOs.AuthenticateRequest;
import com.kamehoot.kamehoot_backend.DTOs.TokenResponse;
import com.kamehoot.kamehoot_backend.services.IUserService;
import com.kamehoot.kamehoot_backend.services.JwtService;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final IUserService userService;

    public AuthenticationController(JwtService jwtService, AuthenticationManager authenticationManager,
            IUserService userService) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    // @PostMapping("/token")
    // public String token(@RequestBody AuthenticateRequest userLogin) {

    // System.out.println("Generating token for user: " + userLogin.username());
    // System.out.println("User password: " + userLogin.password());
    // Authentication authentication = this.authenticationManager.authenticate(
    // new UsernamePasswordAuthenticationToken(userLogin.username(),
    // userLogin.password()));
    // return this.tokenService.generateToken(authentication);
    // }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody AuthenticateRequest userLogin) {

        System.out.println("Login received");

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLogin.username(), userLogin.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtService.generateToken(authentication);
        return new ResponseEntity<>(new TokenResponse(token, this.jwtService.getTokenExpirationSeconds()),
                HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@RequestBody AuthenticateRequest userRegister) {

        System.out.println("Register received");
        if (userService.existsByUsername(userRegister.username())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is already taken");
        }
        this.userService.registerUser(userRegister);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userRegister.username(), userRegister.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtService.generateToken(authentication);
        return new ResponseEntity<>(new TokenResponse(token, this.jwtService.getTokenExpirationSeconds()),
                HttpStatus.OK);
    }
}
