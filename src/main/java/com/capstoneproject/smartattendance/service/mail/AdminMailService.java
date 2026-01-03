package com.capstoneproject.smartattendance.service.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capstoneproject.smartattendance.dto.StudentDto;
import com.capstoneproject.smartattendance.dto.TeacherDto;

@Service
public class AdminMailService {

    @Autowired
    private MailSenderService mailSenderService;

    public void sendStudentDetailsMail(StudentDto studentDto, String adminId,String type) {

        String subject = "Student Account "+type+" – Smart Attendance System";

        String body =
                "Dear " + studentDto.getName() + ",\n\n" +

                "Your student account has been successfully "+ type +" in the Smart Attendance System.\n\n" +

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
                type +" By     : " + adminId + "\n\n" +

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

    public void sendTeacherDetailsMail(TeacherDto teacherDto, String adminId, String type) {
       String subject = "Teacher Account "+type+" – Smart Attendance System";

        String body =
                "Dear " + teacherDto.getName() + ",\n\n" +

                "Your teacher account has been successfully "+ type +" in the Smart Attendance System.\n\n" +

                "================ ACCOUNT CREDENTIALS ================\n" +
                "User ID   : " + teacherDto.getUserId() + "\n" +
                "Password  : " + teacherDto.getPassword() + "\n\n" +

                "================ STUDENT DETAILS =====================\n" +
                "Name           : " + teacherDto.getName() + "\n" +
                "College        : " + teacherDto.getCollegeName() + "\n" +

                "================ MANAGED BY ==========================\n" +
                type +" By     : " + adminId + "\n\n" +

                "Please log in and change your password after first login.\n\n" +
                "If you did not expect this email, please contact your administrator.\n\n" +

                "Best Regards,\n" +
                "Smart Attendance Team";

        mailSenderService.sendMail(
                teacherDto.getEmail(),
                subject,
                body
        );
    }
}
