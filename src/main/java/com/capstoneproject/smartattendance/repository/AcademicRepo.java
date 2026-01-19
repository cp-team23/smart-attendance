package com.capstoneproject.smartattendance.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capstoneproject.smartattendance.entity.Academic;

import jakarta.transaction.Transactional;

@Repository
public interface AcademicRepo extends JpaRepository<Academic,UUID> {

    @Transactional
    void deleteByAdminUserId(String userId);
    List<Academic> findByAdminUserId(String userId);
}
