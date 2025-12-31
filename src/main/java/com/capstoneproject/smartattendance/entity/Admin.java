package com.capstoneproject.smartattendance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Admin extends User {
    
    @Column(unique = true, nullable = false)
    private String email;

    private String name;
    private String collegeName;

    @Column(columnDefinition = "json")
    private String academicStructure;
}
