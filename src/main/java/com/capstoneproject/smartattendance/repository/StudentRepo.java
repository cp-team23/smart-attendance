package com.capstoneproject.smartattendance.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capstoneproject.smartattendance.entity.Admin;
import com.capstoneproject.smartattendance.entity.Student;

@Repository
public interface StudentRepo extends JpaRepository<Student, String> {

    Optional<Student> findByUserIdAndAdmin_UserId(String userId, String adminId);

    Optional<Student> findByUserIdAndIsDeleted(String userId, Boolean isDeleted);

    List<Student> findByAdminUserId(String userId);

    List<Student> findByAcademic_AcademicId(UUID academicId);

    List<Student> findByIsDeletedAndNewImageIsNotNullAndAdmin_UserId(Boolean isDeleted, String userId);

    boolean existsByAdminAndEnrollmentNo(Admin admin, String enrollmentNo);

    Optional<Student> findByAdminAndEnrollmentNoAndIsDeletedFalseAndDeletedDateIsNull(Admin admin,String enrollmentNo);

}
