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
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
@RequestMapping("/api/users")
@Api(tags = "User Management")
@RequiredArgsConstructor
@Slf4j
public class UserController extends BaseController {
    private final UserService userService;

    @GetMapping("/me")
    @ApiOperation("Lấy thông tin cá nhân của người dùng hiện tại")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Lấy thông tin thành công", response = UserResponse.class),
        @ApiResponse(code = 401, message = "Chưa đăng nhập"),
        @ApiResponse(code = 404, message = "Không tìm thấy người dùng")
    })
    public ResponseEntity<DataResponse<UserResponse>> getMe(@RequestHeader(name = Constant.headerUserId) Long userId) {
        UserResponse user = userService.getUserById(userId);
        return success(user);
    }

    @GetMapping("/instructors")
    @ApiOperation("Lấy danh sách giảng viên")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Lấy danh sách thành công"),
        @ApiResponse(code = 500, message = "Lỗi server")
    })
    public ResponseEntity<DataResponse<PaginationData<InstructorReponse>>> getAllInstructors(Pageable pageable) {
        var page = userService.getAllInstructors(pageable);
        return successWithPagination(page);
    }

    @PutMapping("/me")
    @ApiOperation("Cập nhật thông tin cá nhân")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Cập nhật thành công", response = UserResponse.class),
        @ApiResponse(code = 400, message = "Dữ liệu không hợp lệ"),
        @ApiResponse(code = 401, message = "Chưa đăng nhập")
    })
    public ResponseEntity<DataResponse<UserResponse>> updateMe(
            @RequestHeader(name = Constant.headerUserId) Long userId,
            @RequestParam(value = "user", required = false) String userJson,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        UserDTO user = null;

        if (userJson != null) {
            user = objectMapper.readValue(userJson, UserDTO.class);
        }

        UserResponse updatedUser = userService.updateMe(userId, user, file);
        return success(updatedUser);
    }

    @GetMapping("/attendances")
    @ApiOperation("Lấy danh sách điểm danh")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Lấy danh sách thành công"),
        @ApiResponse(code = 500, message = "Lỗi server")
    })
    public ResponseEntity<DataResponse<PaginationData<AttendanceResponse>>> getAllAttendances(
            @ApiParam(value = "Ngày điểm danh", example = "2024-01-15") 
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Pageable pageable
    ) {
        if (date == null) {
            date = LocalDate.now();
        }
        var page = userService.getAllAttendances(date, pageable);
        return successWithPagination(page);
    }

    @GetMapping
    @ApiOperation("Lấy danh sách người dùng có phân trang")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Lấy danh sách thành công"),
        @ApiResponse(code = 500, message = "Lỗi server")
    })
    public ResponseEntity<DataResponse<PaginationData<UserResponse>>> getAllUser(Pageable pageable) {
        var page = userService.getALlUser(pageable);
        return successWithPagination(page);
    }

    @GetMapping("/filter")
    @ApiOperation("Tìm kiếm và lọc người dùng")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Tìm kiếm thành công"),
        @ApiResponse(code = 400, message = "Tham số không hợp lệ"),
        @ApiResponse(code = 500, message = "Lỗi server")
    })
    public ResponseEntity<DataResponse<PaginationData<UserResponse>>> searchUser(
            @ApiParam(value = "Thông tin lọc") 
            @ModelAttribute UserFilterDTO userFilterDTO, 
            Pageable pageable) {
        List<String> allowedFields = Arrays.asList("id", "userName", "fullName", "classCode");

        if (userFilterDTO.getSortField() != null && !allowedFields.contains(userFilterDTO.getSortField())) {
            userFilterDTO.setSortField("id");
        }
        var page = userService.searchUser(userFilterDTO, pageable);
        return successWithPagination(page);
    }

    @PostMapping
    @ApiOperation("Tạo người dùng mới")
    @ApiResponses({
        @ApiResponse(code = 201, message = "Tạo thành công"),
        @ApiResponse(code = 400, message = "Dữ liệu không hợp lệ"),
        @ApiResponse(code = 409, message = "Người dùng đã tồn tại"),
        @ApiResponse(code = 500, message = "Lỗi server")
    })
    public ResponseEntity<DataResponse<MessageResponse>> createUser(
            @ApiParam(value = "Thông tin người dùng", required = true) 
            @Valid @RequestBody UserDTO userDto) {
        MessageResponse response = userService.createUser(userDto);
        return success(response);
    }

    @GetMapping("/{id}")
    @ApiOperation("Lấy thông tin người dùng theo ID")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Lấy thông tin thành công", response = UserResponse.class),
        @ApiResponse(code = 404, message = "Không tìm thấy người dùng"),
        @ApiResponse(code = 500, message = "Lỗi server")
    })
    public ResponseEntity<DataResponse<UserResponse>> getUserById(
            @ApiParam(value = "ID của người dùng", example = "1", required = true) 
            @PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return success(user);
    }

    @GetMapping("/username/{userName}")
    @ApiOperation("Lấy thông tin người dùng theo username")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Lấy thông tin thành công", response = StudentResponse.class),
        @ApiResponse(code = 404, message = "Không tìm thấy người dùng"),
        @ApiResponse(code = 500, message = "Lỗi server")
    })
    public ResponseEntity<DataResponse<StudentResponse>> getUserByUsername(
            @ApiParam(value = "Username", example = "student123", required = true) 
            @PathVariable String userName) {
        StudentResponse student = userService.getUserByUsername(userName);
        return success(student);
    }

    @PostMapping("/send-notification")
    @ApiOperation("Gửi thông báo cho tất cả người dùng")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Gửi thông báo thành công"),
        @ApiResponse(code = 400, message = "Dữ liệu không hợp lệ"),
        @ApiResponse(code = 500, message = "Lỗi server")
    })
    public ResponseEntity<DataResponse<MessageResponse>> sendNotificationToAllUsers(
            @ApiParam(value = "Tiêu đề thông báo", example = "Thông báo quan trọng", required = true) 
            @RequestParam String subject,
            @ApiParam(value = "Nội dung thông báo", example = "Đây là nội dung thông báo", required = true) 
            @RequestParam String message) {
        Context context = new Context();
        context.setVariable("message", message);

        MessageResponse response = userService.sendNotificationToAllUsers(subject, context);
        return success(response);
    }

    @PostMapping("/change-password")
    @ApiOperation("Đổi mật khẩu")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Đổi mật khẩu thành công"),
        @ApiResponse(code = 400, message = "Mật khẩu cũ không đúng"),
        @ApiResponse(code = 401, message = "Chưa đăng nhập"),
        @ApiResponse(code = 500, message = "Lỗi server")
    })
    public ResponseEntity<DataResponse<MessageResponse>> changePassword(
            @ApiParam(value = "Thông tin đổi mật khẩu", required = true) 
            @Valid @RequestBody ChangePasswordDTO request, 
            Authentication authentication) {
        MessageResponse response = userService.changePassword(request, authentication);
        return success(response);
    }

    @PutMapping("/{id}")
    @ApiOperation("Thay đổi trạng thái tài khoản")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Thay đổi trạng thái thành công"),
        @ApiResponse(code = 404, message = "Không tìm thấy người dùng"),
        @ApiResponse(code = 500, message = "Lỗi server")
    })
    public ResponseEntity<DataResponse<MessageResponse>> changeStatusAccount(
            @ApiParam(value = "ID của người dùng", example = "1", required = true) 
            @PathVariable Long id, 
            @ApiParam(value = "Trạng thái mới", required = true) 
            @PathParam("status") StateUser status) {
        MessageResponse response = userService.changeStatusAccount(id, status);
        return success(response);
    }

    @PutMapping("/update/{id}")
    @ApiOperation("Cập nhật thông tin người dùng")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Cập nhật thành công", response = UserResponse.class),
        @ApiResponse(code = 400, message = "Dữ liệu không hợp lệ"),
        @ApiResponse(code = 404, message = "Không tìm thấy người dùng"),
        @ApiResponse(code = 500, message = "Lỗi server")
    })
    public ResponseEntity<DataResponse<UserResponse>> updateUser(
            @ApiParam(value = "ID của người dùng", example = "1", required = true) 
            @PathVariable Long id, 
            @ApiParam(value = "Thông tin cập nhật", required = true) 
            @RequestBody UserDTO userDto) {
        UserResponse updatedUser = userService.updateUser(id, userDto);
        return success(updatedUser);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Xóa người dùng")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Xóa thành công"),
        @ApiResponse(code = 404, message = "Không tìm thấy người dùng"),
        @ApiResponse(code = 500, message = "Lỗi server")
    })
    public ResponseEntity<DataResponse<MessageResponse>> deleteUser(
            @ApiParam(value = "ID của người dùng", example = "1", required = true) 
            @PathVariable Long id) {
        MessageResponse response = userService.deleteUser(id);
        return success(response);
    }
}
