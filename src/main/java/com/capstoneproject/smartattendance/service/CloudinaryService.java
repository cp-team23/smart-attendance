package com.capstoneproject.smartattendance.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    // Returns the secure URL of uploaded image
    public String uploadImage(MultipartFile file, String folder) throws IOException {
        Map<?, ?> result = cloudinary.uploader().upload(
            file.getBytes(),
            ObjectUtils.asMap(
                "folder", folder,   // e.g. "smart-attendance/students"
                "resource_type", "image"
            )
        );
        return (String) result.get("secure_url");
    }

    // Pass the public_id to delete (extracted from URL or stored in DB)
    public void deleteImage(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    // URL format: https://res.cloudinary.com/{cloud}/image/upload/v{version}/{folder}/{filename}.jpg
    public String extractPublicId(String imageUrl) {
        // e.g. "smart-attendance/students/abc123"
        String[] parts = imageUrl.split("/upload/");
        String afterUpload = parts[1]; // "v1234567/smart-attendance/students/abc123.jpg"
        // Remove version prefix if present
        if (afterUpload.startsWith("v") && afterUpload.contains("/")) {
            afterUpload = afterUpload.substring(afterUpload.indexOf("/") + 1);
        }
        // Remove extension
        int dotIndex = afterUpload.lastIndexOf(".");
        return dotIndex != -1 ? afterUpload.substring(0, dotIndex) : afterUpload;
    }
}