package com.capstoneproject.smartattendance.controller;

import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capstoneproject.smartattendance.dto.AttendanceDto;
import com.capstoneproject.smartattendance.dto.QRDto;
import com.capstoneproject.smartattendance.service.TeacherService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/teacher")
public class TeacherController {
    
    @Autowired 
   TeacherService teacherService;

    @GetMapping("/my")
    public ResponseEntity<?> getMyDetails(Authentication authentication){
        String taecherId = authentication.getName();
        return teacherService.getMyDetailsService(taecherId);
    }

    @PostMapping("/createattendance")
    public ResponseEntity<?> createAttandance(@Valid @RequestBody AttendanceDto attendanceDto,Authentication authentication){
        String taecherId = authentication.getName();
        return teacherService.createAttendanceService(attendanceDto, taecherId);
    }

    @PatchMapping("/startattendance")
    public ResponseEntity<?> startAttandance(@RequestBody UUID attendanceId,Authentication authentication){
        String taecherId = authentication.getName();
        return teacherService.startAttendanceService(attendanceId, taecherId);
    }
    @PatchMapping("/stopattendance")
    public ResponseEntity<?> stopAttandance(@RequestBody UUID attendanceId,Authentication authentication){
        String taecherId = authentication.getName();
        return teacherService.stopAttendanceService(attendanceId, taecherId);
    }

    @PostMapping("/addnewacademininattendance")
    public ResponseEntity<?> addNewAcademicInAttendance(@RequestBody UUID attendanceId,@RequestBody UUID academicId,Authentication authentication){
        String taecherId = authentication.getName();
        return teacherService.addNewAcademicInAttendanceService(attendanceId,academicId,taecherId);
    }

    @GetMapping("/refreshqrcode")
    public ResponseEntity<?> refreshQRCode(@Valid @RequestBody QRDto qrDto,Authentication authentication){
        String teacherId = authentication.getName();
        return teacherService.refreshQRCodeService(qrDto.getAttendanceId(), teacherId,qrDto.getRefreshTime());
    }

    @DeleteMapping("/removeacademininattendance")
    public ResponseEntity<?> removeNewAcademicInAttendance(@RequestBody UUID attendanceId,@RequestBody UUID academicId,Authentication authentication){
        String taecherId = authentication.getName();
        return teacherService.removeAcademicInAttendanceService(attendanceId,academicId,taecherId);
    }
    @DeleteMapping("/deleteattendance")
    public ResponseEntity<?> removeNewAcademicInAttendance(@RequestBody UUID attendanceId,Authentication authentication){
        String taecherId = authentication.getName();
        return teacherService.deleteAttendanceService(attendanceId,taecherId);
    }
    @GetMapping("/allattendance")
    public ResponseEntity<?> getAllAttendance(Authentication authentication){
        String taecherId = authentication.getName();
        return teacherService.getAllAttendanceService(taecherId);
    }
    @GetMapping("/attendancebyid")
    public ResponseEntity<?> getAttendanceByAttendanceId(@RequestBody UUID attendanceId,Authentication authentication){
        String taecherId = authentication.getName();
        return teacherService.getAttendancByAttendanceIdService(attendanceId,taecherId);
    }
    @GetMapping("/allattendancebysubject")
    public ResponseEntity<?> getAttendanceBySubject(@RequestBody String subjectName,Authentication authentication){
        String taecherId = authentication.getName();
        return teacherService.getAllAttendanceBySubjectNameService(subjectName,taecherId);
    }
    @GetMapping("/allattendancebydate")
    public ResponseEntity<?> getAttendanceByDate(@RequestBody LocalDate attendanceDate,Authentication authentication){
        String taecherId = authentication.getName();
        return teacherService.getAllAttendanceByDateService(attendanceDate,taecherId);
    }
    @GetMapping("/allattendancebydateandsubject")
    public ResponseEntity<?> getAttendanceByDateAndSubject(@RequestBody LocalDate attendanceDate,@RequestBody String subjectName,Authentication authentication){
        String taecherId = authentication.getName();
        return teacherService.getAllttendanceByDateAndSubjectNameService(subjectName,attendanceDate,taecherId);
    }

    @PatchMapping("/addstudentinattendance")
    public ResponseEntity<?> markStudentPresentInAttendance(@RequestBody UUID attendanceId,@RequestBody String studentId,Authentication authentication){
        String taecherId = authentication.getName();
        return teacherService.markStudentPresentInAttendanceService(attendanceId,studentId,taecherId);
    }

    @PatchMapping("/removestudentinattendance")
    public ResponseEntity<?> markStudentAbsentInAttendance(@RequestBody UUID attendanceId,@RequestBody String studentId,Authentication authentication){
        String taecherId = authentication.getName();
        return teacherService.markStudentAbsentInAttendanceService(attendanceId,studentId,taecherId);
    }
}
