package com.ptit.service.controller;

import com.ptit.service.response.DataResponse;
import com.ptit.service.response.MessageResponse;
import com.ptit.service.response.PaginationData;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

/**
 * Base controller để thống nhất pattern response cho tất cả các controller
 */
public abstract class BaseController {

    /**
     * Trả về response thành công với data
     */
    protected <T> ResponseEntity<DataResponse<T>> success(T data) {
        return ResponseEntity.ok(DataResponse.success(data));
    }

    /**
     * Trả về response thành công với message
     */
    protected ResponseEntity<DataResponse<MessageResponse>> success(String message) {
        MessageResponse messageResponse = new MessageResponse();
        return ResponseEntity.ok(DataResponse.success(messageResponse));
    }

    /**
     * Trả về response thành công với pagination data từ Page
     */
    protected <T> ResponseEntity<DataResponse<PaginationData<T>>> successWithPagination(Page<T> page) {
        PaginationData<T> paginationData = PaginationData.fromPage(page);
        return ResponseEntity.ok(DataResponse.successWithPagination(paginationData));
    }

    /**
     * Trả về response thành công với pagination data từ Page với mapping
     */
    protected <T, S> ResponseEntity<DataResponse<PaginationData<S>>> successWithPagination(Page<T> page, Class<S> responseClass) {
        PaginationData<S> paginationData = PaginationData.fromPageWithMapping(page, responseClass);
        return ResponseEntity.ok(DataResponse.successWithPagination(paginationData));
    }

    /**
     * Trả về response thành công với pagination data từ PaginationData
     */
    protected <T> ResponseEntity<DataResponse<PaginationData<T>>> successWithPagination(PaginationData<T> paginationData) {
        return ResponseEntity.ok(DataResponse.successWithPagination(paginationData));
    }

    /**
     * Trả về response created với data
     */
    protected <T> ResponseEntity<DataResponse<T>> created(T data) {
        return ResponseEntity.ok(DataResponse.created(data));
    }

    /**
     * Trả về response created với message
     */
    protected ResponseEntity<DataResponse<MessageResponse>> created(String message) {
        MessageResponse messageResponse = new MessageResponse();
        return ResponseEntity.ok(DataResponse.created(messageResponse));
    }

    /**
     * Trả về response no content
     */
    protected <T> ResponseEntity<DataResponse<T>> noContent() {
        return ResponseEntity.ok(DataResponse.noContent());
    }
} 