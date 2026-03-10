package com.capstoneproject.smartattendance.dto;


import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AcademicDto {
    private UUID academicId;

    @NotNull(message = "ALL_FIELD_REQUIRED")
    @NotBlank(message = "ALL_FIELD_REQUIRED")
    private String year;

    @NotNull(message = "ALL_FIELD_REQUIRED")
    @NotBlank(message = "ALL_FIELD_REQUIRED")
    private String branch;   

    @NotNull(message = "ALL_FIELD_REQUIRED")
    @NotBlank(message = "ALL_FIELD_REQUIRED")
    private String semester; 

    @NotNull(message = "ALL_FIELD_REQUIRED")
    @NotBlank(message = "ALL_FIELD_REQUIRED")
    private String className;

    @NotNull(message = "ALL_FIELD_REQUIRED")
    @NotBlank(message = "ALL_FIELD_REQUIRED")
    private String batch;

    private long studentCount;
    private long deletedStudentstudentCount;
}
