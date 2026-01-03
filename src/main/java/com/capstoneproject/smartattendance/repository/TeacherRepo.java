package com.capstoneproject.smartattendance.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.capstoneproject.smartattendance.entity.Teacher;

public interface TeacherRepo extends JpaRepository<Teacher,String> {

    Optional<Teacher> findByUserIdAndAdmin_UserId(String userId, String adminId);

        
       
} 