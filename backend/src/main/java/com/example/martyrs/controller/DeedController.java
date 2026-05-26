package com.example.martyrs.controller;

import com.example.martyrs.entity.MartyrDeed;
import com.example.martyrs.service.MartyrService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/deeds")
public class DeedController {

    private final MartyrService martyrService;

    public DeedController(MartyrService martyrService) {
        this.martyrService = martyrService;
    }

    @GetMapping
    public ResponseEntity<Page<MartyrDeed>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(martyrService.getDeedPage(page, size, keyword));
    }

    @GetMapping("/martyr/{martyrId}")
    public ResponseEntity<List<MartyrDeed>> getByMartyr(@PathVariable Long martyrId) {
        return ResponseEntity.ok(martyrService.getDeedsByMartyrId(martyrId));
    }

    @PostMapping
    public ResponseEntity<MartyrDeed> create(@RequestBody MartyrDeed deed) {
        return ResponseEntity.ok(martyrService.saveDeed(deed));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MartyrDeed> update(@PathVariable Long id, @RequestBody MartyrDeed deed) {
        deed.setId(id);
        return ResponseEntity.ok(martyrService.saveDeed(deed));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        martyrService.deleteDeed(id);
        return ResponseEntity.ok(Map.of("message", "删除成功"));
    }
}
