package com.capstoneproject.smartattendance.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.multipart.MultipartFile;

import com.capstoneproject.smartattendance.dto.QRDto;
import com.capstoneproject.smartattendance.dto.StudentAttendanceResponseDto;
import com.capstoneproject.smartattendance.dto.StudentResponseDto;
import com.capstoneproject.smartattendance.service.StudentService;
import com.capstoneproject.smartattendance.util.IpUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/my")
    public ResponseEntity<?> getMyDetails(Authentication authentication){
        String studentId = authentication.getName();
        StudentResponseDto response = studentService.getMyDetailsService(studentId);
        return ResponseEntity.ok(Map.of("response", response));

    }

    @GetMapping("/allattendance")
    public ResponseEntity<?> getMyAllAttendance(Authentication authentication){
        String studentId = authentication.getName();
        List<StudentAttendanceResponseDto> response = studentService.getMyAllAttendanceService(studentId);
        return ResponseEntity.ok(Map.of("response", response));

    }

    @PostMapping("/changemyimage")
    public ResponseEntity<?> changeMyImageReq(@RequestParam("image") MultipartFile image,Authentication authentication) throws IOException{
        String studentId = authentication.getName();
        studentService.changeMyImageReqService(image,studentId);
        return ResponseEntity.ok(Map.of("message", "IMAGE_CHANGE_REQUEST_SEND_TO_ADMIN"));

    }

    @DeleteMapping("/deletechangeimagerequest")
    public ResponseEntity<?> deleteMyImageReq(Authentication authentication) throws IOException{
        String studentId = authentication.getName();
        studentService.deleteMyImageReqService(studentId);
        return ResponseEntity.ok(Map.of("message", "IMAGE_CHANGE_REQUEST_DELETED"));

    }

    @PostMapping("/scanqrcode")
    public ResponseEntity<?> scanQRCode(@Valid @RequestBody QRDto scaQrDto,Authentication authentication,HttpServletRequest request){
        String studentId = authentication.getName();
        String ipAddress = IpUtil.getClientIp(request);
        studentService.scanQRCodeService(studentId,scaQrDto,ipAddress);
        return ResponseEntity.ok(Map.of("message", "QR_SCANNED_SUCCESSFULLY"));

    }

    
}
