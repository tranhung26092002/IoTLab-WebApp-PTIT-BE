#ifndef COMMAND_HANDLER_H
#define COMMAND_HANDLER_H

#include <ArduinoJson.h>

class CommandHandler {
public:
    static void processCommand(const char* message);
    static void handleActivateCommand(JsonDocument& doc);
    static void handleRestartCommand(JsonDocument& doc);
    static void handleDeactivateCommand(JsonDocument& doc);
    static void handleConfigureCommand(JsonDocument& doc);
    static void sendCommandResponse(const char* commandType, bool success, const char* message);
};

#endif 