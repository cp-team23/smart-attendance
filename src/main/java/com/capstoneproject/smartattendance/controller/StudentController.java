package com.capstoneproject.smartattendance.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.capstoneproject.smartattendance.service.StudentService;
import com.capstoneproject.smartattendance.util.IpUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/student")
public class StudentController {
    @Autowired
    StudentService studentService;

    @GetMapping("/my")
    public ResponseEntity<?> getMyDetails(Authentication authentication){
        String studentId = authentication.getName();
        return studentService.getMyDetailsService(studentId);
    }

    @GetMapping("/allattendance")
    public ResponseEntity<?> getMyAllAttendance(Authentication authentication){
        String studentId = authentication.getName();
        return studentService.getMyAllAttendanceService(studentId);
    }

    @PostMapping("/changemyimage")
    public ResponseEntity<?> changeMyImageReq(@RequestParam("image") MultipartFile image,Authentication authentication) throws IOException{
        String studentId = authentication.getName();
        return studentService.changeMyImageReqService(image,studentId);
    }

    @DeleteMapping("/deletechangeimagerequest")
    public ResponseEntity<?> deleteMyImageReq(Authentication authentication) throws IOException{
        String studentId = authentication.getName();
        return studentService.deleteMyImageReqService(studentId);
    }

    @PostMapping("/scanqrcode")
    public ResponseEntity<?> scanQRCode(@Valid @RequestBody QRDto scaQrDto,Authentication authentication,HttpServletRequest request){
        String studentId = authentication.getName();
        String ipAddress = IpUtil.getClientIp(request);
        return studentService.scanQRCodeService(studentId,scaQrDto,ipAddress);
    }

    
}
