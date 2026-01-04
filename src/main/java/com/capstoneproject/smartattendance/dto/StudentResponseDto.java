package com.capstoneproject.smartattendance.dto;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentResponseDto{

    private String UserId;
    private String name;
    private String email;
    private String collegeName;
    private String departmentName;
    private String enrollmentNo;
    private String sem;
    private String className;
    private String batchName;
    private float attendance;
}
