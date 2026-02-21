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

    private String userId;
    private String name;
    private String email;
    private String collegeName;
    private String enrollmentNo;
    private String year;
    private String branch;
    private String semester;
    private String className;
    private String batch;
    private String curImage;
    private String newImage;
}
