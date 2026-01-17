package com.capstoneproject.smartattendance.entity;

import com.capstoneproject.smartattendance.dto.Role;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.Data;


@Entity
@Data
@Inheritance(strategy = InheritanceType.JOINED)
public class User {

    @Id
    private String userId;

    private String password;

    @Enumerated(EnumType.STRING) 
    private Role role;
    
}
