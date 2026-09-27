package com.coachpad.controller;

import com.coachpad.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/avatars")
@RequiredArgsConstructor
public class AvatarController {

    private final FileStorageService fileStorageService;

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getAvatar(@PathVariable String filename) {
        Resource resource = fileStorageService.loadAvatar(filename);

        String contentType = "image/jpeg";
        if (filename.endsWith(".png")) contentType = "image/png";
        else if (filename.endsWith(".webp")) contentType = "image/webp";
        else if (filename.endsWith(".gif")) contentType = "image/gif";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000")
                .body(resource);
    }
}
