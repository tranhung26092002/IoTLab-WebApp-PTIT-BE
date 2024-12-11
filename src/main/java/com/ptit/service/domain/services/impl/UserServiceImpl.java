package com.ptit.service.domain.services.impl;

import com.ommanisoft.common.exceptions.ExceptionOm;
import com.ommanisoft.common.utils.FnCommon;
import com.ptit.service.app.dtos.UserDto;
import com.ptit.service.app.dtos.auth.ChangePasswordDto;
import com.ptit.service.app.responses.user.UserResponse;
import com.ptit.service.app.responses.address.AddressResponse;
import com.ptit.service.app.responses.MessageResponse;
import com.ptit.service.app.responses.ResponsePage;
import com.ptit.service.domain.entities.Address;
import com.ptit.service.domain.entities.User;
import com.ptit.service.domain.enums.StateUser;
import com.ptit.service.domain.exceptions.ErrorMessage;
import com.ptit.service.domain.repositories.UserRepository;
import com.ptit.service.domain.services.UserService;
import com.ptit.service.domain.utils.AddressUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final AddressUtil addressUtil;

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public ResponsePage<User, UserResponse> getALlUser(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);

        Page<UserResponse> userResponsePage = userPage.map(this::convertToUserResponse);

        log.info("Get all user successful!");
        return new ResponsePage<>(userResponsePage);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ExceptionOm(HttpStatus.NOT_FOUND, ErrorMessage.USER_NOT_FOUND.val())
        );

        log.info("Get user with id: {} successful!", id);
        return convertToUserResponse(user);
    }

    @Override
    public MessageResponse sendNotificationToAllUsers(String subject, Context context) {
        // Lấy danh sách tất cả người dùng
        List<User> users = userRepository.findAll();

        // Tạo danh sách email và tên người dùng
        List<Map<String, Object>> personalizedContexts = new ArrayList<>();
        for (User user : users) {
            // Tạo một context mới cho mỗi người dùng
            Context userContext = new Context();  // Tạo context mới

            // Cung cấp tên của từng người dùng vào context
            userContext.setVariable("name", user.getUserName());
            userContext.setVariable("message", context.getVariable("message"));

            // Cung cấp thêm các thông tin khác vào context
            userContext.setVariable("office", "Công ty ABC");
            userContext.setVariable("companyEmail", "support@company.com");
            userContext.setVariable("supportPhone", "0901234567");
            userContext.setVariable("supportEmail", "support@company.com");

            // Thêm thông tin vào danh sách personalizedContexts
//            personalizedContexts.add(Map.of("email", user.getEmail(), "context", userContext));
        }

        // Gửi email theo từng nhóm để tránh quá tải
        int batchSize = 50;  // Bạn có thể điều chỉnh kích thước nhóm
        for (int i = 0; i < personalizedContexts.size(); i += batchSize) {
            // Lấy nhóm nhỏ từ danh sách personalizedContexts
            List<Map<String, Object>> batch = personalizedContexts.subList(i, Math.min(i + batchSize, personalizedContexts.size()));

            // Gửi email cho nhóm hiện tại
//            emailService.sendEmails(batch, subject, "notificationTemplate");
        }

        log.info("Send notification to all users successfully!");

        return MessageResponse.builder().message("Send notification to all users successfully!").build();
    }

    @Override
    @Transactional
    public MessageResponse changePassword(ChangePasswordDto request, Authentication authentication) {
        String currentPassword = request.getCurrentPassword();

        String phoneNumber = authentication.getName();
        User user = userRepository.findByPhoneNumber(phoneNumber).orElseThrow(
                () -> new ExceptionOm(HttpStatus.NOT_FOUND, ErrorMessage.USER_NOT_FOUND.val())
        );

        if(!passwordEncoder.matches(currentPassword,user.getPassword())){
            throw new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessage.CURRENT_PASSWORD_INCORRECT.val());
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Change password successful!");
        return MessageResponse
                .builder()
                .message("Change password successful!")
                .build();
    }

    @Override
    @Transactional
    public MessageResponse changeStatusAccount(Long id, StateUser status) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ExceptionOm(HttpStatus.NOT_FOUND, ErrorMessage.USER_NOT_FOUND.val())
        );

        user.setStatus(status);
        userRepository.save(user);

        log.info("Change status: {} account successful!", status);
        return MessageResponse
                .builder()
                .message("Change status account successful!")
                .build();
    }

    @Override
    public UserResponse updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ExceptionOm(HttpStatus.NOT_FOUND, ErrorMessage.USER_NOT_FOUND.val())
        );

        if (userDto.getAddress() != null) {
            Address address = addressUtil.generateAddress(userDto.getAddress());
            if (!addressUtil.isSameAddress(user.getAddress(), address)) {
                user.setAddress(address);
            }
        }

        FnCommon.coppyNonNullProperties(user, userDto);

        userRepository.save(user);

        return convertToUserResponse(user);
    }

    @Override
    public MessageResponse deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ExceptionOm(HttpStatus.NOT_FOUND, ErrorMessage.USER_NOT_FOUND.val())
        );

        user.setDeleted(true);
        userRepository.save(user);

        return MessageResponse.builder().message("Delete user successful!").build();
    }

    @Override
    public MessageResponse createUser(UserDto userDto) {
        if (userRepository.existsByPhoneNumber(userDto.getPhoneNumber())) {
            throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessage.PHONE_NUMBER_EXISTED.val());
        }

        User user = new User();

        if (userDto.getAddress() != null) {
            Address address = addressUtil.generateAddress(userDto.getAddress());
            user.setAddress(address);
        }

        FnCommon.coppyNonNullProperties(user, userDto);

        user.setPassword(passwordEncoder.encode("12345678"));
        user.setDeleted(false);

        userRepository.save(user);

        return MessageResponse.builder().message("Create user successful!").build();
    }

    private UserResponse convertToUserResponse(User user){
        UserResponse userResponse = new UserResponse();

        FnCommon.coppyNonNullProperties(userResponse, user);

        return userResponse;
    }
}
