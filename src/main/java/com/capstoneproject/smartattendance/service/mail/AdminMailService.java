package com.capstoneproject.smartattendance.service.mail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.capstoneproject.smartattendance.dto.ImageApprovalResult;
import com.capstoneproject.smartattendance.entity.Admin;
import com.capstoneproject.smartattendance.entity.Student;
import com.capstoneproject.smartattendance.entity.Teacher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminMailService {

    private final MailSenderService mailSenderService;

    // ── Template paths ─────────────────────────────────────────────────────────
    private static final String TPL_STUDENT = "templates/mail/student-account.html";
    private static final String TPL_TEACHER = "templates/mail/teacher-account.html";
    private static final String TPL_APPROVAL_REPORT = "templates/mail/image-approval-report.html";
    private static final String TPL_UPLOAD_REPORT = "templates/mail/bulk-upload-report.html";
    private static final String TPL_IMAGE_DECISION = "templates/mail/student-image-decision.html";


    // ── Helper: load HTML file from classpath ──────────────────────────────────
    private String loadTemplate(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    // ──────────────────────────────────────────────────────────────────────────
    public void sendStudentDetailsMail(Student student, String adminId, String password, String type) {
        if (password == null)
            password = "same as before";

        String subject = "Student Account " + type + " – Smart Attendance System";

        String body;
        try {
            body = loadTemplate(TPL_STUDENT);
        } catch (IOException e) {
            log.error("Failed to load student-account.html: {}", e.getMessage());
            return;
        }

        body = body
                .replace("{{type}}", type)
                .replace("{{studentName}}", student.getName())
                .replace("{{userId}}", student.getUserId())
                .replace("{{password}}", password)
                .replace("{{collegeName}}", student.getCollegeName())
                .replace("{{enrollmentNo}}", student.getEnrollmentNo())
                .replace("{{branch}}", student.getAcademic().getBranch())
                .replace("{{semester}}", student.getAcademic().getSemester())
                .replace("{{className}}", student.getAcademic().getClassName())
                .replace("{{batch}}", student.getAcademic().getBatch())
                .replace("{{adminName}}", student.getAdmin().getName());

        mailSenderService.sendMail(student.getEmail(), subject, body);
    }

    // ──────────────────────────────────────────────────────────────────────────
    public void sendTeacherDetailsMail(Teacher teacher, String adminId, String password, String type) {
        if (password == null)
            password = "same as before";

        String subject = "Teacher Account " + type + " – Smart Attendance System";

        String body;
        try {
            body = loadTemplate(TPL_TEACHER);
        } catch (IOException e) {
            log.error("Failed to load teacher-account.html: {}", e.getMessage());
            return;
        }

        body = body
                .replace("{{type}}", type)
                .replace("{{teacherName}}", teacher.getName())
                .replace("{{userId}}", teacher.getUserId())
                .replace("{{password}}", password)
                .replace("{{collegeName}}", teacher.getCollegeName())
                .replace("{{adminName}}", teacher.getAdmin().getName());

        mailSenderService.sendMail(teacher.getEmail(), subject, body);
    }

    // ──────────────────────────────────────────────────────────────────────────
    public void sendFullImageApprovalReport(Admin admin, List<ImageApprovalResult> results) {
        String subject = "Bulk Image Approval Report – Smart Attendance System";

        long approvedCount = results.stream().filter(r -> "APPROVED".equals(r.getStatus())).count();
        long failedCount = results.size() - approvedCount;

        StringBuilder rows = new StringBuilder();
        for (int i = 0; i < results.size(); i++) {
            ImageApprovalResult r = results.get(i);
            boolean approved = "APPROVED".equals(r.getStatus());

            String badgeStyle = approved
                    ? "background:#16a34a; color:#fff; padding:3px 10px; border-radius:12px; font-size:12px; font-weight:bold; font-family:Arial,sans-serif;"
                    : "background:#dc2626; color:#fff; padding:3px 10px; border-radius:12px; font-size:12px; font-weight:bold; font-family:Arial,sans-serif;";

            rows.append("""
                    <tr>
                        <td style="padding:8px 4px; color:#1B263B; border-bottom:1px solid #e5e7eb;">%d</td>
                        <td style="padding:8px 4px; color:#1B263B; border-bottom:1px solid #e5e7eb;">%s</td>
                        <td style="padding:8px 4px; border-bottom:1px solid #e5e7eb;"><span style="%s">%s</span></td>
                    </tr>
                    """.formatted(i + 1, r.getEnrollmentNo(), badgeStyle, r.getStatus()));
        }

        String body;
        try {
            body = loadTemplate(TPL_APPROVAL_REPORT);
        } catch (IOException e) {
            log.error("Failed to load image-approval-report.html: {}", e.getMessage());
            return;
        }

        body = body
                .replace("{{adminName}}", admin.getName())
                .replace("{{totalCount}}", String.valueOf(results.size()))
                .replace("{{approvedCount}}", String.valueOf(approvedCount))
                .replace("{{failedCount}}", String.valueOf(failedCount))
                .replace("{{rows}}", rows.toString());

        mailSenderService.sendMail(admin.getEmail(), subject, body);
    }

    // ──────────────────────────────────────────────────────────────────────────
    public void sendMultipleImageUploadReportMail(Admin admin, List<ImageApprovalResult> results) {
        String subject = "Bulk Image Upload Report – Smart Attendance System";

        long successCount = results.stream().filter(r -> "SUCCESS".equals(r.getStatus())).count();
        long failedCount = results.size() - successCount;

        StringBuilder rows = new StringBuilder();
        for (int i = 0; i < results.size(); i++) {
            ImageApprovalResult r = results.get(i);
            boolean success = "SUCCESS".equals(r.getStatus());

            String badgeStyle = success
                    ? "background:#16a34a; color:#fff; padding:3px 10px; border-radius:12px; font-size:12px; font-weight:bold;"
                    : "background:#dc2626; color:#fff; padding:3px 10px; border-radius:12px; font-size:12px; font-weight:bold;";

            // ✅ Resolve detail BEFORE touching anything — r is NEVER mutated
            String detail = switch (r.getStatus()) {
                case "SUCCESS" -> "Image uploaded successfully ✓";
                case "USER NOT FOUND" -> "No student found with this enrollment no";
                case "FILE SIZE EXCEEDED" -> "Image exceeds the 5MB size limit";
                case "INVALID FILE TYPE" -> "Only JPG, JPEG, PNG files are allowed";
                case "INVALID FILE NAME" -> "File name is missing or has no extension";
                case "INTERNAL ERROR" -> "Unexpected error during upload";
                default -> r.getStatus();
            };

            // ✅ Local display label only — never r.setStatus()
            String displayLabel = success ? "SUCCESS" : "ERROR";

            rows.append(
                    """
                            <tr>
                                <td style="padding:8px 4px; color:#1B263B; border-bottom:1px solid #e5e7eb; text-align:center;">%d</td>
                                <td style="padding:8px 4px; color:#1B263B; border-bottom:1px solid #e5e7eb;">%s</td>
                                <td style="padding:8px 4px; border-bottom:1px solid #e5e7eb;"><span style="%s">%s</span></td>
                                <td style="padding:8px 4px; color:#6b7280; font-size:13px; border-bottom:1px solid #e5e7eb;">%s</td>
                            </tr>
                            """
                            .formatted(i + 1, r.getEnrollmentNo(), badgeStyle, displayLabel, detail));
        }

        String body;
        try {
            body = loadTemplate(TPL_UPLOAD_REPORT);
        } catch (IOException e) {
            log.error("Failed to load bulk-upload-report.html: {}", e.getMessage());
            return;
        }

        body = body
                .replace("{{adminName}}", admin.getName())
                .replace("{{totalCount}}", String.valueOf(results.size()))
                .replace("{{successCount}}", String.valueOf(successCount))
                .replace("{{failedCount}}", String.valueOf(failedCount))
                .replace("{{rows}}", rows.toString());

        mailSenderService.sendMail(admin.getEmail(), subject, body);
    }


    public void sendImageDecisionMail(Student student, boolean approved,boolean sync) {
        String subject = (approved ? "Image Approved" : "Image Rejected") + " – Smart Attendance System";

        String body;
        try {
            body = loadTemplate(TPL_IMAGE_DECISION);
        } catch (IOException e) {
            log.error("Failed to load student-image-decision.html: {}", e.getMessage());
            return;
        }

        String badgeHtml = approved
                ? "<span style=\"display:inline-block; margin-top:12px; background:#16a34a; color:#fff; font-size:12px; font-weight:bold; padding:4px 14px; border-radius:20px; letter-spacing:0.8px; text-transform:uppercase;\">Image Approved ✓</span>"
                : "<span style=\"display:inline-block; margin-top:12px; background:#dc2626; color:#fff; font-size:12px; font-weight:bold; padding:4px 14px; border-radius:20px; letter-spacing:0.8px; text-transform:uppercase;\">Image Rejected ✗</span>";

        String statusHtml = approved
                ? "<strong style=\"color:#16a34a;\">approved</strong>"
                : "<strong style=\"color:#dc2626;\">rejected</strong>";

        String noticeHtml = approved
                ? "<div style=\"background:#f0fdf4; border:1px solid #bbf7d0; border-left:4px solid #16a34a; border-radius:4px; padding:12px 16px; font-size:13px; color:#1B263B; margin-bottom:24px;\">This image will now be used for attendance verification.</div>"
                : "<div style=\"background:#fef2f2; border:1px solid #fecaca; border-left:4px solid #dc2626; border-radius:4px; padding:12px 16px; font-size:13px; color:#1B263B; margin-bottom:24px;\">Please contact your administrator or re-upload a valid image.</div>";

        body = body
                .replace("{{studentName}}", student.getName())
                .replace("{{enrollmentNo}}", student.getEnrollmentNo())
                .replace("{{adminName}}", student.getAdmin().getName())
                .replace("{{actionLabel}}", approved ? "Approved" : "Rejected")
                .replace("{{statusDetail}}", approved
                        ? "Your new image is now active on your account."
                        : "The submitted image did not meet the required criteria.")
                .replace("{{badgeHtml}}", badgeHtml)
                .replace("{{statusHtml}}", statusHtml)
                .replace("{{noticeHtml}}", noticeHtml);

        if(sync){
            mailSenderService.sendMailSync(student.getEmail(), subject, body);
        }else{
            mailSenderService.sendMail(student.getEmail(), subject, body);
        }
    }
}