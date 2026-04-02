package com.capstoneproject.smartattendance.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PageController {

    @GetMapping("/")
    public String landingPage(Authentication authentication) {

        if (authentication == null) {
            return "login";
        }

        List<GrantedAuthority> authorities = new ArrayList<>(authentication.getAuthorities());

        boolean isAdmin = authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        return isAdmin ? "admin-dashboard" : "teacher-dashboard";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/forgotpassword")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboardPage() {
        return "admin-dashboard";
    }

    @GetMapping("/admin/academic")
    public String academicPage() {
        return "academic";
    }

    @GetMapping("/admin/student/academic/change")
    public String studentAcademicChangePage() {
        return "student-academic-change";
    }

    @GetMapping("/admin/user/add")
    public String addUserPage() {
        return "adduser";
    }

    @GetMapping("/admin/user/search")
    public String searchUserPage() {
        return "admin-search-user";
    }

    @GetMapping("/admin/user/update/{role}/{key}")
    public String updateUserPage(
            @PathVariable String role,
            @PathVariable String key,
            Model model) {

        model.addAttribute("key",key);
        model.addAttribute("role", role);
        return "admin-update-user";
    }

    @GetMapping("/admin/user/update")
    public String updateUserPageStatic() {
        return "admin-update-user";
    }

    @GetMapping("/admin/all-students/{academicId}")
    public String allStudentPage(@PathVariable(required = false) String academicId, Model model) {
        try {
            UUID uuid = UUID.fromString(academicId);
            model.addAttribute("academicId", uuid);
        } catch (IllegalArgumentException e) {
            model.addAttribute("academicId", null);
        }

        return "all-students";
    }

    @GetMapping("/admin/all-teachers")
    public String allTeacherPage() {
        return "all-teachers";
    }

    @GetMapping("/admin/attendance/all")
    public String allAttendancePage() {
        return "admin-all-attendance";
    }

    @GetMapping("/admin/profile")
    public String updateAdminPage() {
        return "admin-update-profile";
    }

    @GetMapping("/admin/recycle-bin")
    public String recycleBinPage() {
        return "admin-recyclebin";
    }

    @GetMapping("/admin/attendance/{attendanceId}")
    public String adminAttendancePage(@PathVariable UUID attendanceId,Model model) {
        model.addAttribute("academicId", attendanceId);
        return "attendance";
    }

    @GetMapping("/admin/student/image/requests")
    public String userImageChangePage() {
        return "student-image-req";
    }


    @GetMapping("/teacher/dashboard")
    public String teacherDashboardPage() {
        return "teacher-dashboard";
    }

    @GetMapping("/teacher/profile")
    public String teacherProfilePage() {
        return "teacher-profile";
    }

    @GetMapping("/teacher/attendance/new")
    public String teacherNewAttendancePage() {
        return "teacher-create-attendance";
    }

    @GetMapping("/teacher/all-attendance")
    public String teacherAllAttendancePage() {
        return "teacher-all-attendance";
    }

    @GetMapping("/teacher/recycle-bin")
    public String teacherRecycleBinPage() {
        return "teacher-recyclebin";
    }

    @GetMapping("/teacher/attendance/{attendanceId}")
    public String teacherAttendancePage(@PathVariable UUID attendanceId,Model model) {
        model.addAttribute("academicId",attendanceId);
        return "attendance";
    }

    @GetMapping("/app")
    public String testPage() {
        return "student-app";
    }
    
}

