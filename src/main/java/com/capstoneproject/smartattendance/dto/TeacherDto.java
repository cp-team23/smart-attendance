package com.capstoneproject.smartattendance.dto;

import jakarta.validation.constraints.Email;
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
public class TeacherDto extends UserDto {
   
   @NotNull(message = "ALL_FIELD_REQUIRED")
   @NotBlank(message = "ALL_FIELD_REQUIRED")
   private String name;

   @NotNull(message = "ALL_FIELD_REQUIRED")
   @NotBlank(message = "ALL_FIELD_REQUIRED")
   private String collegeName;

   @NotNull(message = "ALL_FIELD_REQUIRED")
   @Email(message = "ALL_FIELD_REQUIRED")
   @NotBlank(message = "ALL_FIELD_REQUIRED")
   private String email;
}
