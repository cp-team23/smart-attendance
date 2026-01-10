package com.capstoneproject.smartattendance.entity;

import java.time.LocalDate;
import java.time.LocalTime;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Attendance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID AttendanceId;

    @Column(nullable = false)
    private LocalDate attendanceDate;

    @Column(nullable = false)
    private LocalTime attendanceTime;

    @Column(nullable = false)
    private String subjectName;

    @Column(nullable = false)
    private String verificationCode;

    @Column(nullable = false)
    private String attendanceKey;

    @ManyToOne(fetch = FetchType.LAZY)
    private Teacher teacher;

    private boolean running;

    @OneToMany(mappedBy = "attendance", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AttendanceRecord> attendanceRecords = new ArrayList<>();

    @OneToMany(mappedBy = "attendance",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<AttendanceAcademic> attendanceAcademics = new ArrayList<>();
}
