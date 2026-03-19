package com.capstoneproject.smartattendance.service.mail;

import org.springframework.stereotype.Service;
import com.capstoneproject.smartattendance.entity.Student;
import com.capstoneproject.smartattendance.entity.Teacher;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminMailService {

        private final MailSenderService mailSenderService;

        public void sendStudentDetailsMail(Student student, String adminId, String password, String type) {
                if(password == null){
                    password = "same as before";
                }
                String subject = "Student Account " + type + " – Smart Attendance System";

                String body = """
                                <!DOCTYPE html>
                                <html>

                                <head>
                                    <meta charset="UTF-8" />
                                    <style>
                                        body {
                                            margin: 0;
                                            padding: 0;
                                            background: #e5e7eb;
                                            font-family: Arial, Helvetica, sans-serif;
                                        }

                                        .wrapper {
                                            max-width: 600px;
                                            margin: 30px auto;
                                            background: #ffffff;
                                            border-radius: 8px;
                                            overflow: hidden;
                                            box-shadow: 0 2px 8px rgba(27, 38, 59, 0.12);
                                        }

                                        .header {
                                            background: #1B263B;
                                            padding: 28px 32px;
                                            text-align: center;
                                        }

                                        .header h1 {
                                            margin: 0;
                                            color: #ffffff;
                                            font-size: 22px;
                                            letter-spacing: 0.5px;
                                        }

                                        .header p {
                                            margin: 6px 0 0;
                                            color: #eeeeee;
                                            font-size: 13px;
                                        }

                                        .badge {
                                            display: inline-block;
                                            margin-top: 12px;
                                            background: #16a34a;
                                            color: #ffffff;
                                            font-size: 12px;
                                            font-weight: bold;
                                            padding: 4px 14px;
                                            border-radius: 20px;
                                            letter-spacing: 0.8px;
                                            text-transform: uppercase;
                                        }

                                        .content {
                                            padding: 28px 32px;
                                        }

                                        .greeting {
                                            font-size: 15px;
                                            color: #1B263B;
                                            margin-bottom: 20px;
                                        }

                                        .section {
                                            margin-bottom: 24px;
                                        }

                                        .section-title {
                                            font-size: 11px;
                                            font-weight: bold;
                                            color: #354a75;
                                            text-transform: uppercase;
                                            letter-spacing: 1px;
                                            margin-bottom: 10px;
                                            padding-bottom: 6px;
                                            border-bottom: 2px solid #e5e7eb;
                                        }

                                        .cred-box {
                                            background: #eeeeee;
                                            border-left: 4px solid #1B263B;
                                            border-radius: 4px;
                                            padding: 14px 18px;
                                        }

                                        .cred-row {
                                            display: flex;
                                            justify-content: space-between;
                                            margin-bottom: 8px;
                                            font-size: 14px;
                                        }

                                        .cred-row:last-child {
                                            margin-bottom: 0;
                                        }

                                        .cred-label {
                                            color: #354a75;
                                            font-weight: bold;
                                        }

                                        .cred-value {
                                            color: #1B263B;
                                            font-weight: bold;
                                            font-family: monospace;
                                            font-size: 15px;
                                        }

                                        table.details {
                                            width: 100%%;
                                            border-collapse: collapse;
                                            font-size: 14px;
                                        }

                                        table.details td {
                                            padding: 8px 4px;
                                            color: #1B263B;
                                            border-bottom: 1px solid #e5e7eb;
                                        }

                                        table.details td:first-child {
                                            color: #354a75;
                                            width: 42%%;
                                        }

                                        table.details tr:last-child td {
                                            border-bottom: none;
                                        }

                                        .notice {
                                            background: #eeeeee;
                                            border: 1px solid #e5e7eb;
                                            border-left: 4px solid #f59e0b;
                                            border-radius: 4px;
                                            padding: 12px 16px;
                                            font-size: 13px;
                                            color: #1B263B;
                                            margin-bottom: 24px;
                                        }

                                        .notice strong {
                                            color: #dc2626;
                                        }

                                        .footer {
                                            background: #e5e7eb;
                                            text-align: center;
                                            padding: 18px 32px;
                                            font-size: 12px;
                                            color: #354a75;
                                        }
                                    </style>
                                </head>

                                <body>
                                    <div class="wrapper">
                                        <div class="header">
                                            <h1>Smart Attendance System</h1>
                                            <p>Automated Account Notification</p>
                                            <span class="badge">Account %s</span>
                                        </div>
                                        <div class="content">
                                            <p class="greeting">Dear <strong>%s</strong>,<br><br>
                                                Your student account has been successfully <strong>%s</strong>. Below are your login credentials and
                                                account details.</p>

                                            <div class="section">
                                                <div class="section-title">Account Credentials</div>
                                                <div class="cred-box">
                                                    <div class="cred-row">
                                                        <span class="cred-label">User ID : </span>
                                                        <span class="cred-value">%s</span>
                                                    </div>
                                                    <div class="cred-row">
                                                        <span class="cred-label">Password : </span>
                                                        <span class="cred-value">%s</span>
                                                    </div>
                                                </div>
                                            </div>

                                            <div class="section">
                                                <div class="section-title">Student Details</div>
                                                <table class="details">
                                                    <tr>
                                                        <td>Full Name</td>
                                                        <td><strong>%s</strong></td>
                                                    </tr>
                                                    <tr>
                                                        <td>College</td>
                                                        <td>%s</td>
                                                    </tr>
                                                    <tr>
                                                        <td>Enrollment No</td>
                                                        <td>%s</td>
                                                    </tr>
                                                    <tr>
                                                        <td>Branch</td>
                                                        <td>%s</td>
                                                    </tr>
                                                    <tr>
                                                        <td>Semester</td>
                                                        <td>%s</td>
                                                    </tr>
                                                    <tr>
                                                        <td>Class</td>
                                                        <td>%s</td>
                                                    </tr>
                                                    <tr>
                                                        <td>Batch</td>
                                                        <td>%s</td>
                                                    </tr>
                                                </table>
                                            </div>

                                            <div class="section">
                                                <div class="section-title">Managed By</div>
                                                <table class="details">
                                                    <tr>
                                                        <td>Administrator</td>
                                                        <td><strong>%s</strong></td>
                                                    </tr>
                                                </table>
                                            </div>

                                            <div class="notice">
                                                <strong>⚠ Important:</strong> Please log in and change your password after your first login.
                                                If you did not expect this email, contact your administrator immediately.
                                            </div>
                                        </div>
                                        <div class="footer">
                                            &copy; Smart Attendance System &nbsp;|&nbsp; This is an automated message, please do not reply.
                                        </div>
                                    </div>
                                </body>

                                </html>
                                """
                                .formatted(
                                                type, student.getName(), type,
                                                student.getUserId(), password,
                                                student.getName(), student.getCollegeName(),
                                                student.getEnrollmentNo(),
                                                student.getAcademic().getBranch(),
                                                student.getAcademic().getSemester(),
                                                student.getAcademic().getClassName(),
                                                student.getAcademic().getBatch(),
                                                student.getAdmin().getName());

                mailSenderService.sendMail(student.getEmail(), subject, body);
        }

        public void sendTeacherDetailsMail(Teacher teacher, String adminId, String password, String type) {
                if(password == null){
                    password = "same as before";
                }
                String subject = "Teacher Account " + type + " – Smart Attendance System";

                String body = """
                                <!DOCTYPE html>
                                <html>

                                <head>
                                    <meta charset="UTF-8" />
                                    <style>
                                        body {
                                            margin: 0;
                                            padding: 0;
                                            background: #e5e7eb;
                                            font-family: Arial, Helvetica, sans-serif;
                                        }

                                        .wrapper {
                                            max-width: 600px;
                                            margin: 30px auto;
                                            background: #ffffff;
                                            border-radius: 8px;
                                            overflow: hidden;
                                            box-shadow: 0 2px 8px rgba(27, 38, 59, 0.12);
                                        }

                                        .header {
                                            background: #354a75;
                                            padding: 28px 32px;
                                            text-align: center;
                                        }

                                        .header h1 {
                                            margin: 0;
                                            color: #ffffff;
                                            font-size: 22px;
                                            letter-spacing: 0.5px;
                                        }

                                        .header p {
                                            margin: 6px 0 0;
                                            color: #eeeeee;
                                            font-size: 13px;
                                        }

                                        .badge {
                                            display: inline-block;
                                            margin-top: 12px;
                                            background: #16a34a;
                                            color: #ffffff;
                                            font-size: 12px;
                                            font-weight: bold;
                                            padding: 4px 14px;
                                            border-radius: 20px;
                                            letter-spacing: 0.8px;
                                            text-transform: uppercase;
                                        }

                                        .content {
                                            padding: 28px 32px;
                                        }

                                        .greeting {
                                            font-size: 15px;
                                            color: #1B263B;
                                            margin-bottom: 20px;
                                        }

                                        .section {
                                            margin-bottom: 24px;
                                        }

                                        .section-title {
                                            font-size: 11px;
                                            font-weight: bold;
                                            color: #354a75;
                                            text-transform: uppercase;
                                            letter-spacing: 1px;
                                            margin-bottom: 10px;
                                            padding-bottom: 6px;
                                            border-bottom: 2px solid #e5e7eb;
                                        }

                                        .cred-box {
                                            background: #eeeeee;
                                            border-left: 4px solid #354a75;
                                            border-radius: 4px;
                                            padding: 14px 18px;
                                        }

                                        .cred-row {
                                            display: flex;
                                            justify-content: space-between;
                                            margin-bottom: 8px;
                                            font-size: 14px;
                                        }

                                        .cred-row:last-child {
                                            margin-bottom: 0;
                                        }

                                        .cred-label {
                                            color: #1B263B;
                                            font-weight: bold;
                                        }

                                        .cred-value {
                                            color: #354a75;
                                            font-weight: bold;
                                            font-family: monospace;
                                            font-size: 15px;
                                        }

                                        table.details {
                                            width: 100%%;
                                            border-collapse: collapse;
                                            font-size: 14px;
                                        }

                                        table.details td {
                                            padding: 8px 4px;
                                            color: #1B263B;
                                            border-bottom: 1px solid #e5e7eb;
                                        }

                                        table.details td:first-child {
                                            color: #354a75;
                                            width: 42%%;
                                        }

                                        table.details tr:last-child td {
                                            border-bottom: none;
                                        }

                                        .notice {
                                            background: #eeeeee;
                                            border: 1px solid #e5e7eb;
                                            border-left: 4px solid #f59e0b;
                                            border-radius: 4px;
                                            padding: 12px 16px;
                                            font-size: 13px;
                                            color: #1B263B;
                                            margin-bottom: 24px;
                                        }

                                        .notice strong {
                                            color: #dc2626;
                                        }

                                        .footer {
                                            background: #e5e7eb;
                                            text-align: center;
                                            padding: 18px 32px;
                                            font-size: 12px;
                                            color: #354a75;
                                        }
                                    </style>
                                </head>

                                <body>
                                    <div class="wrapper">
                                        <div class="header">
                                            <h1>Smart Attendance System</h1>
                                            <p>Automated Account Notification</p>
                                            <span class="badge">Account %s</span>
                                        </div>
                                        <div class="content">
                                            <p class="greeting">Dear <strong>%s</strong>,<br><br>
                                                Your teacher account has been successfully <strong>%s</strong>. Below are your login credentials and
                                                account details.</p>

                                            <div class="section">
                                                <div class="section-title">Account Credentials</div>
                                                <div class="cred-box">
                                                    <div class="cred-row">
                                                        <span class="cred-label">User ID : </span>
                                                        <span class="cred-value">%s</span>
                                                    </div>
                                                    <div class="cred-row">
                                                        <span class="cred-label">Password : </span>
                                                        <span class="cred-value">%s</span>
                                                    </div>
                                                </div>
                                            </div>

                                            <div class="section">
                                                <div class="section-title">Teacher Details</div>
                                                <table class="details">
                                                    <tr>
                                                        <td>Full Name</td>
                                                        <td><strong>%s</strong></td>
                                                    </tr>
                                                    <tr>
                                                        <td>College</td>
                                                        <td>%s</td>
                                                    </tr>
                                                </table>
                                            </div>

                                            <div class="section">
                                                <div class="section-title">Managed By</div>
                                                <table class="details">
                                                    <tr>
                                                        <td>Administrator</td>
                                                        <td><strong>%s</strong></td>
                                                    </tr>
                                                </table>
                                            </div>

                                            <div class="notice">
                                                <strong>⚠ Important:</strong> Please log in and change your password after your first login.
                                                If you did not expect this email, contact your administrator immediately.
                                            </div>
                                        </div>
                                        <div class="footer">
                                            &copy; Smart Attendance System &nbsp;|&nbsp; This is an automated message, please do not reply.
                                        </div>
                                    </div>
                                </body>

                                </html>
                                """
                                .formatted(
                                                type, teacher.getName(), type,
                                                teacher.getUserId(), password,
                                                teacher.getName(), teacher.getCollegeName(),
                                                teacher.getAdmin().getName());

                mailSenderService.sendMail(teacher.getEmail(), subject, body);
        }
}