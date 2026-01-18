package com.capstoneproject.smartattendance.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    ALL_FIELD_REQUIRED(HttpStatus.BAD_REQUEST),
    BOTH_PASSWORD_SHOULD_BE_SAME(HttpStatus.BAD_REQUEST),

    USERID_NOT_AVAILABLE(HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND),

    EMAIL_NOT_AVAILABLE(HttpStatus.BAD_REQUEST),
    EMAIL_NOT_MATCH(HttpStatus.BAD_REQUEST),
    MAIL_SEND_FAILED(HttpStatus.BAD_REQUEST),
    MAIL_SERVICE_NOT_AVAILABLE(HttpStatus.INTERNAL_SERVER_ERROR),
    
    WRONG_PASSWORD(HttpStatus.BAD_REQUEST),
    ROLE_MISMATCH(HttpStatus.BAD_REQUEST),
    NOT_ALLOWED(HttpStatus.BAD_REQUEST),

    OTP_INVALID(HttpStatus.BAD_REQUEST),
    OTP_EXPIRED(HttpStatus.BAD_REQUEST),
    NO_OTP_RECORD(HttpStatus.BAD_REQUEST),
    INVALID_OTP(HttpStatus.BAD_REQUEST),

    SAME_STRUCTURE_EXIST_ALREADY(HttpStatus.BAD_REQUEST),
    ACADEMIC_DETAILS_NOT_FOUND(HttpStatus.BAD_REQUEST),
    CANT_DELETE_THIS(HttpStatus.BAD_REQUEST),

    ATTENDANCE_RECORD_NOT_FOUND(HttpStatus.BAD_REQUEST),
    ACADEMIC_ALREADY_PRESENT(HttpStatus.BAD_REQUEST),
    WRONG_ACADEMIC(HttpStatus.BAD_REQUEST),
    ACADEMIC_NOT_PRESENT(HttpStatus.BAD_REQUEST),

    IMAGE_UPLOADS_FAIL(HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST),
    INVALID_FILE_NAME(HttpStatus.BAD_REQUEST),
    NO_REQUEST_FOUND(HttpStatus.BAD_REQUEST),

    QR_EXPIRED(HttpStatus.BAD_REQUEST),
    WRONG_DATA(HttpStatus.BAD_REQUEST),

    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR);
    private final HttpStatus status;

    ErrorCode(HttpStatus status) {
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return this.name();
    }
}
