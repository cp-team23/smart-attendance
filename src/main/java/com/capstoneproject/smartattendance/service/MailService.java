package com.capstoneproject.smartattendance.service;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import com.capstoneproject.smartattendance.dto.StudentDto;
import com.capstoneproject.smartattendance.exception.AuthException;
import com.capstoneproject.smartattendance.exception.ErrorCode;

import org.springframework.mail.javamail.JavaMailSender;

@Service
public class MailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    public ResponseEntity<?> sendOtpOnMail(String email, String otp) {
        try {
            if (mailSender == null) {
                throw new AuthException(ErrorCode.MAIL_SERVICE_NOT_AVAILABLE);
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Your One-Time Password (OTP) for Account Verification");
            message.setText(
                    "Dear User,\n\n" +
                            "Your One-Time Password (OTP) for account verification is: " + otp + "\n\n" +
                            "This OTP is valid for the next 2 minute.\n" +
                            "If you did not request this, please ignore this email.\n\n" +
                            "Best regards,\n" +
                            "Smart Attendance Team");

            mailSender.send(message);
            return ResponseEntity.ok(Map.of("message", "OTP_SENT"));

        } catch (Exception e) {
            throw new AuthException(ErrorCode.INTERNAL_ERROR);
        }
    }

    public void sendStudentAccountDetailsMail(StudentDto studentDto,String adminId) {

        if (mailSender == null) {
            throw new AuthException(ErrorCode.MAIL_SERVICE_NOT_AVAILABLE);
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(studentDto.getEmail());
            message.setSubject("Student Account Created – Smart Attendance System");

            message.setText(
                    "Dear " + studentDto.getName() + ",\n\n" +

                    "Your student account has been successfully created in the Smart Attendance System.\n\n" +

                    "================ ACCOUNT CREDENTIALS ================\n" +
                    "User ID   : " + studentDto.getUserId() + "\n" +
                    "Password  : " + studentDto.getPassword()+ "\n\n" +

                    "================ STUDENT DETAILS =====================\n" +
                    "Name           : " + studentDto.getName() + "\n" +
                    "College        : " + studentDto.getCollegeName() + "\n" +
                    "Department     : " + studentDto.getDepartmentName() + "\n" +
                    "Enrollment No  : " + studentDto.getEnrollmentNo() + "\n" +
                    "Semester       : " + studentDto.getSem() + "\n" +
                    "Class          : " + studentDto.getClassName() + "\n" +
                    "Batch          : " + studentDto.getBatchName() + "\n\n" +

                    "================ MANAGED BY ==========================\n" +
                    "Created By     : " + adminId + "\n\n" +

                    "Please log in and change your password after first login.\n\n" +
                    "If you did not expect this email, please contact your administrator.\n\n" +

                    "Best Regards,\n" +
                    "Smart Attendance Team"
            );

            mailSender.send(message);

        } catch (Exception e) {
            throw new AuthException(ErrorCode.MAIL_SEND_FAILED);
        }
    }


}
