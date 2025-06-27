package com.ptit.service.controller;

import com.ptit.service.entity.Command;
import com.ptit.service.service.CommandService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/commands")
@Api(tags = "Command Management", description = "APIs gửi lệnh điều khiển thiết bị")
public class CommandController {

    @Autowired
    private CommandService commandService;

    @PostMapping
    @ApiOperation(value = "Lưu lệnh điều khiển", notes = "Lưu lệnh điều khiển thiết bị IoT")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Lưu thành công"),
        @ApiResponse(code = 400, message = "Dữ liệu không hợp lệ"),
        @ApiResponse(code = 401, message = "Chưa xác thực"),
        @ApiResponse(code = 403, message = "Không có quyền gửi lệnh")
    })
    public ResponseEntity<Command> saveCommand(@RequestBody Command command) {
        return ResponseEntity.ok(commandService.saveCommand(command));
    }
}