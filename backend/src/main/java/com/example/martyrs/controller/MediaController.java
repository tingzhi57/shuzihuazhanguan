package com.example.martyrs.controller;

import com.example.martyrs.entity.MediaLibrary;
import com.example.martyrs.service.MartyrService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/media")
public class MediaController {

    private final MartyrService martyrService;

    public MediaController(MartyrService martyrService) {
        this.martyrService = martyrService;
    }

    @GetMapping
    public ResponseEntity<Page<MediaLibrary>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(martyrService.getMediaPage(page, size, keyword));
    }

    @GetMapping("/martyr/{martyrId}")
    public ResponseEntity<List<MediaLibrary>> getByMartyr(@PathVariable Long martyrId) {
        return ResponseEntity.ok(martyrService.getMediaByMartyrId(martyrId));
    }

    @PostMapping
    public ResponseEntity<MediaLibrary> create(@RequestBody MediaLibrary media) {
        return ResponseEntity.ok(martyrService.saveMedia(media));
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Long martyrId) {
        String originalName = file.getOriginalFilename();
        String ext = originalName != null && originalName.contains(".") ?
                originalName.substring(originalName.lastIndexOf(".") + 1) : "";

        try {
            String uploadDir = System.getProperty("user.dir") + "/uploads/";
            Path dir = Paths.get(uploadDir);
            Files.createDirectories(dir);

            String uuid = UUID.randomUUID().toString();
            String filename = uuid + (ext.isEmpty() ? "" : "." + ext);
            Path dest = dir.resolve(filename);
            file.transferTo(dest.toFile());

            MediaLibrary media = new MediaLibrary();
            media.setTitle(title != null && !title.isEmpty() ? title : originalName);
            media.setType(type != null ? type : detectType(ext));
            media.setDescription(description);
            media.setMartyrId(martyrId);
            media.setFileSize(file.getSize());
            media.setFilePath("/uploads/" + filename);
            media.setUploadDate(LocalDateTime.now());

            MediaLibrary saved = martyrService.saveMedia(media);
            return ResponseEntity.ok(Map.of("id", saved.getId(), "url", saved.getFilePath()));
        } catch (IOException e) {
            log.error("Upload failed: {} file={} size={}", e.getMessage(), originalName, file.getSize(), e);
            return ResponseEntity.badRequest().body(Map.of("error", "上传失败: " + e.getMessage()));
        }
    }

    private String detectType(String ext) {
        if (ext == null) return "image";
        return switch (ext.toLowerCase()) {
            case "mp4", "avi", "mov", "wmv", "flv", "mkv" -> "video";
            case "mp3", "wav", "wma", "aac", "ogg" -> "audio";
            default -> "image";
        };
    }

    @PutMapping("/{id}")
    public ResponseEntity<MediaLibrary> update(@PathVariable Long id, @RequestBody MediaLibrary media) {
        media.setId(id);
        return ResponseEntity.ok(martyrService.saveMedia(media));
    }

    @PutMapping("/{id}/avatar")
    public ResponseEntity<?> setAvatar(@PathVariable Long id) {
        martyrService.setAvatar(id);
        return ResponseEntity.ok(Map.of("message", "头像设置成功"));
    }

    @DeleteMapping("/{id}/avatar")
    public ResponseEntity<?> clearAvatar(@PathVariable Long id) {
        martyrService.clearAvatar(id);
        return ResponseEntity.ok(Map.of("message", "头像已取消"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        martyrService.deleteMedia(id);
        return ResponseEntity.ok(Map.of("message", "删除成功"));
    }
}
