package com.capstoneproject.smartattendance.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentDto extends UserDto {
   
   @NotBlank(message = "ALL_FIELD_REQUIRED")
   private String name;

   @NotBlank(message = "ALL_FIELD_REQUIRED")
   private String collegeName;

   @NotBlank(message = "ALL_FIELD_REQUIRED")
   private String departmentName;

   @NotBlank(message = "ALL_FIELD_REQUIRED")
   private String enrollmentNo;

   @NotBlank(message = "ALL_FIELD_REQUIRED")
   private String sem;

   @Email(message = "ALL_FIELD_REQUIRED")
   @NotBlank(message = "ALL_FIELD_REQUIRED")
   private String email;

   @NotBlank(message = "ALL_FIELD_REQUIRED")
   private String className;

   @NotBlank(message = "ALL_FIELD_REQUIRED")
   private String batchName;

   private float attendance;
}
