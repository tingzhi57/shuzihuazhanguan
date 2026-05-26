package com.example.martyrs.controller;

import com.example.martyrs.entity.MartyrBasicInfo;
import com.example.martyrs.service.MartyrService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/martyrs")
public class MartyrController {

    private final MartyrService martyrService;

    public MartyrController(MartyrService martyrService) {
        this.martyrService = martyrService;
    }

    @GetMapping
    public ResponseEntity<Page<MartyrBasicInfo>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(martyrService.getMartyrPage(page, size, keyword));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MartyrBasicInfo> get(@PathVariable Long id) {
        return ResponseEntity.ok(martyrService.getMartyrById(id));
    }

    @PostMapping
    public ResponseEntity<MartyrBasicInfo> create(@RequestBody MartyrBasicInfo martyr) {
        return ResponseEntity.ok(martyrService.saveMartyr(martyr));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MartyrBasicInfo> update(@PathVariable Long id, @RequestBody MartyrBasicInfo martyr) {
        martyr.setId(id);
        return ResponseEntity.ok(martyrService.saveMartyr(martyr));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        martyrService.deleteMartyr(id);
        return ResponseEntity.ok(Map.of("message", "删除成功"));
    }
}
