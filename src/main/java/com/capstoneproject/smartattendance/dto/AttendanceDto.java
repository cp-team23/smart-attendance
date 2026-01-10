package com.capstoneproject.smartattendance.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
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
public class AttendanceDto {

    @NotNull(message = "ALL_FIELD_REQUIRED")
    @NotBlank(message = "ALL_FIELD_REQUIRED")
    private List<UUID> academicIds; 

    @NotNull(message = "ALL_FIELD_REQUIRED")
    @NotBlank(message = "ALL_FIELD_REQUIRED")
    private LocalTime attendaceTime;

    @NotNull(message = "ALL_FIELD_REQUIRED")
    @NotBlank(message = "ALL_FIELD_REQUIRED")
    private LocalDate attendanceDate;

    @NotNull(message = "ALL_FIELD_REQUIRED")
    @NotBlank(message = "ALL_FIELD_REQUIRED")
    private String subjectName;
}
