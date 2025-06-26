package com.ptit.service.service;

import com.ptit.service.dto.UserDTO;
import com.ptit.service.dto.UserFilterDTO;
import com.ptit.service.dto.ChangePasswordDTO;
import com.ptit.service.response.AttendanceResponse;
import com.ptit.service.response.MessageResponse;
import com.ptit.service.response.InstructorReponse;
import com.ptit.service.response.StudentResponse;
import com.ptit.service.response.UserResponse;
import com.ptit.service.entity.Attendance;
import com.ptit.service.entity.User;
import com.ptit.service.entity.enums.StateUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;

import java.time.LocalDate;

@Service
public interface UserService {
    Page<UserResponse> getALlUser(Pageable pageable);

    UserResponse getUserById(Long id);

    MessageResponse sendNotificationToAllUsers(String subject, Context context);

    MessageResponse changePassword(ChangePasswordDTO request, Authentication authentication);

    MessageResponse changeStatusAccount(Long id, StateUser status);

    User findUserByEmail(String username);

    UserResponse updateMe(Long id, UserDTO userDto, MultipartFile file);

    UserResponse updateUser(Long id, UserDTO userDto);

    MessageResponse deleteUser(Long id);

    MessageResponse createUser(UserDTO userDto);

    Page<InstructorReponse> getAllInstructors(Pageable pageable);

    StudentResponse getUserByUsername(String userName);

    Page<AttendanceResponse> getAllAttendances(LocalDate date, Pageable pageable);

    Page<UserResponse> searchUser(UserFilterDTO userFilterDTO, Pageable pageable);
}
