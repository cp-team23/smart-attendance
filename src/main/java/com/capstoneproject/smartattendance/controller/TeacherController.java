package com.capstoneproject.smartattendance.controller;

import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capstoneproject.smartattendance.dto.AcademicDto;
import com.capstoneproject.smartattendance.dto.AtteandanceResponseDto;
import com.capstoneproject.smartattendance.dto.AttendanceDto;
import com.capstoneproject.smartattendance.dto.BasicAttendanceResponseDto;
import com.capstoneproject.smartattendance.dto.BasicDataDto;
import com.capstoneproject.smartattendance.dto.QRDto;
import com.capstoneproject.smartattendance.service.TeacherService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/teacher")
@RequiredArgsConstructor
public class TeacherController {
    
    private final TeacherService teacherService;

    @GetMapping("/my")
    public ResponseEntity<?> getMyDetails(Authentication authentication){
        String taecherId = authentication.getName();
        BasicDataDto basicDataDto = teacherService.getMyDetailsService(taecherId);
        return ResponseEntity.ok(basicDataDto);
    }

    @PostMapping("/createattendance")
    public ResponseEntity<?> createAttandance(@Valid @RequestBody AttendanceDto attendanceDto,Authentication authentication){
        String taecherId = authentication.getName();
        UUID attendaceId = teacherService.createAttendanceService(attendanceDto, taecherId);
        return ResponseEntity.ok(Map.of("attendanceId",attendaceId));
    }

    @PatchMapping("/startattendance")
    public ResponseEntity<?> startAttandance(@RequestBody UUID attendanceId,Authentication authentication){
        String taecherId = authentication.getName();
        teacherService.startAttendanceService(attendanceId, taecherId);
        return ResponseEntity.ok(Map.of("message","ATTENDANCE_STARTED"));
    }
    @PatchMapping("/stopattendance")
    public ResponseEntity<?> stopAttandance(@RequestBody UUID attendanceId,Authentication authentication){
        String taecherId = authentication.getName();
        teacherService.stopAttendanceService(attendanceId, taecherId);
        return ResponseEntity.ok(Map.of("message","ATTENDANCE_STOP_SUCCESSFULLY"));

    }

    @PostMapping("/addnewacademininattendance")
    public ResponseEntity<?> addNewAcademicInAttendance(@RequestBody UUID attendanceId,@RequestBody UUID academicId,Authentication authentication){
        String taecherId = authentication.getName();
        teacherService.addNewAcademicInAttendanceService(attendanceId,academicId,taecherId);
        return ResponseEntity.ok(Map.of("message","ACADEMIC_ADDED_SUCCESSFULLY"));

    }

    @GetMapping("/refreshqrcode")
    public ResponseEntity<?> refreshQRCode(@Valid @RequestBody QRDto qrDto,Authentication authentication){
        String teacherId = authentication.getName();
        QRDto response = teacherService.refreshQRCodeService(qrDto.getAttendanceId(), teacherId,qrDto.getRefreshTime());
        return ResponseEntity.ok(Map.of("response",response));

    }

    @DeleteMapping("/removeacademininattendance")
    public ResponseEntity<?> removeNewAcademicInAttendance(@RequestBody UUID attendanceId,@RequestBody UUID academicId,Authentication authentication){
        String taecherId = authentication.getName();
        teacherService.removeAcademicInAttendanceService(attendanceId,academicId,taecherId);
        return ResponseEntity.ok(Map.of("message","ACADEMIC_DELETED_SUCCESSFULLY"));

    }
    @DeleteMapping("/deleteattendance")
    public ResponseEntity<?> removeNewAcademicInAttendance(@RequestBody UUID attendanceId,Authentication authentication){
        String taecherId = authentication.getName();
        teacherService.deleteAttendanceService(attendanceId,taecherId);
        return ResponseEntity.ok(Map.of("message","ATTENDANCE_DELETE_SUCCESSFULLY"));
    }
    @GetMapping("/allattendance")
    public ResponseEntity<?> getAllAttendance(Authentication authentication){
        String taecherId = authentication.getName();
        List<BasicAttendanceResponseDto> response = teacherService.getAllAttendanceService(taecherId);
        return ResponseEntity.ok(Map.of("response",response));

    }
    @GetMapping("/attendancebyid")
    public ResponseEntity<?> getAttendanceByAttendanceId(@RequestBody UUID attendanceId,Authentication authentication){
        String taecherId = authentication.getName();
        AtteandanceResponseDto response = teacherService.getAttendancByAttendanceIdService(attendanceId,taecherId);
        return ResponseEntity.ok(Map.of("response",response));
    }
    @GetMapping("/allattendancebysubject")
    public ResponseEntity<?> getAttendanceBySubject(@RequestBody String subjectName,Authentication authentication){
        String taecherId = authentication.getName();
        List<BasicAttendanceResponseDto> response = teacherService.getAllAttendanceBySubjectNameService(subjectName,taecherId);
        return ResponseEntity.ok(Map.of("response",response));

    }
    @GetMapping("/allattendancebydate")
    public ResponseEntity<?> getAttendanceByDate(@RequestBody LocalDate attendanceDate,Authentication authentication){
        String taecherId = authentication.getName();
        List<BasicAttendanceResponseDto> response =teacherService.getAllAttendanceByDateService(attendanceDate,taecherId);
        return ResponseEntity.ok(Map.of("response",response));

    }
    @GetMapping("/allattendancebydateandsubject")
    public ResponseEntity<?> getAttendanceByDateAndSubject(@RequestBody LocalDate attendanceDate,@RequestBody String subjectName,Authentication authentication){
        String taecherId = authentication.getName();
        List<BasicAttendanceResponseDto> response = teacherService.getAllttendanceByDateAndSubjectNameService(subjectName,attendanceDate,taecherId);
        return ResponseEntity.ok(Map.of("response",response));

    }

    @PatchMapping("/addstudentinattendance")
    public ResponseEntity<?> markStudentPresentInAttendance(@RequestBody UUID attendanceId,@RequestBody String studentId,Authentication authentication){
        String taecherId = authentication.getName();
        teacherService.markStudentPresentInAttendanceService(attendanceId,studentId,taecherId);
        return ResponseEntity.ok(Map.of("message","STUDENT_ADDED_SUCCESSFULLY"));
    }

    @PatchMapping("/removestudentinattendance")
    public ResponseEntity<?> markStudentAbsentInAttendance(@RequestBody UUID attendanceId,@RequestBody String studentId,Authentication authentication){
        String taecherId = authentication.getName();
        teacherService.markStudentAbsentInAttendanceService(attendanceId,studentId,taecherId);
        return ResponseEntity.ok(Map.of("message","STUDENT_REMOVED_SUCCESSFULLY"));

    }

    @GetMapping("/getacademicstructure")
    public ResponseEntity<?> getAcademicStructure(Authentication authentication){
        String adminId = authentication.getName();
        List<AcademicDto> response = teacherService.getAcademicDataService(adminId);
        return ResponseEntity.ok(Map.of("response", response));
    }
}
