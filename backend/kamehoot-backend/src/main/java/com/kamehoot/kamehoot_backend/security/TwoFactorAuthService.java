package com.kamehoot.kamehoot_backend.security;

import org.springframework.stereotype.Service;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;

@Service
public class TwoFactorAuthService {
    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    public String generateSecretKey() {
        final GoogleAuthenticatorKey key = gAuth.createCredentials();
        return key.getKey();
    }

    public String getQRBarcodeURL(String username, String secretKey) {
        return GoogleAuthenticatorQRGenerator.getOtpAuthURL(
                "Kamehoot",
                username,
                new GoogleAuthenticatorKey.Builder(secretKey).build());
    }

    public boolean verifyCode(String secretKey, int code) {
        return gAuth.authorize(secretKey, code);
    }
}