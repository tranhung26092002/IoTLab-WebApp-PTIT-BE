package com.ptit.service.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    private String status;
    private String message;
    private Object data;

    public MessageResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }
}
