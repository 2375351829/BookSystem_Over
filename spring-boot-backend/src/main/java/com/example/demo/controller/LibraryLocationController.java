package com.example.demo.controller;

import com.example.demo.model.LibraryLocation;
import com.example.demo.service.LibraryLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/library-locations")
public class LibraryLocationController {

    @Autowired
    private LibraryLocationService locationService;

    @GetMapping
    public ResponseEntity<?> getAllActiveLocations() {
        List<LibraryLocation> locations = locationService.getAllActiveLocations();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", locations);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/floors")
    public ResponseEntity<?> getAllFloors() {
        List<String> floors = locationService.getAllFloors();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", floors);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/floor/{floor}")
    public ResponseEntity<?> getLocationsByFloor(@PathVariable String floor) {
        List<LibraryLocation> locations = locationService.getLocationsByFloor(floor);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", locations);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/map")
    public ResponseEntity<?> getLocationMap() {
        Map<String, Object> map = locationService.getLocationMap();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", map);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLocationById(@PathVariable Long id) {
        LibraryLocation location = locationService.getLocationById(id);
        if (location == null || location.getDeleted() == 1) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 404);
            response.put("message", "Location not found");
            return ResponseEntity.status(404).body(response);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", location);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<?> findBookLocation(@PathVariable Long bookId) {
        Map<String, Object> location = locationService.findBookLocation(bookId);
        if (location == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 404);
            response.put("message", "Book not found");
            return ResponseEntity.status(404).body(response);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", location);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createLocation(@RequestBody LibraryLocation location) {
        LibraryLocation created = locationService.createLocation(location);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "Location created successfully");
        response.put("data", created);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLocation(@PathVariable Long id, @RequestBody LibraryLocation location) {
        LibraryLocation updated = locationService.updateLocation(id, location);
        if (updated == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 404);
            response.put("message", "Location not found");
            return ResponseEntity.status(404).body(response);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "Location updated successfully");
        response.put("data", updated);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLocation(@PathVariable Long id) {
        boolean deleted = locationService.deleteLocation(id);
        if (!deleted) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 404);
            response.put("message", "Location not found");
            return ResponseEntity.status(404).body(response);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "Location deleted successfully");
        return ResponseEntity.ok(response);
    }
}
