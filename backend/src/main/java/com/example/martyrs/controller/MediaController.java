package com.example.martyrs.controller;

import com.example.martyrs.entity.MediaLibrary;
import com.example.martyrs.repository.MediaLibraryRepository;
import com.example.martyrs.service.MartyrService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/media")
public class MediaController {

    private final MartyrService martyrService;
    private final MediaLibraryRepository mediaRepository;

    public MediaController(MartyrService martyrService, MediaLibraryRepository mediaRepository) {
        this.martyrService = martyrService;
        this.mediaRepository = mediaRepository;
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
            MediaLibrary media = new MediaLibrary();
            media.setTitle(title != null && !title.isEmpty() ? title : originalName);
            media.setType(type != null ? type : detectType(ext));
            media.setDescription(description);
            media.setMartyrId(martyrId);
            media.setFileSize(file.getSize());
            media.setFileData(file.getBytes());
            media.setUploadDate(LocalDateTime.now());

            MediaLibrary saved = martyrService.saveMedia(media);
            String fileUrl = "/api/media/file/" + saved.getId();
            saved.setFilePath(fileUrl);
            martyrService.saveMedia(saved);
            return ResponseEntity.ok(Map.of("id", saved.getId(), "url", fileUrl));
        } catch (IOException e) {
            log.error("Upload failed: {} file={} size={}", e.getMessage(), originalName, file.getSize(), e);
            return ResponseEntity.badRequest().body(Map.of("error", "上传失败: " + e.getMessage()));
        }
    }

    @GetMapping("file/{id}")
    public ResponseEntity<byte[]> download(@PathVariable Long id) {
        MediaLibrary media = mediaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文件不存在"));
        if (media.getFileData() == null) {
            return ResponseEntity.notFound().build();
        }
        String title = media.getTitle();
        String ext = title != null && title.contains(".") ?
                title.substring(title.lastIndexOf(".") + 1) : "";
        MediaType mediaType = getMediaType(ext);
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + title + "\"")
                .body(media.getFileData());
    }

    private String detectType(String ext) {
        if (ext == null) return "image";
        return switch (ext.toLowerCase()) {
            case "mp4", "avi", "mov", "wmv", "flv", "mkv" -> "video";
            case "mp3", "wav", "wma", "aac", "ogg" -> "audio";
            default -> "image";
        };
    }

    private MediaType getMediaType(String format) {
        if (format == null) return MediaType.APPLICATION_OCTET_STREAM;
        return switch (format.toLowerCase()) {
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            case "png" -> MediaType.IMAGE_PNG;
            case "gif" -> MediaType.IMAGE_GIF;
            case "webp" -> MediaType.valueOf("image/webp");
            case "mp4" -> MediaType.valueOf("video/mp4");
            case "avi" -> MediaType.valueOf("video/x-msvideo");
            case "mov" -> MediaType.valueOf("video/quicktime");
            case "mp3" -> MediaType.valueOf("audio/mpeg");
            case "wav" -> MediaType.valueOf("audio/wav");
            case "ogg" -> MediaType.valueOf("audio/ogg");
            default -> MediaType.APPLICATION_OCTET_STREAM;
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
