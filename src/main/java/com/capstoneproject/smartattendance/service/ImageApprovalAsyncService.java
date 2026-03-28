package com.capstoneproject.smartattendance.service;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.capstoneproject.smartattendance.dto.ImageApprovalResult;
import com.capstoneproject.smartattendance.entity.Admin;
import com.capstoneproject.smartattendance.entity.Student;
import com.capstoneproject.smartattendance.repository.StudentRepo;
import com.capstoneproject.smartattendance.service.mail.AdminMailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageApprovalAsyncService {

    private final CloudinaryService cloudinaryService;
    private final FaceMatchService faceMatchService;
    private final StudentRepo studentRepo;
    private final AdminMailService adminMailService;

    @Async
    public void processImageApprovals(Admin admin, List<Student> students) {

        String defaultUrl = "https://res.cloudinary.com/dzyjaax7p/image/upload/v1773846089/defaultimage_kuxomk.jpg";

        List<ImageApprovalResult> results = new CopyOnWriteArrayList<>();

        students.parallelStream().forEach(s -> {
            try {
                byte[] curImageBytes = cloudinaryService.downloadImage(s.getCurImage());
                byte[] newImageBytes = cloudinaryService.downloadImage(s.getNewImage());

                boolean matched = false;
                if (s.getCurImage() == null || s.getCurImage().equals(defaultUrl)) {
                    results.add(new ImageApprovalResult(s.getEnrollmentNo(), "FACE NOT MATCH"));
                }
                else{
                    matched = faceMatchService.matchFaces(curImageBytes, newImageBytes);
                }
                if (matched) {
                    if (s.getCurImage() != null && !s.getCurImage().equals(defaultUrl)) {
                        String publicId = cloudinaryService.extractPublicId(s.getCurImage());
                        cloudinaryService.deleteImage(publicId);
                    }
                    s.setCurImage(s.getNewImage());
                    s.setNewImage(null);
                    studentRepo.save(s);
                    results.add(new ImageApprovalResult(s.getEnrollmentNo(), "APPROVED"));
                } else {
                   
                }

            } catch (Exception e) {
                log.warn("Image approval failed for student {}: {}", s.getUserId(), e.getMessage());
            }
        });

        // ✅ SORT by enrollment number
        List<ImageApprovalResult> sortedResults = results.stream()
                .sorted((a, b) -> a.getEnrollmentNo().compareTo(b.getEnrollmentNo()))
                .toList();

        if (!sortedResults.isEmpty()) {
            adminMailService.sendFullImageApprovalReport(admin, sortedResults);
        }
    }
}
