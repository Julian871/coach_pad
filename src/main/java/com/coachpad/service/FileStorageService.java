package com.coachpad.service;

import com.coachpad.exception.ApiException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {

    @Value("${RAILWAY_VOLUME_MOUNT_PATH:/app/data}")
    private String volumePath;

    private Path avatarsDir;

    @PostConstruct
    public void init() {
        avatarsDir = Paths.get(volumePath, "avatars").toAbsolutePath().normalize();
        try {
            Files.createDirectories(avatarsDir);
            log.info("Avatars directory initialized: {}", avatarsDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create avatars directory", e);
        }
    }

    public String saveAvatar(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ApiException("File is empty", HttpStatus.BAD_REQUEST);
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ApiException("Only images allowed", HttpStatus.BAD_REQUEST);
        }

        String ext = extractExtension(file.getOriginalFilename(), contentType);
        String filename = UUID.randomUUID() + ext;

        try (InputStream inputStream = file.getInputStream()) {
            Path target = avatarsDir.resolve(filename);
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
            log.info("Saved avatar: {}", filename);
            return filename;
        } catch (IOException e) {
            throw new ApiException("Failed to save file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Resource loadAvatar(String filename) {
        try {
            Path filePath = avatarsDir.resolve(filename).normalize();
            if (!filePath.startsWith(avatarsDir)) {
                throw new ApiException("Invalid path", HttpStatus.BAD_REQUEST);
            }
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ApiException("File not found", HttpStatus.NOT_FOUND);
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new ApiException("File not found", HttpStatus.NOT_FOUND);
        }
    }

    public void deleteAvatar(String filename) {
        if (filename == null || filename.isBlank()) return;
        try {
            Path filePath = avatarsDir.resolve(filename).normalize();
            if (!filePath.startsWith(avatarsDir)) return;
            Files.deleteIfExists(filePath);
            log.info("Deleted avatar: {}", filename);
        } catch (IOException e) {
            log.warn("Failed to delete avatar: {}", filename, e);
        }
    }

    private String extractExtension(String originalName, String contentType) {
        if (originalName != null && originalName.contains(".")) {
            String ext = originalName.substring(originalName.lastIndexOf('.')).toLowerCase();
            if (ext.length() <= 5 && ext.matches("\\.[a-z]+")) {
                return ext;
            }
        }
        return switch (contentType) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";
            default -> ".jpg";
        };
    }
}
