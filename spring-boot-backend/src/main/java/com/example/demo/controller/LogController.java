package com.example.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.model.OperationLog;
import com.example.demo.service.LogService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    @Autowired
    private LogService logService;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String operator,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        IPage<OperationLog> logs = logService.getLogs(page, pageSize, operator, operationType, module, startDate, endDate);
        
        List<Map<String, Object>> enrichedRecords = logs.getRecords().stream()
                .map(this::convertToEnrichedMap)
                .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("records", enrichedRecords);
        response.put("total", logs.getTotal());
        response.put("current", logs.getCurrent());
        response.put("size", logs.getSize());
        response.put("pages", logs.getPages());
        
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getLogById(@PathVariable Long id) {
        OperationLog log = logService.getLogById(id);
        if (log == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertToEnrichedMap(log));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/export")
    public void exportLogs(
            @RequestParam(required = false) String operator,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpServletResponse response) throws IOException {
        
        List<OperationLog> logs = logService.getAllLogs(operator, operationType, module, startDate, endDate);
        
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String fileName = URLEncoder.encode("操作日志_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date()), StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("操作日志");
            
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setAlignment(HorizontalAlignment.CENTER);
            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            
            String[] headers = {"ID", "操作人", "操作类型", "模块", "操作描述", "IP地址", "执行结果", "操作时间", "耗时(ms)"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 20 * 256);
            }
            
            int rowNum = 1;
            for (OperationLog log : logs) {
                Row row = sheet.createRow(rowNum++);
                
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(log.getId() != null ? log.getId().toString() : "");
                cell0.setCellStyle(dataStyle);
                
                Cell cell1 = row.createCell(1);
                cell1.setCellValue(log.getUsername() != null ? log.getUsername() : "");
                cell1.setCellStyle(dataStyle);
                
                Cell cell2 = row.createCell(2);
                cell2.setCellValue(getOperationLabel(log.getOperation()));
                cell2.setCellStyle(dataStyle);
                
                Cell cell3 = row.createCell(3);
                cell3.setCellValue(getModuleLabel(log.getModule()));
                cell3.setCellStyle(dataStyle);
                
                Cell cell4 = row.createCell(4);
                cell4.setCellValue(generateDescription(log));
                cell4.setCellStyle(dataStyle);
                
                Cell cell5 = row.createCell(5);
                cell5.setCellValue(log.getIpAddress() != null ? log.getIpAddress() : "");
                cell5.setCellStyle(dataStyle);
                
                Cell cell6 = row.createCell(6);
                cell6.setCellValue(log.getStatus() != null && log.getStatus() == 1 ? "成功" : "失败");
                cell6.setCellStyle(dataStyle);
                
                Cell cell7 = row.createCell(7);
                cell7.setCellValue(log.getCreateTime() != null ? DATE_FORMAT.format(log.getCreateTime()) : "");
                cell7.setCellStyle(dataStyle);
                
                Cell cell8 = row.createCell(8);
                cell8.setCellValue(log.getExecuteTime() != null ? log.getExecuteTime().toString() : "");
                cell8.setCellStyle(dataStyle);
            }
            
            workbook.write(response.getOutputStream());
        }
    }
    
    private String getOperationLabel(String operation) {
        if (operation == null) return "";
        switch (operation) {
            case "create": return "新增";
            case "delete": return "删除";
            case "update": return "修改";
            case "read": return "查询";
            case "login": return "登录";
            case "logout": return "登出";
            case "system": return "系统设置";
            default: return operation;
        }
    }
    
    private String getModuleLabel(String module) {
        if (module == null) return "";
        switch (module) {
            case "user": return "用户管理";
            case "book": return "图书管理";
            case "borrow": return "借阅管理";
            case "category": return "分类管理";
            case "config": return "系统配置";
            case "seat": return "座位管理";
            case "inquiry": return "咨询管理";
            case "acquisition": return "图书采编";
            case "statistics": return "统计报表";
            default: return module;
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping
    public ResponseEntity<Map<String, Object>> clearLogs() {
        Map<String, Object> response = new HashMap<>();
        try {
            int deletedCount = logService.clearLogs();
            response.put("success", true);
            response.put("message", "日志清空成功");
            response.put("deletedCount", deletedCount);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "日志清空失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    private Map<String, Object> convertToEnrichedMap(OperationLog log) {
        Map<String, Object> map = new HashMap<>();
        
        map.put("id", log.getId());
        map.put("userId", log.getUserId());
        map.put("username", log.getUsername());
        map.put("operation", log.getOperation());
        map.put("module", log.getModule());
        map.put("method", log.getMethod());
        map.put("status", log.getStatus());
        
        map.put("createdAt", log.getCreateTime() != null ? DATE_FORMAT.format(log.getCreateTime()) : null);
        map.put("duration", log.getExecuteTime());
        map.put("requestData", log.getParams());
        map.put("responseData", log.getResult());
        map.put("errorMessage", log.getErrorMessage());
        map.put("ipAddress", log.getIpAddress());
        map.put("userAgent", log.getUserAgent());
        
        map.put("description", generateDescription(log));
        
        return map;
    }

    private String generateDescription(OperationLog log) {
        if (log == null) {
            return "";
        }
        
        StringBuilder desc = new StringBuilder();
        
        if (log.getModule() != null && !log.getModule().isEmpty()) {
            desc.append("[").append(log.getModule()).append("] ");
        }
        
        if (log.getOperation() != null && !log.getOperation().isEmpty()) {
            desc.append(log.getOperation());
        }
        
        if (log.getUsername() != null && !log.getUsername().isEmpty()) {
            desc.append(" - 操作人: ").append(log.getUsername());
        }
        
        return desc.toString();
    }
}