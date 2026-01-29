package com.capstoneproject.smartattendance.controller;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class PageController {

    @GetMapping({"/","/login"})
    public String loginPage() {
        return "login"; 
    }
    @GetMapping({"/register"})
    public String registerPage() {
        return "register"; 
    }
    @GetMapping({"/admin/dashboard"})
    public String adminDashboardPage() {
        return "admin-dashboard"; 
    }
    @GetMapping({"/forgotpassword"})
    public String forgotPasswordPage() {
        return "forgot-password"; 
    }
    @GetMapping({"/admin/academic"})
    public String academicPage() {
        return "admin-academic"; 
    }
    @GetMapping({"/admin/student/add"})
    public String addStudentPage() {
        return "add-student"; 
    }
    @GetMapping({"/admin/teacher/add"})
    public String addTeacherPage() {
        return "add-teacher"; 
    }
    @GetMapping({"/admin/student/{enrollmentNo}"})
    public String updateStudentPage(@PathVariable String enrollmentNo,Model model) {
        model.addAttribute("enrollmentNo",enrollmentNo);
        return "update-student"; 
    }
    @GetMapping({"/admin/teacher/{teacherId}"})
    public String updateTeacherPage(@PathVariable String teacherId,Model model) {
        model.addAttribute("teacherId",teacherId);
        return "update-teacher"; 
    }
    @GetMapping({"/admin/search-user"})
    public String searchUserPage(@RequestParam String role,Model model) {
        model.addAttribute("role",role);
        return "search-user"; 
    }

    @GetMapping({"/admin/student-image-requests"})
    public String userImageChangePage() {
        return "student-image-request"; 
    }

    @GetMapping({"/admin/all-students/{academicId}"})
    public String allStudentPage(@PathVariable UUID academicId,Model model) {
        model.addAttribute("academicId",academicId);
        return "all-student"; 
    }
    @GetMapping({"/admin/all-students"})
    public String allStudentDirectPage() {
        return "all-student"; 
    }

    @GetMapping({"/admin/all-teachers"})
    public String allTeacherPage() {
        return "all-teacher"; 
    }
    @GetMapping({"/admin/update"})
    public String updateAdminPage() {
        return "update-admin"; 
    }

}


