package com.capstoneproject.smartattendance.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capstoneproject.smartattendance.entity.Student;

@Repository
public interface StudentRepo extends JpaRepository<Student,String> {

    Optional<Student> findByUserIdAndAdmin_UserId(String userId, String adminId);

    List<Student> findByAdminUserId(String userId);

    List<Student> findByAcademic_AcademicId(UUID academicId);

    List<Student> findByNewImageIsNotNullAndAdmin_UserId(String userId);

    boolean existsByCollegeNameAndEnrollmentNo(String collegeName, String enrollmentNo);
    
}
