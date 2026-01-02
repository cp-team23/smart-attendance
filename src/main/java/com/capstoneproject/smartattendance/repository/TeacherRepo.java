package com.capstoneproject.smartattendance.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.capstoneproject.smartattendance.entity.Teacher;

public interface TeacherRepo extends JpaRepository<Teacher,String> {

        
       
} 