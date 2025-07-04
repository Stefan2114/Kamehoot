package com.kamehoot.kamehoot_backend.utils;

import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.kamehoot.kamehoot_backend.services.IUserService;

@Component
public class AuthenticationUtil {

    private final IUserService userService;

    public AuthenticationUtil(IUserService userService) {
        this.userService = userService;
    }

    public UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth instanceof UsernamePasswordAuthenticationToken jwtAuth)) {
            throw new AccessDeniedException("No authentication user");
        }

        String username = jwtAuth.getName();
        return userService.getUserByUsername(username).getId();
    }
}
