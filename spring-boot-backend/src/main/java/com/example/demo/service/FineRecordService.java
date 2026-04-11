package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.model.BorrowRecord;
import com.example.demo.model.FineRecord;
import com.example.demo.model.FineStatus;
import com.example.demo.model.FineType;
import com.example.demo.repository.BorrowRecordMapper;
import com.example.demo.repository.FineRecordMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

@Service
public class FineRecordService {

    private static final Logger logger = LoggerFactory.getLogger(FineRecordService.class);

    private static final BigDecimal FINE_PER_DAY = new BigDecimal("0.50");
    private static final int GRACE_PERIOD_DAYS = 3;
    private static final BigDecimal MAX_FINE_AMOUNT = new BigDecimal("100.00");
    private static final BigDecimal DAMAGE_FINE_RATE = new BigDecimal("0.30");
    private static final BigDecimal LOSS_FINE_RATE = new BigDecimal("2.00");

    @Autowired
    private FineRecordMapper fineRecordMapper;

    @Autowired
    private BorrowRecordMapper borrowRecordMapper;

    @Transactional
    public FineRecord createFineRecord(Long userId, Long borrowRecordId, FineType fineType, 
                                        BigDecimal amount, String description, Long operatorId) {
        logger.info("创建罚款记录: userId={}, borrowRecordId={}, fineType={}, amount={}", 
                    userId, borrowRecordId, fineType, amount);

        FineRecord fineRecord = new FineRecord();
        fineRecord.setUserId(userId);
        fineRecord.setBorrowRecordId(borrowRecordId);
        fineRecord.setFineTypeEnum(fineType);
        fineRecord.setAmount(amount);
        fineRecord.setPaidStatus(FineStatus.UNPAID.getCode());
        fineRecord.setRemarks(description);
        fineRecord.setOperatorId(operatorId);
        fineRecord.setDeleted(0);
        fineRecord.setCreateTime(new Date());

        fineRecordMapper.insert(fineRecord);
        logger.info("罚款记录创建成功: id={}", fineRecord.getId());

        return fineRecord;
    }

    @Transactional
    public FineRecord createOverdueFine(Long userId, Long borrowRecordId) {
        BorrowRecord borrowRecord = borrowRecordMapper.selectById(borrowRecordId);
        if (borrowRecord == null) {
            throw new RuntimeException("借阅记录不存在");
        }

        LambdaQueryWrapper<FineRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FineRecord::getUserId, userId)
                   .eq(FineRecord::getBorrowRecordId, borrowRecordId)
                   .eq(FineRecord::getFineType, FineType.OVERDUE.getCode())
                   .eq(FineRecord::getDeleted, 0);
        if (fineRecordMapper.selectCount(queryWrapper) > 0) {
            throw new RuntimeException("该借阅记录已存在逾期罚款");
        }

        BigDecimal fineAmount = calculateOverdueFine(borrowRecord.getDueDate(), new Date());
        if (fineAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("未产生逾期罚款");
        }

        String description = String.format("逾期罚款: 应还日期 %s", borrowRecord.getDueDate());
        return createFineRecord(userId, borrowRecordId, FineType.OVERDUE, fineAmount, description, userId);
    }

    public BigDecimal calculateOverdueFine(Date dueDate, Date returnDate) {
        if (dueDate == null || returnDate == null) {
            return BigDecimal.ZERO;
        }

        long diffMs = returnDate.getTime() - dueDate.getTime();
        long diffDays = diffMs / (24 * 60 * 60 * 1000);

        long effectiveDays = diffDays - GRACE_PERIOD_DAYS;
        if (effectiveDays <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal fine = FINE_PER_DAY.multiply(new BigDecimal(effectiveDays));
        fine = fine.min(MAX_FINE_AMOUNT);
        fine = fine.setScale(2, RoundingMode.HALF_UP);

        return fine;
    }

    public BigDecimal calculateDamageFine(BigDecimal bookPrice) {
        if (bookPrice == null || bookPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return new BigDecimal("10.00");
        }
        BigDecimal fine = bookPrice.multiply(DAMAGE_FINE_RATE);
        fine = fine.min(MAX_FINE_AMOUNT);
        fine = fine.setScale(2, RoundingMode.HALF_UP);
        return fine;
    }

    public BigDecimal calculateLossFine(BigDecimal bookPrice) {
        if (bookPrice == null || bookPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return new BigDecimal("50.00");
        }
        BigDecimal fine = bookPrice.multiply(LOSS_FINE_RATE);
        fine = fine.min(MAX_FINE_AMOUNT);
        fine = fine.setScale(2, RoundingMode.HALF_UP);
        return fine;
    }

    @Transactional
    public FineRecord payFine(Long fineId, Long userId) {
        logger.info("缴纳罚款: fineId={}, userId={}", fineId, userId);

        FineRecord fineRecord = fineRecordMapper.selectById(fineId);
        if (fineRecord == null || fineRecord.getDeleted() == 1) {
            throw new RuntimeException("罚款记录不存在");
        }

        if (!fineRecord.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此罚款记录");
        }

        if (FineStatus.PAID.getCode().equals(fineRecord.getPaidStatus())) {
            throw new RuntimeException("该罚款已支付");
        }

        fineRecord.setPaidStatus(FineStatus.PAID.getCode());
        fineRecord.setPaidDate(new Date());
        fineRecordMapper.updateById(fineRecord);

        logger.info("罚款支付成功: fineId={}", fineId);
        return fineRecord;
    }

    @Transactional
    public FineRecord payFineByAdmin(Long fineId, Long operatorId) {
        logger.info("管理员代缴罚款: fineId={}, operatorId={}", fineId, operatorId);

        FineRecord fineRecord = fineRecordMapper.selectById(fineId);
        if (fineRecord == null || fineRecord.getDeleted() == 1) {
            throw new RuntimeException("罚款记录不存在");
        }

        if (FineStatus.PAID.getCode().equals(fineRecord.getPaidStatus())) {
            throw new RuntimeException("该罚款已支付");
        }

        fineRecord.setPaidStatus(FineStatus.PAID.getCode());
        fineRecord.setPaidDate(new Date());
        fineRecord.setOperatorId(operatorId);
        fineRecordMapper.updateById(fineRecord);

        logger.info("管理员代缴罚款成功: fineId={}", fineId);
        return fineRecord;
    }

    public IPage<FineRecord> getUserFines(Long userId, Integer page, Integer size, Integer status) {
        Page<FineRecord> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<FineRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FineRecord::getUserId, userId)
                   .eq(FineRecord::getDeleted, 0);

        if (status != null) {
            queryWrapper.eq(FineRecord::getPaidStatus, status);
        }

        queryWrapper.orderByDesc(FineRecord::getCreateTime);

        return fineRecordMapper.selectPage(pageInfo, queryWrapper);
    }

    public IPage<FineRecord> getAllFines(Integer page, Integer size, Long userId, 
                                          String fineType, Integer status) {
        Page<FineRecord> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<FineRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FineRecord::getDeleted, 0);

        if (userId != null) {
            queryWrapper.eq(FineRecord::getUserId, userId);
        }

        if (fineType != null && !fineType.isEmpty()) {
            queryWrapper.eq(FineRecord::getFineType, fineType);
        }

        if (status != null) {
            queryWrapper.eq(FineRecord::getPaidStatus, status);
        }

        queryWrapper.orderByDesc(FineRecord::getCreateTime);

        return fineRecordMapper.selectPage(pageInfo, queryWrapper);
    }

    public FineRecord getFineById(Long fineId) {
        FineRecord fineRecord = fineRecordMapper.selectById(fineId);
        if (fineRecord == null || fineRecord.getDeleted() == 1) {
            return null;
        }
        return fineRecord;
    }

    public BigDecimal getUnpaidFineTotal(Long userId) {
        LambdaQueryWrapper<FineRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FineRecord::getUserId, userId)
                   .eq(FineRecord::getPaidStatus, FineStatus.UNPAID.getCode())
                   .eq(FineRecord::getDeleted, 0);

        List<FineRecord> unpaidFines = fineRecordMapper.selectList(queryWrapper);
        return unpaidFines.stream()
                .map(FineRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public int getUnpaidFineCount(Long userId) {
        LambdaQueryWrapper<FineRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FineRecord::getUserId, userId)
                   .eq(FineRecord::getPaidStatus, FineStatus.UNPAID.getCode())
                   .eq(FineRecord::getDeleted, 0);
        return Math.toIntExact(fineRecordMapper.selectCount(queryWrapper));
    }

    public boolean hasUnpaidFines(Long userId) {
        return getUnpaidFineCount(userId) > 0;
    }

    @Transactional
    public void cancelFine(Long fineId, Long operatorId, String reason) {
        logger.info("取消罚款: fineId={}, operatorId={}, reason={}", fineId, operatorId, reason);

        FineRecord fineRecord = fineRecordMapper.selectById(fineId);
        if (fineRecord == null || fineRecord.getDeleted() == 1) {
            throw new RuntimeException("罚款记录不存在");
        }

        if (FineStatus.PAID.getCode().equals(fineRecord.getPaidStatus())) {
            throw new RuntimeException("已支付的罚款无法取消");
        }

        LambdaUpdateWrapper<FineRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(FineRecord::getId, fineId)
                    .set(FineRecord::getDeleted, 1)
                    .set(FineRecord::getRemarks, fineRecord.getRemarks() + " | 取消原因: " + reason);

        fineRecordMapper.update(null, updateWrapper);
        logger.info("罚款取消成功: fineId={}", fineId);
    }
}
