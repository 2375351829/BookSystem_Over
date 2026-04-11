package com.example.demo.controller;

import com.example.demo.model.BorrowRule;
import com.example.demo.model.UserType;
import com.example.demo.service.BorrowRuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/borrow-rules")
@CrossOrigin(origins = "*")
public class BorrowRuleController {

    private static final Logger logger = LoggerFactory.getLogger(BorrowRuleController.class);

    @Autowired
    private BorrowRuleService borrowRuleService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllRules() {
        List<BorrowRule> rules = borrowRuleService.getAllRules();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", rules);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userType}")
    public ResponseEntity<Map<String, Object>> getRuleByUserType(@PathVariable String userType) {
        try {
            UserType type = UserType.fromCode(userType);
            BorrowRule rule = borrowRuleService.getRule(type);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", rule);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取借阅规则失败: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取借阅规则失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{userType}")
    public ResponseEntity<Map<String, Object>> updateRule(
            @PathVariable String userType,
            @RequestBody BorrowRule newRule) {
        try {
            UserType type = UserType.fromCode(userType);
            borrowRuleService.updateRule(type, newRule);
            
            logger.info("更新借阅规则成功: {}", type.getDescription());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "借阅规则更新成功");
            response.put("data", borrowRuleService.getRule(type));
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("更新借阅规则失败: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("更新借阅规则失败: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "更新借阅规则失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<Map<String, Object>> resetAllRules() {
        try {
            borrowRuleService.resetToDefault();
            
            logger.info("重置所有借阅规则为默认配置");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "所有借阅规则已重置为默认配置");
            response.put("data", borrowRuleService.getAllRules());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("重置借阅规则失败: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "重置借阅规则失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/reset/{userType}")
    public ResponseEntity<Map<String, Object>> resetRuleByUserType(@PathVariable String userType) {
        try {
            UserType type = UserType.fromCode(userType);
            borrowRuleService.resetRuleToDefault(type);
            
            logger.info("重置 {} 的借阅规则为默认配置", type.getDescription());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", type.getDescription() + "的借阅规则已重置为默认配置");
            response.put("data", borrowRuleService.getRule(type));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("重置借阅规则失败: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "重置借阅规则失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/user-types")
    public ResponseEntity<Map<String, Object>> getUserTypes() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", UserType.values());
        return ResponseEntity.ok(response);
    }
}
