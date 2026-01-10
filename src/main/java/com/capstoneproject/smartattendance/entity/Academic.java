package com.capstoneproject.smartattendance.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Table(
    uniqueConstraints = @UniqueConstraint(
    columnNames = {"year","branch", "semester", "class_name", "batch", "admin_id"}
  )
)
public class Academic {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID academicId;

    @Column(nullable = false)
    private String year;

    @Column(nullable = false)
    private String branch;   

    @Column(nullable = false)
    private String semester;
    
    @Column(nullable = false)
    private String className; 

    @Column(nullable = false)
    private String batch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;

    @OneToMany(mappedBy = "academic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Student> students = new ArrayList<>();

    @OneToMany(mappedBy = "academic")
    private List<AttendanceAcademic> attendanceAcademics = new ArrayList<>();
}
