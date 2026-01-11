package com.capstoneproject.smartattendance.repository;


import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capstoneproject.smartattendance.entity.AttendanceRecord;

@Repository
public interface AttendanceRecordRepo extends JpaRepository<AttendanceRecord,Long> {

    boolean existsByAttendance_AttendanceIdAndStudent_UserId(UUID attendanceId, String userId);

    void deleteByAttendance_AttendanceIdAndStudent_Academic_AcademicId(UUID attendanceId, UUID academicId);

    Optional<AttendanceRecord> findByAttendance_AttendanceIdAndStudent_StudentId(UUID attendanceId, String studentId);

    
}
