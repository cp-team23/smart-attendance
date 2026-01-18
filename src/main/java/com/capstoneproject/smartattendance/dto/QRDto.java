package com.capstoneproject.smartattendance.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QRDto {

    @NotNull(message = "ALL_FIELD_REQUIRED")
    @NotBlank(message = "ALL_FIELD_REQUIRED")
    private UUID attendanceId; 

    @NotNull(message = "ALL_FIELD_REQUIRED")
    @NotBlank(message = "ALL_FIELD_REQUIRED")
    private String encryptedCode;

    @NotNull(message = "ALL_FIELD_REQUIRED")
    @NotBlank(message = "ALL_FIELD_REQUIRED")
    private Long expireTime;

    @NotNull(message = "ALL_FIELD_REQUIRED")
    @NotBlank(message = "ALL_FIELD_REQUIRED")
    private int refreshTime;
}