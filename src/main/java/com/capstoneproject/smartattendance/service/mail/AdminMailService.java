package com.capstoneproject.smartattendance.service.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capstoneproject.smartattendance.dto.StudentDto;

@Service
public class AdminMailService {

    @Autowired
    private MailSenderService mailSenderService;

    public void sendStudentAccountCreatedMail(StudentDto studentDto, String adminId) {

        String subject = "Student Account Created – Smart Attendance System";

        String body =
                "Dear " + studentDto.getName() + ",\n\n" +

                "Your student account has been successfully created in the Smart Attendance System.\n\n" +

                "================ ACCOUNT CREDENTIALS ================\n" +
                "User ID   : " + studentDto.getUserId() + "\n" +
                "Password  : " + studentDto.getPassword() + "\n\n" +

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
                "Smart Attendance Team";

        mailSenderService.sendMail(
                studentDto.getEmail(),
                subject,
                body
        );
    }
}
