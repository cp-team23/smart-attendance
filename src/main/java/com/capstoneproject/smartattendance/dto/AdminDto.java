package com.capstoneproject.smartattendance.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminDto extends UserDto {

    @NotBlank(message = "ALL_FIELD_REQUIRED")
    private String name;

    @NotBlank(message = "ALL_FIELD_REQUIRED")
    private String email;

    @NotBlank(message = "ALL_FIELD_REQUIRED")
    private String collegeName;

    @NotBlank(message = "ALL_FIELD_REQUIRED")
    private String otp;
    
    @NotBlank(message = "ALL_FIELD_REQUIRED")
    private String confirmPassword;

}
