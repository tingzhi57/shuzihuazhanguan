package com.example.martyrs.controller;

import com.example.martyrs.entity.HonorMemorial;
import com.example.martyrs.service.MartyrService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/honors")
public class HonorController {

    private final MartyrService martyrService;

    public HonorController(MartyrService martyrService) {
        this.martyrService = martyrService;
    }

    @GetMapping
    public ResponseEntity<Page<HonorMemorial>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(martyrService.getHonorPage(page, size, keyword));
    }

    @GetMapping("/martyr/{martyrId}")
    public ResponseEntity<List<HonorMemorial>> getByMartyr(@PathVariable Long martyrId) {
        return ResponseEntity.ok(martyrService.getHonorsByMartyrId(martyrId));
    }

    @PostMapping
    public ResponseEntity<HonorMemorial> create(@RequestBody HonorMemorial honor) {
        return ResponseEntity.ok(martyrService.saveHonor(honor));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HonorMemorial> update(@PathVariable Long id, @RequestBody HonorMemorial honor) {
        honor.setId(id);
        return ResponseEntity.ok(martyrService.saveHonor(honor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        martyrService.deleteHonor(id);
        return ResponseEntity.ok(Map.of("message", "删除成功"));
    }
}
