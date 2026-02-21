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
public class StudentAttendanceResponseDto {
    private UUID attendanceId;
    private LocalDate attendanceDate;
    private LocalTime attendanceTime;
    private String subjectName;
    private AttendanceStatus status;
    private String teacherName;
}
