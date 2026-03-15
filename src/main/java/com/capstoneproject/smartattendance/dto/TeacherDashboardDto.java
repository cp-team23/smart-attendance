package com.capstoneproject.smartattendance.dto;

import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeacherDashboardDto {
    private int totalSession;
    private int activeSession;
    private Float avgAttendance;
    private List<SessionDto> sessions;
}
