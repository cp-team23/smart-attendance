package com.capstoneproject.smartattendance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ImageApprovalResult {
    private String enrollmentNo;
    private String status;
}
