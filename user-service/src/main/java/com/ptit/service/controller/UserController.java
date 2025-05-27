package com.ptit.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ptit.service.dto.UserDTO;
import com.ptit.service.dto.UserFilterDTO;
import com.ptit.service.dto.ChangePasswordDTO;
import com.ptit.service.response.AttendanceResponse;
import com.ptit.service.response.MessageResponse;
import com.ptit.service.response.ResponsePage;
import com.ptit.service.response.InstructorReponse;
import com.ptit.service.response.StudentResponse;
import com.ptit.service.response.UserResponse;
import com.ptit.service.entity.Attendance;
import com.ptit.service.entity.User;
import com.ptit.service.entity.enums.StateUser;
import com.ptit.service.service.UserService;
import com.ptit.service.util.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.websocket.server.PathParam;

import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

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

    // Get list of users with role admin
    @GetMapping("/instructors")
    public ResponsePage<User, InstructorReponse> getAllInstructors(Pageable pageable){
        return userService.getAllInstructors(pageable);
    }

    @PutMapping("/me")
    public UserResponse updateMe(
            @RequestHeader(name = Constant.headerUserId) Long userId,
            @RequestParam(value = "user", required = false) String userJson,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Đăng ký JavaTimeModule
//        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        UserDTO user = null;

        if (userJson != null) {
            user = objectMapper.readValue(userJson, UserDTO.class);
        }

        return userService.updateMe(userId, user, file);
    }

    // Get list of attendees
    @GetMapping("/attendances")
    public ResponsePage<Attendance, AttendanceResponse> getAllAttendances(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Pageable pageable
    ) {
        if (date == null) {
            date = LocalDate.now(); // hoặc một giá trị mặc định khác
        }
        return userService.getAllAttendances(date, pageable);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public ResponsePage<User, UserResponse> getAllUser(Pageable pageable){
        return userService.getALlUser(pageable);
    }

    @GetMapping("/filter")
    public ResponsePage<User, UserResponse> searchUser(@ModelAttribute UserFilterDTO userFilterDTO, Pageable pageable) {
        List<String> allowedFields = Arrays.asList(
                "id", "userName", "fullName", "classCode");

        if (!allowedFields.contains(userFilterDTO.getSortField())) {
            userFilterDTO.setSortField("id");
        }
        return userService.searchUser(userFilterDTO, pageable);
    }

    @PostMapping()
    public MessageResponse createUser(@Valid @RequestBody UserDTO userDto) {
        return userService.createUser(userDto);
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // Get user by username
    @GetMapping("/username/{userName}")
    public StudentResponse getUserByUsername(@PathVariable String userName) {
        return userService.getUserByUsername(userName);
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
    public MessageResponse changePassword(@Valid @RequestBody ChangePasswordDTO request, Authentication authentication) {
        return userService.changePassword(request, authentication);
    }

//    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public MessageResponse changeStatusAccount(@PathVariable Long id, @PathParam("status") StateUser status) {
        return userService.changeStatusAccount(id, status);
    }

    @PutMapping("/update/{id}")
    public UserResponse updateUser(@PathVariable Long id, @RequestBody UserDTO userDto) {
        return userService.updateUser(id, userDto);
    }

    @DeleteMapping("/{id}")
    public MessageResponse deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }
}
