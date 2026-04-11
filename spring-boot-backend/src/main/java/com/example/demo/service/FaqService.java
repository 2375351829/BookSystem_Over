package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.model.Faq;
import com.example.demo.repository.FaqMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class FaqService {

    @Autowired
    private FaqMapper faqMapper;

    public List<Faq> getAllActiveFaqs() {
        return faqMapper.findAllActive();
    }

    public List<Faq> getFaqsByCategory(String category) {
        return faqMapper.findByCategory(category);
    }

    public List<String> getAllCategories() {
        return faqMapper.findAllCategories();
    }

    public List<Faq> searchFaqs(String keyword) {
        return faqMapper.searchByKeyword(keyword);
    }

    public Faq getFaqById(Long id) {
        return faqMapper.selectById(id);
    }

    public IPage<Faq> getFaqPage(int page, int size, String category, String keyword) {
        Page<Faq> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<Faq> queryWrapper = new LambdaQueryWrapper<>();
        
        queryWrapper.eq(Faq::getDeleted, 0);
        
        if (category != null && !category.isEmpty()) {
            queryWrapper.eq(Faq::getCategory, category);
        }
        
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                .like(Faq::getQuestion, keyword)
                .or().like(Faq::getAnswer, keyword)
            );
        }
        
        queryWrapper.orderByAsc(Faq::getSortOrder);
        queryWrapper.orderByDesc(Faq::getCreateTime);
        
        return faqMapper.selectPage(pageInfo, queryWrapper);
    }

    public Faq createFaq(Faq faq) {
        faq.setDeleted(0);
        faq.setViewCount(0);
        faq.setStatus(1);
        faq.setCreateTime(new Date());
        faq.setUpdateTime(new Date());
        faqMapper.insert(faq);
        return faq;
    }

    public Faq updateFaq(Long id, Faq faq) {
        Faq existingFaq = faqMapper.selectById(id);
        if (existingFaq == null || existingFaq.getDeleted() == 1) {
            return null;
        }
        
        faq.setId(id);
        faq.setDeleted(existingFaq.getDeleted());
        faq.setCreateTime(existingFaq.getCreateTime());
        faq.setViewCount(existingFaq.getViewCount());
        faq.setUpdateTime(new Date());
        faqMapper.updateById(faq);
        return faq;
    }

    public boolean deleteFaq(Long id) {
        Faq faq = faqMapper.selectById(id);
        if (faq == null || faq.getDeleted() == 1) {
            return false;
        }
        
        faq.setDeleted(1);
        faq.setUpdateTime(new Date());
        faqMapper.updateById(faq);
        return true;
    }

    public void incrementViewCount(Long id) {
        Faq faq = faqMapper.selectById(id);
        if (faq != null && faq.getDeleted() == 0) {
            faq.setViewCount(faq.getViewCount() + 1);
            faqMapper.updateById(faq);
        }
    }

    public void updateSortOrder(Long id, Integer sortOrder) {
        Faq faq = faqMapper.selectById(id);
        if (faq != null && faq.getDeleted() == 0) {
            faq.setSortOrder(sortOrder);
            faq.setUpdateTime(new Date());
            faqMapper.updateById(faq);
        }
    }
}
