package com.capstoneproject.smartattendance.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.capstoneproject.smartattendance.entity.Student;

public interface StudentRepo extends JpaRepository<Student,String> {

    Optional<Student> findByUserIdAndAdmin_UserId(String userId, String adminId);
    
}
