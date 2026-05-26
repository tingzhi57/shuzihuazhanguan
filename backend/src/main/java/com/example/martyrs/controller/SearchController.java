package com.example.martyrs.controller;

import com.example.martyrs.service.MartyrService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final MartyrService martyrService;

    public SearchController(MartyrService martyrService) {
        this.martyrService = martyrService;
    }

    @GetMapping
    public ResponseEntity<List<?>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(martyrService.searchAll(keyword));
    }
}
