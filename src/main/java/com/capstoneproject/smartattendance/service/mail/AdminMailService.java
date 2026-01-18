package com.capstoneproject.smartattendance.service.mail;

import org.springframework.stereotype.Service;


import com.capstoneproject.smartattendance.entity.Student;
import com.capstoneproject.smartattendance.entity.Teacher;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminMailService {

    private final MailSenderService mailSenderService;

    public void sendStudentDetailsMail(Student student, String adminId,String type) {

        String subject = "Student Account "+type+" – Smart Attendance System";

        String body =
                "Dear " + student.getName() + ",\n\n" +

                "Your student account has been successfully "+ type +" in the Smart Attendance System.\n\n" +

                "================ ACCOUNT CREDENTIALS ================\n" +
                "User ID   : " + student.getUserId() + "\n" +
                "Password  : " + student.getPassword() + "\n\n" +

                "================ STUDENT DETAILS =====================\n" +
                "Name           : " + student.getName() + "\n" +
                "College        : " + student.getCollegeName() + "\n" +
                "Branch     : " + student.getAcademic().getBranch() + "\n" +
                "Enrollment No  : " + student.getEnrollmentNo() + "\n" +
                "Semester       : " + student.getAcademic().getSemester() + "\n" +
                "Class          : " + student.getAcademic().getClassName() + "\n" +
                "Batch          : " + student.getAcademic().getBatch() + "\n\n" +

                "================ MANAGED BY ==========================\n" +
                type +" By     : " + adminId + "\n\n" +

                "Please log in and change your password after first login.\n\n" +
                "If you did not expect this email, please contact your administrator.\n\n" +

                "Best Regards,\n" +
                "Smart Attendance Team";

        mailSenderService.sendMail(
                student.getEmail(),
                subject,
                body
        );
    }

    public void sendTeacherDetailsMail(Teacher teacher, String adminId, String type) {
       String subject = "Teacher Account "+type+" – Smart Attendance System";

        String body =
                "Dear " + teacher.getName() + ",\n\n" +

                "Your teacher account has been successfully "+ type +" in the Smart Attendance System.\n\n" +

                "================ ACCOUNT CREDENTIALS ================\n" +
                "User ID   : " + teacher.getUserId() + "\n" +
                "Password  : " + teacher.getPassword() + "\n\n" +

                "================ STUDENT DETAILS =====================\n" +
                "Name           : " + teacher.getName() + "\n" +
                "College        : " + teacher.getCollegeName() + "\n" +

                "================ MANAGED BY ==========================\n" +
                type +" By     : " + adminId + "\n\n" +

                "Please log in and change your password after first login.\n\n" +
                "If you did not expect this email, please contact your administrator.\n\n" +

                "Best Regards,\n" +
                "Smart Attendance Team";

        mailSenderService.sendMail(
                teacher.getEmail(),
                subject,
                body
        );
    }
}
