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
public class AdminDashboardDto {
    private int studentCount;
    private int teacherCount;
    private int imageReqCount;
    private int academicCount;

    private List<SessionDto> sessions;
}
