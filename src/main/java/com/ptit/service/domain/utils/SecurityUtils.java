package com.ptit.service.domain.utils;

import com.ommanisoft.common.exceptions.ExceptionOm;
import com.ptit.service.domain.exceptions.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated())
            throw new ExceptionOm(HttpStatus.UNAUTHORIZED, ErrorMessage.USER_UNAUTHENTICATED.val());
        return authentication.getName();
    }


}
