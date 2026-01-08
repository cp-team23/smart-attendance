package com.capstoneproject.smartattendance.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminDto extends UserDto {
    
    @NotNull(message = "ALL_FIELD_REQUIRED")
    @NotBlank(message = "ALL_FIELD_REQUIRED")
    private String name;

    @NotNull(message = "ALL_FIELD_REQUIRED")
    @NotBlank(message = "ALL_FIELD_REQUIRED")
    private String email;

    @NotNull(message = "ALL_FIELD_REQUIRED")
    @NotBlank(message = "ALL_FIELD_REQUIRED")
    private String collegeName;

    private String otp;
    
    private String confirmPassword;

    private String newPassword;
    
    private List<AcademicDto> academicDatas;

}
