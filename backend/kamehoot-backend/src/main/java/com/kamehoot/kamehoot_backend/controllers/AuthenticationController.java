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
import com.kamehoot.kamehoot_backend.DTOs.TwoFaSetupRequest;
import com.kamehoot.kamehoot_backend.DTOs.TwoFaVerifyRequest;
import com.kamehoot.kamehoot_backend.models.AppUser;
import com.kamehoot.kamehoot_backend.security.JwtService;
import com.kamehoot.kamehoot_backend.security.TwoFactorAuthService;
import com.kamehoot.kamehoot_backend.services.IUserService;

@RequestMapping("/auth")
@RestController
public class AuthenticationController implements IAuthenticationController {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final IUserService userService;
    private final TwoFactorAuthService twoFactorAuthService;

    public AuthenticationController(JwtService jwtService, AuthenticationManager authenticationManager,
            IUserService userService, TwoFactorAuthService twoFactorAuthService) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.twoFactorAuthService = twoFactorAuthService;
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

    @Override
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody AuthenticateRequest userLogin) {

        System.out.println("Login received");

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLogin.username(), userLogin.password()));

        AppUser user = this.userService.getUserByUsername(userLogin.username());
        if (user.isTwoFaEnabled()) {
            if (userLogin.totpCode() == null) {
                return ResponseEntity.ok(new TokenResponse(null, 0L, true, "2FA code required"));
            }

            if (!twoFactorAuthService.verifyCode(user.getTwoFaSecret(), userLogin.totpCode())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid 2FA code");
            }
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtService.generateToken(authentication);
        return new ResponseEntity<>(new TokenResponse(token, this.jwtService.getTokenExpirationSeconds()),
                HttpStatus.OK);
    }

    @PostMapping("/setup-2fa")
    public ResponseEntity<TwoFaSetupRequest> setup2FA(Authentication authentication) {
        String username = authentication.getName();
        String secretKey = twoFactorAuthService.generateSecretKey();
        String qrCodeUrl = twoFactorAuthService.getQRBarcodeURL(username, secretKey);

        userService.setTwoFaSecret(username, secretKey);

        return ResponseEntity.ok(new TwoFaSetupRequest(qrCodeUrl, secretKey));
    }

    @PostMapping("/verify-2fa")
    public ResponseEntity<String> verify2FA(@RequestBody TwoFaVerifyRequest request, Authentication authentication) {
        String username = authentication.getName();
        AppUser user = this.userService.getUserByUsername(username);

        if (twoFactorAuthService.verifyCode(user.getTwoFaSecret(), request.totpCode())) {
            userService.enable2FA(username);
            return ResponseEntity.ok("2FA enable successfully");
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid 2FA code");
        }
    }

    @PostMapping("/disable-2fa")
    public ResponseEntity<String> disable2FA(@RequestBody TwoFaVerifyRequest request, Authentication authentication) {
        String username = authentication.getName();
        AppUser user = this.userService.getUserByUsername(username);

        if (!user.isTwoFaEnabled()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "2FA is not enable");
        }

        if (twoFactorAuthService.verifyCode(user.getTwoFaSecret(), request.totpCode())) {
            userService.disable2FA(username);
            return ResponseEntity.ok("2FA disabled successfully");
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid 2FA code");
        }
    }

    @Override
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
