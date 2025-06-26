package com.ptit.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ptit.service.dto.ChangePasswordDTO;
import com.ptit.service.dto.UserDTO;
import com.ptit.service.dto.UserFilterDTO;
import com.ptit.service.entity.enums.StateUser;
import com.ptit.service.response.*;
import com.ptit.service.service.UserService;
import com.ptit.service.util.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
//@PreAuthorize("hasRole('APPLICANT') || hasRole('EMPLOYER') || hasRole('ADMIN')")
public class UserController extends BaseController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<DataResponse<UserResponse>> getMe(@RequestHeader(name = Constant.headerUserId) Long userId) {
        UserResponse user = userService.getUserById(userId);
        return success(user);
    }

    // Get list of users with role admin
    @GetMapping("/instructors")
    public ResponseEntity<DataResponse<PaginationData<InstructorReponse>>> getAllInstructors(Pageable pageable) {
        var page = userService.getAllInstructors(pageable);
        return successWithPagination(page);
    }

    @PutMapping("/me")
    public ResponseEntity<DataResponse<UserResponse>> updateMe(
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

        UserResponse updatedUser = userService.updateMe(userId, user, file);
        return success(updatedUser);
    }

    // Get list of attendees
    @GetMapping("/attendances")
    public ResponseEntity<DataResponse<PaginationData<AttendanceResponse>>> getAllAttendances(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Pageable pageable
    ) {
        if (date == null) {
            date = LocalDate.now(); // hoặc một giá trị mặc định khác
        }
        var page = userService.getAllAttendances(date, pageable);
        return successWithPagination(page);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public ResponseEntity<DataResponse<PaginationData<UserResponse>>> getAllUser(Pageable pageable) {
        var page = userService.getALlUser(pageable);
        return successWithPagination(page);
    }

    @GetMapping("/filter")
    public ResponseEntity<DataResponse<PaginationData<UserResponse>>> searchUser(@ModelAttribute UserFilterDTO userFilterDTO, Pageable pageable) {
        List<String> allowedFields = Arrays.asList(
                "id", "userName", "fullName", "classCode");

        if (userFilterDTO.getSortField() != null && !allowedFields.contains(userFilterDTO.getSortField())) {
            userFilterDTO.setSortField("id");
        }
        var page = userService.searchUser(userFilterDTO, pageable);
        return successWithPagination(page);
    }

    @PostMapping()
    public ResponseEntity<DataResponse<MessageResponse>> createUser(@Valid @RequestBody UserDTO userDto) {
        MessageResponse response = userService.createUser(userDto);
        return success(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return success(user);
    }

    // Get user by username
    @GetMapping("/username/{userName}")
    public ResponseEntity<DataResponse<StudentResponse>> getUserByUsername(@PathVariable String userName) {
        StudentResponse student = userService.getUserByUsername(userName);
        return success(student);
    }

    @PostMapping("/send-notification")
    public ResponseEntity<DataResponse<MessageResponse>> sendNotificationToAllUsers(
            @RequestParam String subject,
            @RequestParam String message) {
        Context context = new Context();
        context.setVariable("message", message);

        MessageResponse response = userService.sendNotificationToAllUsers(subject, context);
        return success(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<DataResponse<MessageResponse>> changePassword(@Valid @RequestBody ChangePasswordDTO request, Authentication authentication) {
        MessageResponse response = userService.changePassword(request, authentication);
        return success(response);
    }

    //    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<DataResponse<MessageResponse>> changeStatusAccount(@PathVariable Long id, @PathParam("status") StateUser status) {
        MessageResponse response = userService.changeStatusAccount(id, status);
        return success(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<DataResponse<UserResponse>> updateUser(@PathVariable Long id, @RequestBody UserDTO userDto) {
        UserResponse updatedUser = userService.updateUser(id, userDto);
        return success(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DataResponse<MessageResponse>> deleteUser(@PathVariable Long id) {
        MessageResponse response = userService.deleteUser(id);
        return success(response);
    }
}
