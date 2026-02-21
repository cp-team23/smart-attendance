package com.capstoneproject.smartattendance.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capstoneproject.smartattendance.entity.Attendance;
import com.capstoneproject.smartattendance.entity.Teacher;

import org.springframework.transaction.annotation.Transactional;


@Repository
public interface AttendanceRepo extends JpaRepository<Attendance,UUID> {

    Optional<Attendance> findByAttendanceIdAndTeacher_UserId(UUID attendanceId,String userId);

    List<Attendance> findByTeacher_UserId(String userId);

    @Transactional
    void deleteByTeacher(Teacher teacher);

    List<Attendance> findByTeacher_Admin_UserId(String adminId);

    List<Attendance> findByTeacher_Admin_UserIdOrderByAttendanceDateDescAttendanceTimeDesc(String adminId);


}