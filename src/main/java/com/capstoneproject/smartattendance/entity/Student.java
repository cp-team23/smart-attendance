package com.capstoneproject.smartattendance.entity;

import java.util.ArrayList;
import java.util.List;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
    columnNames = {"collegeName","enrollmentNo"}
  )
)
public class Student extends User {

   @Column(nullable = false)
   private String name;

   @Column(nullable = false)
   private String collegeName;

   @Column(nullable = false)
   private String enrollmentNo;

   @Column(nullable = false)
   private String email;

   private String curImage;

   private String newImage;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "admin_id", nullable = false)
   private Admin admin;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "academic_id", nullable = false)
   private Academic academic;

   @OneToMany(mappedBy = "student", fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
   private List<AttendanceRecord> attendanceRecords = new ArrayList<>();
}
