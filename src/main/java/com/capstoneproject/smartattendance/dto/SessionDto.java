package com.capstoneproject.smartattendance.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SessionDto {
    private LocalDate date;
    private String subjectName;
    private double attendancePercentage;
    private String teacherName;
}
