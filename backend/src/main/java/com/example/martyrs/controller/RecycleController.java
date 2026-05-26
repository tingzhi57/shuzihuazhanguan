package com.example.martyrs.controller;

import com.example.martyrs.entity.MartyrBasicInfo;
import com.example.martyrs.service.MartyrService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recycle")
public class RecycleController {

    private final MartyrService martyrService;

    public RecycleController(MartyrService martyrService) {
        this.martyrService = martyrService;
    }

    @GetMapping
    public ResponseEntity<List<MartyrBasicInfo>> list() {
        return ResponseEntity.ok(martyrService.getDeletedMartyrs());
    }

    @PutMapping("/restore/{id}")
    public ResponseEntity<?> restore(@PathVariable Long id) {
        martyrService.restoreMartyr(id);
        return ResponseEntity.ok(Map.of("message", "恢复成功"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> permanentlyDelete(@PathVariable Long id) {
        martyrService.permanentlyDelete(id);
        return ResponseEntity.ok(Map.of("message", "已永久删除"));
    }
}
