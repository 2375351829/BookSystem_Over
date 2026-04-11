package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.FineRecord;
import com.example.demo.model.FineStatus;
import com.example.demo.model.FineType;
import com.example.demo.model.UserAccount;
import com.example.demo.repository.UserAccountMapper;
import com.example.demo.service.FineRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/fines")
@CrossOrigin(origins = "*")
public class FineController {

    private static final Logger logger = LoggerFactory.getLogger(FineController.class);

    @Autowired
    private FineRecordService fineRecordService;

    @Autowired
    private UserAccountMapper userAccountMapper;

    private Long getCurrentUserId(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        QueryWrapper<UserAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username).eq("deleted", 0);
        UserAccount userAccount = userAccountMapper.selectOne(queryWrapper);
        if (userAccount == null) {
            throw new UserNotFoundException("用户不存在");
        }
        return userAccount.getId();
    }

    @GetMapping
    public ResponseEntity<?> getUserFines(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            Authentication authentication) {
        
        Long userId = getCurrentUserId(authentication);
        logger.info("查询用户罚款记录: userId={}, page={}, size={}, status={}", userId, page, size, status);
        
        IPage<FineRecord> pageResult = fineRecordService.getUserFines(userId, page, size, status);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", pageResult);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unpaid")
    public ResponseEntity<?> getUnpaidFines(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        logger.info("查询用户未支付罚款: userId={}", userId);
        
        BigDecimal totalAmount = fineRecordService.getUnpaidFineTotal(userId);
        int count = fineRecordService.getUnpaidFineCount(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", Map.of(
            "totalAmount", totalAmount,
            "count", count
        ));
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{fineId}/pay")
    public ResponseEntity<?> payFine(
            @PathVariable Long fineId,
            Authentication authentication) {
        
        Long userId = getCurrentUserId(authentication);
        logger.info("用户缴纳罚款: fineId={}, userId={}", fineId, userId);
        
        try {
            FineRecord fineRecord = fineRecordService.payFine(fineId, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "支付成功");
            response.put("data", fineRecord);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("罚款支付失败: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getFineHistory(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            Authentication authentication) {
        
        Long userId = getCurrentUserId(authentication);
        logger.info("查询用户已支付罚款历史: userId={}", userId);
        
        IPage<FineRecord> pageResult = fineRecordService.getUserFines(userId, page, size, FineStatus.PAID.getCode());
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", pageResult);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{fineId}")
    public ResponseEntity<?> getFineDetail(
            @PathVariable Long fineId,
            Authentication authentication) {
        
        Long userId = getCurrentUserId(authentication);
        logger.info("查询罚款详情: fineId={}, userId={}", fineId, userId);
        
        FineRecord fineRecord = fineRecordService.getFineById(fineId);
        if (fineRecord == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 404);
            response.put("message", "罚款记录不存在");
            return ResponseEntity.status(404).body(response);
        }
        
        if (!fineRecord.getUserId().equals(userId)) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 403);
            response.put("message", "无权查看此罚款记录");
            return ResponseEntity.status(403).body(response);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", fineRecord);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/list")
    public ResponseEntity<?> getAllFines(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String fineType,
            @RequestParam(required = false) Integer status) {
        
        logger.info("管理员查询所有罚款记录: page={}, size={}, userId={}, fineType={}, status={}", 
                    page, size, userId, fineType, status);
        
        IPage<FineRecord> pageResult = fineRecordService.getAllFines(page, size, userId, fineType, status);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", pageResult);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin/create")
    public ResponseEntity<?> createFine(
            @RequestParam Long userId,
            @RequestParam(required = false) Long borrowRecordId,
            @RequestParam String fineType,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String description,
            Authentication authentication) {
        
        Long operatorId = getCurrentUserId(authentication);
        logger.info("管理员创建罚款: userId={}, borrowRecordId={}, fineType={}, amount={}", 
                    userId, borrowRecordId, fineType, amount);
        
        try {
            FineType type = FineType.fromCode(fineType);
            FineRecord fineRecord = fineRecordService.createFineRecord(
                userId, borrowRecordId, type, amount, description, operatorId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "罚款创建成功");
            response.put("data", fineRecord);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("创建罚款失败: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/admin/{fineId}/pay")
    public ResponseEntity<?> adminPayFine(
            @PathVariable Long fineId,
            Authentication authentication) {
        
        Long operatorId = getCurrentUserId(authentication);
        logger.info("管理员代缴罚款: fineId={}, operatorId={}", fineId, operatorId);
        
        try {
            FineRecord fineRecord = fineRecordService.payFineByAdmin(fineId, operatorId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "代缴成功");
            response.put("data", fineRecord);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("管理员代缴罚款失败: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/admin/{fineId}")
    public ResponseEntity<?> cancelFine(
            @PathVariable Long fineId,
            @RequestParam String reason,
            Authentication authentication) {
        
        Long operatorId = getCurrentUserId(authentication);
        logger.info("管理员取消罚款: fineId={}, operatorId={}, reason={}", fineId, operatorId, reason);
        
        try {
            fineRecordService.cancelFine(fineId, operatorId, reason);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "罚款已取消");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("取消罚款失败: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/calculate")
    public ResponseEntity<?> calculateFine(
            @RequestParam String fineType,
            @RequestParam(required = false) BigDecimal bookPrice,
            @RequestParam(required = false) Long overdueDays) {
        
        logger.info("计算罚款金额: fineType={}, bookPrice={}, overdueDays={}", fineType, bookPrice, overdueDays);
        
        BigDecimal amount;
        FineType type = FineType.fromCode(fineType);
        
        switch (type) {
            case OVERDUE:
                if (overdueDays == null || overdueDays <= 0) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("code", 400);
                    response.put("message", "逾期天数必须大于0");
                    return ResponseEntity.badRequest().body(response);
                }
                long effectiveDays = overdueDays - 3;
                if (effectiveDays <= 0) {
                    amount = BigDecimal.ZERO;
                } else {
                    amount = new BigDecimal("0.50").multiply(new BigDecimal(effectiveDays));
                    amount = amount.min(new BigDecimal("100.00"));
                }
                break;
            case DAMAGE:
                amount = fineRecordService.calculateDamageFine(bookPrice);
                break;
            case LOSS:
                amount = fineRecordService.calculateLossFine(bookPrice);
                break;
            default:
                amount = BigDecimal.ZERO;
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", Map.of(
            "fineType", type.getCode(),
            "fineTypeDesc", type.getDescription(),
            "amount", amount
        ));
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/types")
    public ResponseEntity<?> getFineTypes() {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", FineType.values());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statuses")
    public ResponseEntity<?> getFineStatuses() {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", FineStatus.values());
        
        return ResponseEntity.ok(response);
    }
}
