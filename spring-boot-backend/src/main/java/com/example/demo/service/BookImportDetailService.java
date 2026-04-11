package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.model.BookImportDetail;
import com.example.demo.repository.BookImportDetailMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class BookImportDetailService {

    private static final Logger logger = LoggerFactory.getLogger(BookImportDetailService.class);

    @Autowired
    private BookImportDetailMapper bookImportDetailMapper;

    public boolean addImportDetail(BookImportDetail detail) {
        logger.info("添加导入明细: batchId={}, rowNumber={}, isbn={}", detail.getBatchId(), detail.getRowNumber(), detail.getIsbn());
        
        detail.setCreateTime(new Date());
        int result = bookImportDetailMapper.insert(detail);
        
        if (result > 0) {
            logger.info("导入明细添加成功: detailId={}", detail.getId());
        }
        return result > 0;
    }

    public boolean updateImportDetail(BookImportDetail detail) {
        logger.info("更新导入明细: detailId={}", detail.getId());
        
        int result = bookImportDetailMapper.updateById(detail);
        
        if (result > 0) {
            logger.info("导入明细更新成功: detailId={}", detail.getId());
        }
        return result > 0;
    }

    public boolean deleteImportDetail(Long id) {
        logger.info("删除导入明细: detailId={}", id);
        
        int result = bookImportDetailMapper.deleteById(id);
        
        if (result > 0) {
            logger.info("导入明细删除成功: detailId={}", id);
        }
        return result > 0;
    }

    public BookImportDetail getImportDetailById(Long id) {
        return bookImportDetailMapper.selectById(id);
    }

    public List<BookImportDetail> getDetailsByBatchId(Long batchId) {
        return bookImportDetailMapper.selectByBatchId(batchId);
    }

    public List<BookImportDetail> getDetailsByBatchIdAndStatus(Long batchId, String status) {
        return bookImportDetailMapper.selectByBatchIdAndStatus(batchId, status);
    }

    public Integer countByBatchId(Long batchId) {
        return bookImportDetailMapper.countByBatchId(batchId);
    }

    public Integer countByBatchIdAndStatus(Long batchId, String status) {
        return bookImportDetailMapper.countByBatchIdAndStatus(batchId, status);
    }

    public IPage<BookImportDetail> getDetailsByPage(int page, int size) {
        Page<BookImportDetail> pageInfo = new Page<>(page, size);
        QueryWrapper<BookImportDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");
        return bookImportDetailMapper.selectPage(pageInfo, queryWrapper);
    }

    public IPage<BookImportDetail> getDetailsByBatchIdByPage(Long batchId, int page, int size) {
        Page<BookImportDetail> pageInfo = new Page<>(page, size);
        QueryWrapper<BookImportDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("batch_id", batchId).orderByAsc("row_number");
        return bookImportDetailMapper.selectPage(pageInfo, queryWrapper);
    }

    public boolean batchInsert(List<BookImportDetail> details) {
        logger.info("批量添加导入明细: count={}", details.size());
        
        int successCount = 0;
        for (BookImportDetail detail : details) {
            detail.setCreateTime(new Date());
            int result = bookImportDetailMapper.insert(detail);
            if (result > 0) {
                successCount++;
            }
        }
        
        logger.info("批量添加完成: 成功={}, 总数={}", successCount, details.size());
        return successCount == details.size();
    }
}
