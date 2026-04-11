package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.model.OperationLog;
import com.example.demo.repository.OperationLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class LogService {

    @Autowired
    private OperationLogMapper operationLogMapper;

    public IPage<OperationLog> getLogs(int page, int pageSize, String operator, String operationType, String module, String startDate, String endDate) {
        Page<OperationLog> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<OperationLog> queryWrapper = new LambdaQueryWrapper<>();

        if (operator != null && !operator.isEmpty()) {
            queryWrapper.like(OperationLog::getUsername, operator);
        }
        if (operationType != null && !operationType.isEmpty()) {
            queryWrapper.eq(OperationLog::getOperation, operationType);
        }
        if (module != null && !module.isEmpty()) {
            queryWrapper.eq(OperationLog::getModule, module);
        }

        if (startDate != null && !startDate.isEmpty()) {
            LocalDate start = LocalDate.parse(startDate);
            LocalDateTime startDateTime = start.atStartOfDay();
            queryWrapper.ge(OperationLog::getCreateTime, startDateTime);
        }
        if (endDate != null && !endDate.isEmpty()) {
            LocalDate end = LocalDate.parse(endDate);
            LocalDateTime endDateTime = end.plusDays(1).atStartOfDay();
            queryWrapper.lt(OperationLog::getCreateTime, endDateTime);
        }

        queryWrapper.orderByDesc(OperationLog::getCreateTime);
        return operationLogMapper.selectPage(pageParam, queryWrapper);
    }

    public OperationLog getLogById(Long id) {
        return operationLogMapper.selectById(id);
    }

    public void saveLog(OperationLog log) {
        operationLogMapper.insert(log);
    }

    public List<OperationLog> getRecentLogs(int limit) {
        LambdaQueryWrapper<OperationLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(OperationLog::getCreateTime);
        queryWrapper.last("LIMIT " + limit);
        return operationLogMapper.selectList(queryWrapper);
    }

    public List<OperationLog> getAllLogs(String operator, String operationType, String module, String startDate, String endDate) {
        LambdaQueryWrapper<OperationLog> queryWrapper = new LambdaQueryWrapper<>();

        if (operator != null && !operator.isEmpty()) {
            queryWrapper.like(OperationLog::getUsername, operator);
        }
        if (operationType != null && !operationType.isEmpty()) {
            queryWrapper.eq(OperationLog::getOperation, operationType);
        }
        if (module != null && !module.isEmpty()) {
            queryWrapper.eq(OperationLog::getModule, module);
        }

        if (startDate != null && !startDate.isEmpty()) {
            LocalDate start = LocalDate.parse(startDate);
            LocalDateTime startDateTime = start.atStartOfDay();
            queryWrapper.ge(OperationLog::getCreateTime, startDateTime);
        }
        if (endDate != null && !endDate.isEmpty()) {
            LocalDate end = LocalDate.parse(endDate);
            LocalDateTime endDateTime = end.plusDays(1).atStartOfDay();
            queryWrapper.lt(OperationLog::getCreateTime, endDateTime);
        }

        queryWrapper.orderByDesc(OperationLog::getCreateTime);
        return operationLogMapper.selectList(queryWrapper);
    }

    public int clearLogs() {
        return operationLogMapper.delete(null);
    }
}