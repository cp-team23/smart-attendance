package com.capstoneproject.smartattendance.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentAttendanceDto {
    private String UserId;
    private String name;
    private String enrollmentNo;
    private String year;
    private String branch;
    private String semester;
    private String className;
    private String batch;
    private float attendance;
    private AttendanceStatus status;
}
