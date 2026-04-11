package com.example.demo.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.model.BookImportDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BookImportDetailMapper extends BaseMapper<BookImportDetail> {
    
    @Select("SELECT * FROM book_import_detail WHERE batch_id = #{batchId} ORDER BY row_number")
    List<BookImportDetail> selectByBatchId(@Param("batchId") Long batchId);
    
    @Select("SELECT * FROM book_import_detail WHERE batch_id = #{batchId} AND status = #{status} ORDER BY row_number")
    List<BookImportDetail> selectByBatchIdAndStatus(@Param("batchId") Long batchId, @Param("status") String status);
    
    @Select("SELECT COUNT(*) FROM book_import_detail WHERE batch_id = #{batchId}")
    Integer countByBatchId(@Param("batchId") Long batchId);
    
    @Select("SELECT COUNT(*) FROM book_import_detail WHERE batch_id = #{batchId} AND status = #{status}")
    Integer countByBatchIdAndStatus(@Param("batchId") Long batchId, @Param("status") String status);
}
