package com.ptit.service.util;

import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

@Data
public class HttpResponse {
    public HttpStatus status;
    public String body;
    public HttpHeaders headers;

    public HttpResponse(HttpStatus status, String body, HttpHeaders headers) {
        this.status = status;
        this.body = body;
        this.headers = headers;
    }
}
