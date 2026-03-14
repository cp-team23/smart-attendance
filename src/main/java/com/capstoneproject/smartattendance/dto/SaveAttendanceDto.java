package com.capstoneproject.smartattendance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SaveAttendanceDto {

    @NotNull(message = "ALL_FIELD_REQUIRED")
    private LocalTime attendanceTime;

    @NotNull(message = "ALL_FIELD_REQUIRED")
    private LocalDate attendanceDate;

    @NotBlank(message = "ALL_FIELD_REQUIRED")
    private String subjectName;

    private List<String> presentList;

    private List<String> absentList;

   
}