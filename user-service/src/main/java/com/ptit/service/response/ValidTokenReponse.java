package com.ptit.service.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidTokenReponse {
    private String userId;
    private Map<String, Object> authorities;
    private boolean authenticate;
    private String message;
}
