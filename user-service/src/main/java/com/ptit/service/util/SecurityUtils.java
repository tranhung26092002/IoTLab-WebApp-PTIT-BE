package com.ptit.service.util;

import com.ptit.service.exception.BaseException;
import com.ptit.service.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated())
            throw new BaseException(ErrorCode.USER_UNAUTHENTICATED);
        return authentication.getName();
    }


}
