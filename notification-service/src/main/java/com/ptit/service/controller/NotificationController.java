package com.ptit.service.controller;

import com.ptit.service.response.NotificationResponse;
import com.ptit.service.service.NotificationService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping()
@RequiredArgsConstructor
@Api(tags = "Notification Management", description = "APIs quản lý thông báo và trạng thái đọc")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/{userId}")
    @ApiOperation(value = "Lấy tất cả thông báo của người dùng", notes = "Trả về danh sách thông báo của người dùng theo ID")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Thành công"),
        @ApiResponse(code = 404, message = "Không tìm thấy người dùng")
    })
    public ResponseEntity<List<NotificationResponse>> getAllNotifications(@PathVariable Long userId) {
        List<NotificationResponse> notifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/{notificationId}/read")
    @ApiOperation(value = "Đánh dấu thông báo đã đọc", notes = "Cập nhật trạng thái thông báo thành đã đọc")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Cập nhật thành công"),
        @ApiResponse(code = 404, message = "Không tìm thấy thông báo")
    })
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable Long notificationId) {
        NotificationResponse notification = notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(notification);
    }

    @PutMapping("/{userId}/read-all")
    @ApiOperation(value = "Đánh dấu tất cả thông báo đã đọc", notes = "Cập nhật trạng thái tất cả thông báo của người dùng thành đã đọc")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Cập nhật thành công"),
        @ApiResponse(code = 404, message = "Không tìm thấy người dùng")
    })
    public ResponseEntity<List<NotificationResponse>> markAllAsRead(@PathVariable Long userId) {
        List<NotificationResponse> notifications = notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(notifications);
    }

    @DeleteMapping("/{notificationId}")
    @ApiOperation(value = "Xóa thông báo", notes = "Xóa thông báo theo ID")
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "Xóa thành công"),
        @ApiResponse(code = 404, message = "Không tìm thấy thông báo")
    })
    public ResponseEntity<Void> deleteNotification(@PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}/all")
    @ApiOperation(value = "Xóa tất cả thông báo", notes = "Xóa tất cả thông báo của người dùng")
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "Xóa thành công"),
        @ApiResponse(code = 404, message = "Không tìm thấy người dùng")
    })
    public ResponseEntity<Void> deleteAllNotifications(@PathVariable Long userId) {
        notificationService.deleteAllNotifications(userId);
        return ResponseEntity.noContent().build();
    }
}
