package com.alwx.backend.utils;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.function.Supplier;

import com.alwx.backend.service.UserService;

public class AdminAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
    private final UserService userService;

    public AdminAuthorizationManager(UserService userService) {
        this.userService = userService;
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        Authentication auth = authentication.get();
        if(auth == null || !auth.isAuthenticated()){
            return new AuthorizationDecision(false);
        }
        UserDetails userDetails = userService.loadUserByUsername(auth.getName());
        return new AuthorizationDecision(userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")));
    }
}
