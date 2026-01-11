package com.capstoneproject.smartattendance.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.capstoneproject.smartattendance.entity.AttendanceAcademic;

public interface AttendanceAcademicRepo extends JpaRepository<AttendanceAcademic,Long> {

    boolean existsByAttendance_AttendanceIdAndAcademic_AcademicId(UUID attendanceId, UUID academicId);

    void deleteByAttendance_AttendanceIdAndAcademic_AcademicId(UUID attendanceId, UUID academicId);
  
} 
