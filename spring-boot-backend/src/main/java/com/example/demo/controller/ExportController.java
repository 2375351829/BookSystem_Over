package com.example.demo.controller;

import com.example.demo.model.ExportTask;
import com.example.demo.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    @Autowired
    private ExportService exportService;

    @GetMapping("/books")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<byte[]> exportBooks(@RequestParam(defaultValue = "excel") String format) {
        try {
            String fileName = exportService.generateFileName(ExportTask.TYPE_BOOK_EXPORT);
            byte[] content;
            String contentType;
            String extension;

            if ("csv".equalsIgnoreCase(format)) {
                content = exportService.exportToCSV(ExportTask.TYPE_BOOK_EXPORT);
                contentType = "text/csv";
                extension = ".csv";
            } else if ("pdf".equalsIgnoreCase(format)) {
                content = exportService.exportBooksToPDF();
                contentType = "application/pdf";
                extension = ".pdf";
            } else {
                content = exportService.exportBooksToExcel();
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                extension = ".xlsx";
            }

            String encodedFileName = URLEncoder.encode(fileName + extension, StandardCharsets.UTF_8)
                    .replace("+", "%20");

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(content);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/borrow-statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<byte[]> exportBorrowStatistics(@RequestParam(defaultValue = "excel") String format) {
        try {
            String fileName = exportService.generateFileName(ExportTask.TYPE_BORROW_STATISTICS);
            byte[] content;
            String contentType;
            String extension;

            if ("csv".equalsIgnoreCase(format)) {
                content = exportService.exportToCSV(ExportTask.TYPE_BORROW_STATISTICS);
                contentType = "text/csv";
                extension = ".csv";
            } else if ("pdf".equalsIgnoreCase(format)) {
                content = exportService.exportBorrowStatisticsToPDF();
                contentType = "application/pdf";
                extension = ".pdf";
            } else {
                content = exportService.exportBorrowStatisticsToExcel();
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                extension = ".xlsx";
            }

            String encodedFileName = URLEncoder.encode(fileName + extension, StandardCharsets.UTF_8)
                    .replace("+", "%20");

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(content);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportUsers(@RequestParam(defaultValue = "excel") String format) {
        try {
            String fileName = exportService.generateFileName(ExportTask.TYPE_USER_STATISTICS);
            byte[] content;
            String contentType;
            String extension;

            if ("csv".equalsIgnoreCase(format)) {
                content = exportService.exportToCSV(ExportTask.TYPE_USER_STATISTICS);
                contentType = "text/csv";
                extension = ".csv";
            } else if ("pdf".equalsIgnoreCase(format)) {
                content = exportService.exportUsersToPDF();
                contentType = "application/pdf";
                extension = ".pdf";
            } else {
                content = exportService.exportUsersToExcel();
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                extension = ".xlsx";
            }

            String encodedFileName = URLEncoder.encode(fileName + extension, StandardCharsets.UTF_8)
                    .replace("+", "%20");

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(content);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/borrow-records")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<byte[]> exportBorrowRecords(
            @RequestParam(defaultValue = "excel") String format,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long bookId,
            @RequestParam(required = false) Integer status) {
        try {
            String fileName = "借阅记录_" + System.currentTimeMillis();
            byte[] content;
            String contentType;
            String extension;

            if ("csv".equalsIgnoreCase(format)) {
                content = exportService.exportBorrowRecordsToCSV(userId, bookId, status);
                contentType = "text/csv";
                extension = ".csv";
            } else if ("pdf".equalsIgnoreCase(format)) {
                content = exportService.exportBorrowRecordsToPDF(userId, bookId, status);
                contentType = "application/pdf";
                extension = ".pdf";
            } else {
                content = exportService.exportBorrowRecordsToExcel(userId, bookId, status);
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                extension = ".xlsx";
            }

            String encodedFileName = URLEncoder.encode(fileName + extension, StandardCharsets.UTF_8)
                    .replace("+", "%20");

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(content);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/tasks")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<List<ExportTask>> getExportTasks(Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        List<ExportTask> tasks;
        if (isAdmin) {
            tasks = exportService.getAllExportTasks();
        } else {
            String username = authentication.getName();
            tasks = exportService.getExportTasksByUserId(getUserIdFromAuthentication(authentication));
        }
        
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/tasks")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<Map<String, Object>> createExportTask(
            @RequestParam String type,
            @RequestParam(defaultValue = "excel") String format,
            Authentication authentication) {
        
        try {
            String fileName = exportService.generateFileName(type) + 
                ("csv".equalsIgnoreCase(format) ? ".csv" : 
                 "pdf".equalsIgnoreCase(format) ? ".pdf" : ".xlsx");
            
            ExportTask task = exportService.createExportTask(
                getUserIdFromAuthentication(authentication),
                type,
                fileName
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("taskId", task.getId());
            response.put("fileName", task.getFileName());
            response.put("status", task.getStatus());
            response.put("message", "导出任务已创建");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "创建导出任务失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        return 1L;
    }
}
