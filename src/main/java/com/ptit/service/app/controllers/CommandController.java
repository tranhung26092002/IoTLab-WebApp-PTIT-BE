package com.ptit.service.app.controllers;

import com.ptit.service.domain.entities.Command;
import com.ptit.service.domain.services.CommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/commands")
public class CommandController {

    @Autowired
    private CommandService commandService;

    @PostMapping
    public ResponseEntity<Command> saveCommand(@RequestBody Command command) {
        return ResponseEntity.ok(commandService.saveCommand(command));
    }
}