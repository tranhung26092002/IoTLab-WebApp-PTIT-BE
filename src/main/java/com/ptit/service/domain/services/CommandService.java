package com.ptit.service.domain.services;

import com.ptit.service.domain.entities.Command;
import com.ptit.service.domain.repositories.CommandRepository;
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