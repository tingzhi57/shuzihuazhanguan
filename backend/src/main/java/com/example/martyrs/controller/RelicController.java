package com.example.martyrs.controller;

import com.example.martyrs.entity.CulturalRelic;
import com.example.martyrs.service.MartyrService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/relics")
public class RelicController {

    private final MartyrService martyrService;

    public RelicController(MartyrService martyrService) {
        this.martyrService = martyrService;
    }

    @GetMapping
    public ResponseEntity<Page<CulturalRelic>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(martyrService.getRelicPage(page, size, keyword));
    }

    @GetMapping("/martyr/{martyrId}")
    public ResponseEntity<List<CulturalRelic>> getByMartyr(@PathVariable Long martyrId) {
        return ResponseEntity.ok(martyrService.getRelicsByMartyrId(martyrId));
    }

    @PostMapping
    public ResponseEntity<CulturalRelic> create(@RequestBody CulturalRelic relic) {
        return ResponseEntity.ok(martyrService.saveRelic(relic));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CulturalRelic> update(@PathVariable Long id, @RequestBody CulturalRelic relic) {
        relic.setId(id);
        return ResponseEntity.ok(martyrService.saveRelic(relic));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        martyrService.deleteRelic(id);
        return ResponseEntity.ok(Map.of("message", "删除成功"));
    }
}
