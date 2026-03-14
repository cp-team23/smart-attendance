package com.capstoneproject.smartattendance.controller;

import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capstoneproject.smartattendance.dto.AcademicDto;
import com.capstoneproject.smartattendance.dto.AdminDto;
import com.capstoneproject.smartattendance.dto.AtteandanceResponseDto;
import com.capstoneproject.smartattendance.dto.BasicAttendanceResponseDto;
import com.capstoneproject.smartattendance.dto.BasicDataDto;
import com.capstoneproject.smartattendance.dto.SaveAttendanceDto;
import com.capstoneproject.smartattendance.dto.StudentDto;
import com.capstoneproject.smartattendance.dto.StudentResponseDto;
import com.capstoneproject.smartattendance.dto.TeacherDto;
import com.capstoneproject.smartattendance.service.AdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
     
    private final AdminService adminService;

    @GetMapping("/my")
    public ResponseEntity<?> getMyDetails(Authentication authentication){
        String adminId = authentication.getName();
        BasicDataDto basicDataDto = adminService.getMyDetailsService(adminId);
        return ResponseEntity.ok(Map.of("response",basicDataDto));

    }
    
    @PostMapping("/academic-structure")
    public ResponseEntity<?> createAcademicStructure(@Valid @RequestBody AcademicDto academicDto,Authentication authentication){
        String adminId = authentication.getName();
        adminService.createAcademicDataService(academicDto,adminId);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "CREATED_SUCCESSFULLY"));
    }
    
    @PutMapping("/academic-structure")
    public ResponseEntity<?> updateAcademicStructure(@Valid @RequestBody AcademicDto academicDto,Authentication authentication){
        String adminId = authentication.getName();
        adminService.updateAcademicDataService(academicDto,adminId);
        return ResponseEntity.ok(Map.of("message", "UPDATED_SUCCESSFULLY"));
    }
    @GetMapping("/academic-structure")
    public ResponseEntity<?> getAcademicStructure(Authentication authentication){
        String adminId = authentication.getName();
        List<AcademicDto> response = adminService.getAcademicDataService(adminId);
        return ResponseEntity.ok(Map.of("response", response));
    }
    @DeleteMapping("/academic-structure/{academicId}")
    public ResponseEntity<?> deleteAcademicStructure(@PathVariable UUID academicId,Authentication authentication){
        String adminId = authentication.getName();
        adminService.deleteAcademicDataService(academicId,adminId);
        return ResponseEntity.ok(Map.of("message", "DELETED_SUCCESSFULLY"));

    }

    @PostMapping("/update")
    public ResponseEntity<?> updateAdmin(@Valid @RequestBody AdminDto adminDto,Authentication authentication){
        String adminId = authentication.getName();
        adminService.updateAdminService(adminDto,adminId);
        return ResponseEntity.ok(Map.of("message", "UPDATED_SUCCESSFULLY"));

    }
    
    @PostMapping("/student")
    public ResponseEntity<?> addStudent(@Valid @RequestBody StudentDto studentDto,Authentication authentication){
        String adminId = authentication.getName();
        adminService.addStudentService(studentDto,adminId);  
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "STUDENT_ACCOUNT_CREATED_SUCCESSFULLY"));
    }

    @PutMapping("/student")
    public ResponseEntity<?> updatestudent(@Valid @RequestBody StudentDto studentDto,Authentication authentication){
        String adminId = authentication.getName();
        adminService.updateStudentService(studentDto,adminId);
        return ResponseEntity.ok(Map.of("message", "STUDENT_ACCOUNT_UPDATED_SUCCESSFULLY"));
    }
    @PutMapping("/student/academic")
    public ResponseEntity<?> updatestudentacademic(@RequestBody StudentDto studentDto,Authentication authentication){
        String adminId = authentication.getName();
        adminService.updateStudentAcademicService(studentDto,adminId);
        return ResponseEntity.ok(Map.of("message", "STUDENT_ACCOUNT_UPDATED_SUCCESSFULLY"));
    }

    @DeleteMapping("/student/{userId}")
    public ResponseEntity<?> deleteStudent(@PathVariable String userId,Authentication authentication) throws IOException{
        String adminId = authentication.getName();
        adminService.deleteStudentService(userId,adminId);
        return ResponseEntity.ok(Map.of("message", "STUDENT_ACCOUNT_DELETED_SUCCESSFULLY"));
    }

    @GetMapping("/all-image-change-request")
    public ResponseEntity<?> getAllImageChangeRequest(Authentication authentication){
        String adminId = authentication.getName();
        List<StudentResponseDto> response = adminService.getAllImageChangeRequestService(adminId);
        return ResponseEntity.ok(Map.of("response", response));

    }
    @PatchMapping("/image-change-request/{userId}/approve")
    public ResponseEntity<?> approveImageChangeRequest(Authentication authentication,@PathVariable String userId) throws IOException{
        String adminId = authentication.getName();
        adminService.approveImageChangeRequestService(adminId,userId);
        return ResponseEntity.ok(Map.of("message", "STUDENT_IMAGE_CHANGED_SUCCESSFULLY"));
    }
    @DeleteMapping("/image-change-request/{userId}/reject")
    public ResponseEntity<?> rejectImageChangeRequest(Authentication authentication,@PathVariable String userId) throws IOException{
        String adminId = authentication.getName();
        adminService.rejectImageChangeRequestService(adminId,userId);
        return ResponseEntity.ok(Map.of("message", "STUDENT_IMAGE_REQUEST_DELETED_SUCCESSFULLY"));
    }
    @PostMapping("/teacher")
    public ResponseEntity<?> addTeacher(@Valid @RequestBody TeacherDto teacherDto,Authentication authentication){
        String adminId = authentication.getName();
        adminService.addTeacherService(teacherDto,adminId);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "TEACHER_ACCOUNT_CREATED_SUCCESSFULLY"));
    }

    @PutMapping("/teacher")
    public ResponseEntity<?> updateTeacher(@Valid @RequestBody TeacherDto teacherDto,Authentication authentication){
        String adminId = authentication.getName();
        adminService.updateTeacherService(teacherDto,adminId);
        return ResponseEntity.ok(Map.of("message", "TEACHER_ACCOUNT_UPDATED_SUCCESSFULLY"));
    }

    @DeleteMapping("/teacher/{userId}")
    public ResponseEntity<?> deleteTeacher(@PathVariable String userId,Authentication authentication){
        String adminId = authentication.getName();
        adminService.deleteTeacherService(userId,adminId);
        return ResponseEntity.ok(Map.of("message", "TEACHER_ACCOUNT_DELETED_SUCCESSFULLY"));

    }

    @GetMapping("/student/{enrollmentNo}")
    public ResponseEntity<?> getStudent(@PathVariable String enrollmentNo,Authentication authentication){
        String adminId = authentication.getName();
        StudentResponseDto studentResponseDto =  adminService.getStudentService(enrollmentNo,adminId);
        return ResponseEntity.ok(Map.of("response", studentResponseDto));
    }
    
    @GetMapping("/teacher/{userId}")
    public ResponseEntity<?> getTeacher(@PathVariable String userId,Authentication authentication){
        String adminId = authentication.getName();
        BasicDataDto basicDataDto =  adminService.getTeacherService(userId,adminId);
        return ResponseEntity.ok(Map.of("response", basicDataDto));

    }

    @GetMapping("/all-student/{academicId}")
    public ResponseEntity<?> getAllStudent(@PathVariable UUID academicId,Authentication authentication){
        String adminId = authentication.getName();
        List<StudentResponseDto> response =adminService.getAllStudentService(academicId,adminId);
        return ResponseEntity.ok(Map.of("response", response));
    }
    @GetMapping("/all-teacher")
    public ResponseEntity<?> getAllTeacher(Authentication authentication){
        String adminId = authentication.getName();
        List<BasicDataDto> response =adminService.getAllteacherService(adminId);
        return ResponseEntity.ok(Map.of("response", response));
    }

    @DeleteMapping("/all-attendance")
    public ResponseEntity<?> deleteAllAttendance(Authentication authentication,@RequestBody String otp){
        String adminId = authentication.getName();
        adminService.deleteAllAttendanceService(adminId,otp);
        return ResponseEntity.ok(Map.of("message","ALL_ATTENDANCE_DELETED_SUCCESSFULLY"));
    }

    @GetMapping("/all-attendance")
    public ResponseEntity<?> getAllAttendance(Authentication authentication){
        String adminId = authentication.getName();
        List<BasicAttendanceResponseDto> response = adminService.getAllAttendanceService(adminId);
        return ResponseEntity.ok(Map.of("response",response));
    }

    @GetMapping("/attendance/{attendanceId}")
    public ResponseEntity<?> getAllAttendance(Authentication authentication,@PathVariable UUID attendanceId){
        String adminId = authentication.getName();
        AtteandanceResponseDto response = adminService.getAttendancByAttendanceIdService(attendanceId,adminId);
        return ResponseEntity.ok(Map.of("response",response));
    }

    @PostMapping("/academic-in-attendance/{attendanceId}/{academicId}")
    public ResponseEntity<?> addNewAcademicInAttendance(@PathVariable UUID attendanceId,@PathVariable UUID academicId,Authentication authentication){
        String adminId = authentication.getName();
        adminService.addNewAcademicInAttendanceService(attendanceId,academicId,adminId);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "ACADEMIC_ADDED_SUCCESSFULLY"));

    }

    @DeleteMapping("/academic-in-attendance/{attendanceId}/{academicId}")
    public ResponseEntity<?> removeNewAcademicInAttendance(@PathVariable UUID attendanceId,@PathVariable UUID academicId,Authentication authentication){
        String adminId = authentication.getName();
        adminService.removeAcademicInAttendanceService(attendanceId,academicId,adminId);
        return ResponseEntity.ok(Map.of("message","ACADEMIC_DELETED_SUCCESSFULLY"));

    }

    @DeleteMapping("/attendance/{attendanceId}")
    public ResponseEntity<?> deleteAttendanceById(@PathVariable UUID attendanceId,Authentication authentication){
        String adminId = authentication.getName();
        adminService.deleteAttendanceService(attendanceId,adminId);
        return ResponseEntity.ok(Map.of("message","DELETED_SUCCESSFULLY"));
    }

    @PatchMapping("/student-in-attendance/{attendanceId}/{studentId}/add")
    public ResponseEntity<?> markStudentPresentInAttendance(@PathVariable UUID attendanceId,@PathVariable String studentId,Authentication authentication){
        String adminId = authentication.getName();
        adminService.markStudentPresentInAttendanceService(attendanceId,studentId,adminId);
        return ResponseEntity.ok(Map.of("message","STUDENT_ADDED_SUCCESSFULLY"));
    }

    @PatchMapping("/student-in-attendance/{attendanceId}/{studentId}/remove")
    public ResponseEntity<?> markStudentAbsentInAttendance(@PathVariable UUID attendanceId,@PathVariable String studentId,Authentication authentication){
        String adminId = authentication.getName();
        adminService.markStudentAbsentInAttendanceService(attendanceId,studentId,adminId);
        return ResponseEntity.ok(Map.of("message","STUDENT_REMOVED_SUCCESSFULLY"));

    }


    @GetMapping("/all-deleted-student")
    public ResponseEntity<?> getAllDeletedStudent(Authentication authentication){
        String adminId = authentication.getName();
        List<StudentResponseDto> response = adminService.allDeletedStudentService(adminId);
        return ResponseEntity.ok(Map.of("response",response));

    }
    @GetMapping("/all-deleted-teacher")
    public ResponseEntity<?> getAllDeletedTeacher(Authentication authentication){
        String adminId = authentication.getName();
        List<BasicDataDto> response = adminService.allDeletedTeacherService(adminId);
        return ResponseEntity.ok(Map.of("response",response));

    }
    @GetMapping("/all-deleted-attendance")
    public ResponseEntity<?> getAllDeletedAttendance(Authentication authentication){
        String adminId = authentication.getName();
        List<BasicAttendanceResponseDto> response = adminService.getDeletedAttendance(adminId);
        return ResponseEntity.ok(Map.of("response",response));
    }

    @PatchMapping("/student/restore/{studentId}")
    public ResponseEntity<?> restoreStudent(Authentication authentication,@PathVariable String studentId){
        String adminId = authentication.getName();
        adminService.restoreStudentService(studentId,adminId);
        return ResponseEntity.ok(Map.of("response","RESTORED_SUCCESSFULLY"));
    }

    @PatchMapping("/teacher/restore/{teacherId}")
    public ResponseEntity<?> restoreTeacher(Authentication authentication,@PathVariable String teacherId){
        String adminId = authentication.getName();
        adminService.restoreTeacherService(teacherId,adminId);
        return ResponseEntity.ok(Map.of("response","RESTORED_SUCCESSFULLY"));
    }

    @PatchMapping("/attendance/restore/{attendanceId}")
    public ResponseEntity<?> restoreAttendance(Authentication authentication,@PathVariable UUID attendanceId){
        String adminId = authentication.getName();
        adminService.restoreAttendanceService(attendanceId,adminId);
        return ResponseEntity.ok(Map.of("response","RESTORED_SUCCESSFULLY"));
    }

    @PatchMapping("/attendance/save/{attendanceId}")
    public ResponseEntity<?> updateAttendance(Authentication authentication, @PathVariable UUID attendanceId,@Valid @RequestBody SaveAttendanceDto saveAttendanceDto){
        String adminId = authentication.getName();
        adminService.updateAttendanceService(attendanceId,saveAttendanceDto, adminId);
        return ResponseEntity.ok(Map.of("response", "SAVED_SUCCESSFULLY"));
    }

}
