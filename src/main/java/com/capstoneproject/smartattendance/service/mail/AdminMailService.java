package com.capstoneproject.smartattendance.service.mail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.annotation.PostConstruct;
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
    private final PdfReportService  pdfReportService;   // ← NEW

    private final Map<String, String> templateCache = new ConcurrentHashMap<>();

    private static final String TPL_STUDENT         = "templates/mail/student-account.html";
    private static final String TPL_TEACHER         = "templates/mail/teacher-account.html";
    private static final String TPL_APPROVAL_REPORT = "templates/mail/image-approval-report.html";
    private static final String TPL_UPLOAD_REPORT   = "templates/mail/bulk-upload-report.html";
    private static final String TPL_IMAGE_DECISION  = "templates/mail/student-image-decision.html";

    @PostConstruct
    public void init() {
        loadAndCache(TPL_STUDENT);
        loadAndCache(TPL_TEACHER);
        loadAndCache(TPL_APPROVAL_REPORT);
        loadAndCache(TPL_UPLOAD_REPORT);
        loadAndCache(TPL_IMAGE_DECISION);
        log.info("Email templates successfully cached.");
    }

    private void loadAndCache(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            templateCache.put(path, content);
        } catch (IOException e) {
            log.error("CRITICAL: Failed to load template {}: {}", path, e.getMessage());
        }
    }

    private String getTemplate(String path) {
        String content = templateCache.get(path);
        if (content == null) {
            log.error("Template cache miss for: {}", path);
            return "";
        }
        return content;
    }

    // ── Unchanged methods ─────────────────────────────────────────────────

    public void sendStudentDetailsMail(Student student, String adminId, String password, String type) {
        if (password == null) password = "same as before";

        String subject = "Student Account " + type + " – Smart Attendance System";
        String body = getTemplate(TPL_STUDENT)
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

    public void sendTeacherDetailsMail(Teacher teacher, String adminId, String password, String type) {
        if (password == null) password = "same as before";

        String subject = "Teacher Account " + type + " – Smart Attendance System";
        String body = getTemplate(TPL_TEACHER)
                .replace("{{type}}", type)
                .replace("{{teacherName}}", teacher.getName())
                .replace("{{userId}}", teacher.getUserId())
                .replace("{{password}}", password)
                .replace("{{collegeName}}", teacher.getCollegeName())
                .replace("{{adminName}}", teacher.getAdmin().getName());

        mailSenderService.sendMail(teacher.getEmail(), subject, body);
    }

    public void sendImageDecisionMail(Student student, boolean approved) {
        String subject = (approved ? "Image Approved" : "Image Rejected") + " – Smart Attendance System";

        String badgeHtml = approved
                ? "<span style=\"background:#16a34a; color:#fff; padding:4px 14px; border-radius:20px;\">Image Approved ✓</span>"
                : "<span style=\"background:#dc2626; color:#fff; padding:4px 14px; border-radius:20px;\">Image Rejected ✗</span>";

        String body = getTemplate(TPL_IMAGE_DECISION)
                .replace("{{studentName}}", student.getName())
                .replace("{{enrollmentNo}}", student.getEnrollmentNo())
                .replace("{{adminName}}", student.getAdmin().getName())
                .replace("{{badgeHtml}}", badgeHtml)
                .replace("{{statusDetail}}", approved ? "Your new image is active." : "Image did not meet criteria.");

        mailSenderService.sendMail(student.getEmail(), subject, body);
    }

    
    public void sendFullImageApprovalReport(Admin admin, List<ImageApprovalResult> results) {
        String subject = "Bulk Image Approval Report – Smart Attendance System";

        long approvedCount = results.stream().filter(r -> "APPROVED".equals(r.getStatus())).count();
        long failedCount   = results.size() - approvedCount;

        // 1. Generate the PDF
        byte[] reportPdf = pdfReportService.generateApprovalReportPdf(admin.getName(), results);

        // 2. Build the slim email body (no table)
        String body = getTemplate(TPL_APPROVAL_REPORT)
                .replace("{{adminName}}",    admin.getName())
                .replace("{{totalCount}}",   String.valueOf(results.size()))
                .replace("{{approvedCount}}", String.valueOf(approvedCount))
                .replace("{{failedCount}}",  String.valueOf(failedCount));

        // 3. Send with PDF attached
        mailSenderService.sendMailWithAttachment(
                admin.getEmail(),
                subject,
                body,
                reportPdf,
                "image-approval-report.pdf",
                "application/pdf"
        );
    }

    /**
     * Sends the bulk image upload report.
     * The file result table is now a branded PDF attachment.
     */
    public void sendMultipleImageUploadReportMail(Admin admin, List<ImageApprovalResult> results) {
        String subject = "Bulk Image Upload Report – Smart Attendance System";

        long successCount = results.stream().filter(r -> "SUCCESS".equals(r.getStatus())).count();
        long failedCount  = results.size() - successCount;

        // 1. Generate the PDF
        byte[] reportPdf = pdfReportService.generateUploadReportPdf(admin.getName(), results);

        // 2. Build the slim email body (no table)
        String body = getTemplate(TPL_UPLOAD_REPORT)
                .replace("{{adminName}}",   admin.getName())
                .replace("{{totalCount}}",  String.valueOf(results.size()))
                .replace("{{successCount}}", String.valueOf(successCount))
                .replace("{{failedCount}}", String.valueOf(failedCount));

        // 3. Send with PDF attached
        mailSenderService.sendMailWithAttachment(
                admin.getEmail(),
                subject,
                body,
                reportPdf,
                "bulk-upload-report.pdf",
                "application/pdf"
        );
    }
}