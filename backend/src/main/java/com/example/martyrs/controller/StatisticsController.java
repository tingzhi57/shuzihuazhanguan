package com.example.martyrs.controller;

import com.example.martyrs.dto.StatisticsVO;
import com.example.martyrs.service.MartyrService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final MartyrService martyrService;

    public StatisticsController(MartyrService martyrService) {
        this.martyrService = martyrService;
    }

    @GetMapping
    public ResponseEntity<StatisticsVO> getStatistics() {
        return ResponseEntity.ok(martyrService.getStatistics());
    }
}
