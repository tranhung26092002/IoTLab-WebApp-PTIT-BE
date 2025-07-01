#include "command_handler.h"

void CommandHandler::processCommand(const char* message) {
    // Xử lý lệnh
}

void CommandHandler::handleActivateCommand(JsonDocument& doc) {
    // Xử lý lệnh ACTIVATE
}

void CommandHandler::handleRestartCommand(JsonDocument& doc) {
    // Xử lý lệnh RESTART
}

void CommandHandler::handleDeactivateCommand(JsonDocument& doc) {
    // Xử lý lệnh DEACTIVATE
}

void CommandHandler::handleConfigureCommand(JsonDocument& doc) {
    // Xử lý lệnh CONFIGURE
}

void CommandHandler::sendCommandResponse(const char* commandType, bool success, const char* message) {
    // Gửi phản hồi lệnh
} 