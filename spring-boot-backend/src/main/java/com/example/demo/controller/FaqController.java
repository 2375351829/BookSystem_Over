package com.example.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.model.Faq;
import com.example.demo.service.FaqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/faq")
public class FaqController {

    @Autowired
    private FaqService faqService;

    @GetMapping
    public ResponseEntity<?> getAllActiveFaqs() {
        List<Faq> faqs = faqService.getAllActiveFaqs();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", faqs);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories")
    public ResponseEntity<?> getAllCategories() {
        List<String> categories = faqService.getAllCategories();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", categories);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> getFaqsByCategory(@PathVariable String category) {
        List<Faq> faqs = faqService.getFaqsByCategory(category);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", faqs);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchFaqs(@RequestParam String keyword) {
        List<Faq> faqs = faqService.searchFaqs(keyword);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", faqs);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFaqById(@PathVariable Long id) {
        Faq faq = faqService.getFaqById(id);
        if (faq == null || faq.getDeleted() == 1) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 404);
            response.put("message", "FAQ not found");
            return ResponseEntity.status(404).body(response);
        }
        
        faqService.incrementViewCount(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", faq);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/page")
    public ResponseEntity<?> getFaqPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        
        IPage<Faq> faqPage = faqService.getFaqPage(page, size, category, keyword);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", faqPage);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createFaq(@RequestBody Faq faq) {
        Faq createdFaq = faqService.createFaq(faq);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "FAQ created successfully");
        response.put("data", createdFaq);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateFaq(@PathVariable Long id, @RequestBody Faq faq) {
        Faq updatedFaq = faqService.updateFaq(id, faq);
        if (updatedFaq == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 404);
            response.put("message", "FAQ not found");
            return ResponseEntity.status(404).body(response);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "FAQ updated successfully");
        response.put("data", updatedFaq);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFaq(@PathVariable Long id) {
        boolean deleted = faqService.deleteFaq(id);
        if (!deleted) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 404);
            response.put("message", "FAQ not found");
            return ResponseEntity.status(404).body(response);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "FAQ deleted successfully");
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/sort")
    public ResponseEntity<?> updateSortOrder(@PathVariable Long id, @RequestParam Integer sortOrder) {
        faqService.updateSortOrder(id, sortOrder);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "Sort order updated successfully");
        return ResponseEntity.ok(response);
    }
}
