package com.kinoton.sales.common.exception;

import com.kinoton.sales.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Object handleBusinessException(BusinessException exception, HttpServletRequest request) {
        return selectErrorResponse(request, HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Object handleAccessDeniedException(AccessDeniedException exception, HttpServletRequest request) {
        return selectErrorResponse(request, HttpStatus.FORBIDDEN, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleValidationException(MethodArgumentNotValidException exception, HttpServletRequest request) {
        String message = exception.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .orElse("요청 값이 올바르지 않습니다.");

        return selectErrorResponse(request, HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(BindException.class)
    public Object handleBindException(BindException exception, HttpServletRequest request) {
        String message = exception.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .orElse("요청 값이 올바르지 않습니다.");

        return selectErrorResponse(request, HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Object handleConstraintViolationException(
        ConstraintViolationException exception,
        HttpServletRequest request
    ) {
        return selectErrorResponse(request, HttpStatus.BAD_REQUEST, "요청 값이 올바르지 않습니다.");
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Object handleMaxUploadSizeExceededException(
        MaxUploadSizeExceededException exception,
        HttpServletRequest request
    ) {
        return selectErrorResponse(request, HttpStatus.BAD_REQUEST, "첨부파일은 20MB 이하만 업로드할 수 있습니다.");
    }

    @ExceptionHandler(Exception.class)
    public Object handleException(Exception exception, HttpServletRequest request) {
        return selectErrorResponse(request, HttpStatus.INTERNAL_SERVER_ERROR, "시스템 오류가 발생했습니다.");
    }

    private Object selectErrorResponse(HttpServletRequest request, HttpStatus status, String message) {
        if (isApiRequest(request)) {
            return selectJsonErrorResponse(status, message);
        }

        ModelAndView modelAndView = new ModelAndView("error/modal");
        modelAndView.setStatus(status);
        modelAndView.addObject("statusCode", status.value());
        modelAndView.addObject("title", selectErrorTitle(status));
        modelAndView.addObject("message", message);
        modelAndView.addObject("returnUrl", selectReturnUrl(request));
        return modelAndView;
    }

    private ResponseEntity<ApiResponse<Void>> selectJsonErrorResponse(HttpStatus status, String message) {
        return ResponseEntity
            .status(status)
            .body(ApiResponse.failure(message));
    }

    private boolean isApiRequest(HttpServletRequest request) {
        String requestPath = selectRequestPath(request);
        String requestedWith = request.getHeader("X-Requested-With");
        String accept = request.getHeader(HttpHeaders.ACCEPT);
        return requestPath.startsWith("/api/")
            || "XMLHttpRequest".equalsIgnoreCase(requestedWith)
            || (accept != null && accept.contains("application/json") && !accept.contains("text/html"));
    }

    private String selectRequestPath(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isBlank() && uri.startsWith(contextPath)) {
            return uri.substring(contextPath.length());
        }
        return uri;
    }

    private String selectErrorTitle(HttpStatus status) {
        if (status.is4xxClientError()) {
            return "요청을 처리할 수 없습니다.";
        }
        return "시스템 오류가 발생했습니다.";
    }

    private String selectReturnUrl(HttpServletRequest request) {
        String referer = request.getHeader(HttpHeaders.REFERER);
        if (referer == null || referer.isBlank()) {
            return request.getContextPath().isBlank() ? "/dashboard" : request.getContextPath() + "/dashboard";
        }
        return referer;
    }
}
