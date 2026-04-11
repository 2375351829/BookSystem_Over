package com.example.demo.service;

import com.example.demo.model.BorrowRule;
import com.example.demo.model.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BorrowRuleService {

    private static final Logger logger = LoggerFactory.getLogger(BorrowRuleService.class);

    private final Map<UserType, BorrowRule> ruleCache = new ConcurrentHashMap<>();

    public BorrowRuleService() {
        initializeDefaultRules();
    }

    private void initializeDefaultRules() {
        ruleCache.put(UserType.READER, new BorrowRule(
            UserType.READER, 10, 30, 2, 30, new BigDecimal("0.50")
        ));
        ruleCache.put(UserType.STUDENT, new BorrowRule(
            UserType.STUDENT, 15, 45, 2, 30, new BigDecimal("0.30")
        ));
        ruleCache.put(UserType.TEACHER, new BorrowRule(
            UserType.TEACHER, 20, 60, 3, 30, new BigDecimal("0.20")
        ));
        ruleCache.put(UserType.INTERNATIONAL, new BorrowRule(
            UserType.INTERNATIONAL, 8, 30, 1, 30, new BigDecimal("0.50")
        ));
        logger.info("借阅规则初始化完成，共加载 {} 条规则", ruleCache.size());
    }

    public BorrowRule getRule(UserType userType) {
        BorrowRule rule = ruleCache.get(userType);
        if (rule == null) {
            logger.warn("未找到用户类型 {} 的借阅规则，使用默认规则", userType);
            return ruleCache.get(UserType.READER);
        }
        return rule;
    }

    public BorrowRule getRuleByCode(String userTypeCode) {
        UserType userType = UserType.fromCode(userTypeCode);
        return getRule(userType);
    }

    public int getMaxBorrowLimit(UserType userType) {
        return getRule(userType).getMaxBorrowLimit();
    }

    public int getBorrowDays(UserType userType) {
        return getRule(userType).getBorrowDays();
    }

    public int getMaxRenewCount(UserType userType) {
        return getRule(userType).getMaxRenewCount();
    }

    public int getRenewDays(UserType userType) {
        return getRule(userType).getRenewDays();
    }

    public BigDecimal getFineRate(UserType userType) {
        return getRule(userType).getFineRatePerDay();
    }

    public List<BorrowRule> getAllRules() {
        return new ArrayList<>(ruleCache.values());
    }

    public void updateRule(UserType userType, BorrowRule newRule) {
        if (userType == null || newRule == null) {
            throw new IllegalArgumentException("用户类型和规则不能为空");
        }
        
        newRule.setUserType(userType);
        newRule.setUserTypeCode(userType.getCode());
        newRule.setDescription(userType.getDescription());
        
        ruleCache.put(userType, newRule);
        logger.info("更新借阅规则: {} -> 借阅上限: {}, 借阅期限: {}天, 续借次数: {}, 罚款费率: {}元/天",
            userType.getDescription(), newRule.getMaxBorrowLimit(), newRule.getBorrowDays(),
            newRule.getMaxRenewCount(), newRule.getFineRatePerDay());
    }

    public void updateRuleByCode(String userTypeCode, BorrowRule newRule) {
        UserType userType = UserType.fromCode(userTypeCode);
        updateRule(userType, newRule);
    }

    public void resetToDefault() {
        ruleCache.clear();
        initializeDefaultRules();
        logger.info("借阅规则已重置为默认配置");
    }

    public void resetRuleToDefault(UserType userType) {
        BorrowRule defaultRule = createDefaultRule(userType);
        ruleCache.put(userType, defaultRule);
        logger.info("已重置 {} 的借阅规则为默认配置", userType.getDescription());
    }

    private BorrowRule createDefaultRule(UserType userType) {
        return switch (userType) {
            case READER -> new BorrowRule(UserType.READER, 10, 30, 2, 30, new BigDecimal("0.50"));
            case STUDENT -> new BorrowRule(UserType.STUDENT, 15, 45, 2, 30, new BigDecimal("0.30"));
            case TEACHER -> new BorrowRule(UserType.TEACHER, 20, 60, 3, 30, new BigDecimal("0.20"));
            case INTERNATIONAL -> new BorrowRule(UserType.INTERNATIONAL, 8, 30, 1, 30, new BigDecimal("0.50"));
        };
    }
}
