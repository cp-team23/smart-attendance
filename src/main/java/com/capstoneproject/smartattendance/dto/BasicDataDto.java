package com.capstoneproject.smartattendance.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BasicDataDto{

    private String UserId;
    private String name;
    private String email;
    private String collegeName;
}
