package com.ptit.service.service;

import com.ptit.service.entity.Command;
import com.ptit.service.repository.CommandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommandService {
    @Autowired
    private CommandRepository commandRepository;

    public Command saveCommand(Command command) {
        return commandRepository.save(command);
    }
}