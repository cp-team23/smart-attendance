package com.capstoneproject.smartattendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.capstoneproject.smartattendance.entity.AttendanceAcademic;

public interface AttendanceAcademicRepo extends JpaRepository<AttendanceAcademic,Long> {
  
} 
