package com.capstoneproject.smartattendance.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capstoneproject.smartattendance.entity.Attendance;

@Repository
public interface AttendanceRepo extends JpaRepository<Attendance,UUID> {

    Optional<Attendance> findByAttendanceIdAndTeacher_UserId(UUID attendanceId,String userId);

    List<Attendance> findByTeacher_UserId(String userId);

}