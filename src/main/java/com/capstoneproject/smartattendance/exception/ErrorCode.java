package com.capstoneproject.smartattendance.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    ALL_FIELD_REQUIRED(HttpStatus.BAD_REQUEST),
    BOTH_PASSWORD_SHOULD_BE_SAME(HttpStatus.BAD_REQUEST),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST),
    INVALID_FILE_NAME(HttpStatus.BAD_REQUEST),
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST),

    // AUTH / SECURITY
    WRONG_PASSWORD(HttpStatus.UNAUTHORIZED),
    ROLE_MISMATCH(HttpStatus.FORBIDDEN),
    NOT_ALLOWED(HttpStatus.FORBIDDEN),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND),
    USERID_NOT_AVAILABLE(HttpStatus.BAD_REQUEST),
    EMAIL_NOT_AVAILABLE(HttpStatus.BAD_REQUEST),
    EMAIL_NOT_MATCH(HttpStatus.BAD_REQUEST),

    // OTP
    OTP_INVALID(HttpStatus.BAD_REQUEST),
    OTP_EXPIRED(HttpStatus.BAD_REQUEST),
    OTP_NOT_FOUND(HttpStatus.BAD_REQUEST),

    // MAIL
    MAIL_SEND_FAILED(HttpStatus.BAD_REQUEST),
    MAIL_SERVICE_NOT_AVAILABLE(HttpStatus.INTERNAL_SERVER_ERROR),

    // ACADEMIC
    ACADEMIC_ALREADY_PRESENT(HttpStatus.BAD_REQUEST),
    ACADEMIC_NOT_FOUND(HttpStatus.NOT_FOUND),
    CANT_DELETE_ACADEMIC(HttpStatus.BAD_REQUEST),

    // ATTENDANCE
    ATTENDANCE_RECORD_NOT_FOUND(HttpStatus.NOT_FOUND),

    // FILE / IMAGE
    IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR),
    IMAGE_NOT_FOUND(HttpStatus.BAD_REQUEST),
    FACE_NOT_MATCHED(HttpStatus.BAD_REQUEST),

    // QR
    QR_EXPIRED(HttpStatus.BAD_REQUEST),
    INVALID_QR_DATA(HttpStatus.BAD_REQUEST),
    SCAN_QR_AGAIN(HttpStatus.BAD_REQUEST),


    //GENERAL
    NO_REQUEST_FOUND(HttpStatus.BAD_REQUEST),
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
