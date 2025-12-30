package com.capstoneproject.smartattendance.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @NotBlank(message = "ALL_FIELD_REQUIRED")
    private String UserId;

    @NotBlank(message = "ALL_FIELD_REQUIRED")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

}
