package com.example.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.model.Announcement;
import com.example.demo.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @GetMapping("/active")
    public ResponseEntity<?> getActiveAnnouncements() {
        List<Announcement> announcements = announcementService.getActiveAnnouncements();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", announcements);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<?> getAnnouncementsByType(@PathVariable String type) {
        List<Announcement> announcements = announcementService.getAnnouncementsByType(type);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", announcements);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAnnouncementById(@PathVariable Long id) {
        Announcement announcement = announcementService.getAnnouncementById(id);
        if (announcement == null || announcement.getDeleted() == 1) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 404);
            response.put("message", "Announcement not found");
            return ResponseEntity.status(404).body(response);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", announcement);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getAnnouncementPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer status) {
        
        IPage<Announcement> announcementPage = announcementService.getAnnouncementPage(page, size, type, status);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", announcementPage);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createAnnouncement(@RequestBody Announcement announcement) {
        Announcement created = announcementService.createAnnouncement(announcement);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "Announcement created successfully");
        response.put("data", created);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAnnouncement(@PathVariable Long id, @RequestBody Announcement announcement) {
        Announcement updated = announcementService.updateAnnouncement(id, announcement);
        if (updated == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 404);
            response.put("message", "Announcement not found");
            return ResponseEntity.status(404).body(response);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "Announcement updated successfully");
        response.put("data", updated);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAnnouncement(@PathVariable Long id) {
        boolean deleted = announcementService.deleteAnnouncement(id);
        if (!deleted) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 404);
            response.put("message", "Announcement not found");
            return ResponseEntity.status(404).body(response);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "Announcement deleted successfully");
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        announcementService.updateStatus(id, status);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "Status updated successfully");
        return ResponseEntity.ok(response);
    }
}
