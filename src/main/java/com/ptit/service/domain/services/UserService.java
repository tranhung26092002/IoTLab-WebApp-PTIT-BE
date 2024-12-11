package com.ptit.service.domain.services;

import com.ptit.service.app.dtos.UserDto;
import com.ptit.service.app.dtos.auth.ChangePasswordDto;
import com.ptit.service.app.responses.MessageResponse;
import com.ptit.service.app.responses.ResponsePage;
import com.ptit.service.app.responses.user.UserResponse;
import com.ptit.service.domain.entities.User;
import com.ptit.service.domain.enums.StateUser;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

@Service
public interface UserService {
    ResponsePage<User, UserResponse> getALlUser(Pageable pageable);
    UserResponse getUserById(Long id);

    MessageResponse sendNotificationToAllUsers(String subject, Context context);

    MessageResponse changePassword(ChangePasswordDto request, Authentication authentication);
    MessageResponse changeStatusAccount(Long id, StateUser status);

    User findUserByEmail(String username);

    UserResponse updateUser(Long id, UserDto userDto);

    MessageResponse deleteUser(Long id);

    MessageResponse createUser(UserDto userDto);
}
