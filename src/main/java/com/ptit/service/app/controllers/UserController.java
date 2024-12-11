package com.ptit.service.app.controllers;

import com.ptit.service.app.dtos.UserDto;
import com.ptit.service.app.dtos.auth.ChangePasswordDto;
import com.ptit.service.app.responses.MessageResponse;
import com.ptit.service.app.responses.ResponsePage;
import com.ptit.service.app.responses.user.UserResponse;
import com.ptit.service.domain.entities.User;
import com.ptit.service.domain.enums.StateUser;
import com.ptit.service.domain.services.UserService;
import com.ptit.service.util.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.websocket.server.PathParam;
import org.thymeleaf.context.Context;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
//@PreAuthorize("hasRole('APPLICANT') || hasRole('EMPLOYER') || hasRole('ADMIN')")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public UserResponse getMe(@RequestHeader(name = Constant.headerUserId) Long userId) {
        return userService.getUserById(userId);
    }

    /**
     * update or create profile
     *
     * @param userId
     * @param dto
     * @return
     */
    @PutMapping("/me")
    public UserResponse updateMe(
            @RequestHeader(name = Constant.headerUserId) Long userId, @RequestBody UserDto dto) {
        return userService.updateUser(userId, dto);
    }

//    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public ResponsePage<User, UserResponse> getAllUser(Pageable pageable){
        return userService.getALlUser(pageable);
    }

    @PostMapping()
    public MessageResponse createUser(@Valid @RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping("/send-notification")
    public MessageResponse sendNotificationToAllUsers(
            @RequestParam String subject,
            @RequestParam String message) {
        Context context = new Context();
        context.setVariable("message", message);

        return userService.sendNotificationToAllUsers(subject, context);
    }

    @PostMapping("/change-password")
    public MessageResponse changePassword(@Valid @RequestBody ChangePasswordDto request, Authentication authentication) {
        return userService.changePassword(request, authentication);
    }

//    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public MessageResponse changeStatusAccount(@PathVariable Long id, @PathParam("status") StateUser status) {
        return userService.changeStatusAccount(id, status);
    }

    @PutMapping("/update/{id}")
    public UserResponse updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        return userService.updateUser(id, userDto);
    }

    @DeleteMapping("/{id}")
    public MessageResponse deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }
}
