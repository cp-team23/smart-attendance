package com.capstoneproject.smartattendance.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceResponseDto {
    
    private UUID AttendanceId;

    private LocalDate attendanceDate;

    private LocalTime attendanceTime;

    private String subjectName;

    private boolean running;
}
