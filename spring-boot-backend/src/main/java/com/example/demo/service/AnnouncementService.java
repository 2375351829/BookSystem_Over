package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.model.Announcement;
import com.example.demo.repository.AnnouncementMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AnnouncementService {

    @Autowired
    private AnnouncementMapper announcementMapper;

    public List<Announcement> getActiveAnnouncements() {
        return announcementMapper.findActiveAnnouncements();
    }

    public List<Announcement> getAnnouncementsByType(String type) {
        return announcementMapper.findByType(type);
    }

    public Announcement getAnnouncementById(Long id) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement != null && announcement.getDeleted() == 0) {
            announcement.setViewCount(announcement.getViewCount() + 1);
            announcementMapper.updateById(announcement);
        }
        return announcement;
    }

    public IPage<Announcement> getAnnouncementPage(int page, int size, String type, Integer status) {
        Page<Announcement> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<Announcement> queryWrapper = new LambdaQueryWrapper<>();
        
        queryWrapper.eq(Announcement::getDeleted, 0);
        
        if (type != null && !type.isEmpty()) {
            queryWrapper.eq(Announcement::getType, type);
        }
        
        if (status != null) {
            queryWrapper.eq(Announcement::getStatus, status);
        }
        
        queryWrapper.orderByDesc(Announcement::getPriority);
        queryWrapper.orderByDesc(Announcement::getPublishTime);
        
        return announcementMapper.selectPage(pageInfo, queryWrapper);
    }

    public Announcement createAnnouncement(Announcement announcement) {
        announcement.setDeleted(0);
        announcement.setViewCount(0);
        announcement.setStatus(1);
        announcement.setPublishTime(new Date());
        announcement.setCreateTime(new Date());
        announcement.setUpdateTime(new Date());
        announcementMapper.insert(announcement);
        return announcement;
    }

    public Announcement updateAnnouncement(Long id, Announcement announcement) {
        Announcement existing = announcementMapper.selectById(id);
        if (existing == null || existing.getDeleted() == 1) {
            return null;
        }
        
        announcement.setId(id);
        announcement.setDeleted(existing.getDeleted());
        announcement.setCreateTime(existing.getCreateTime());
        announcement.setViewCount(existing.getViewCount());
        announcement.setUpdateTime(new Date());
        announcementMapper.updateById(announcement);
        return announcement;
    }

    public boolean deleteAnnouncement(Long id) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null || announcement.getDeleted() == 1) {
            return false;
        }
        
        announcement.setDeleted(1);
        announcement.setUpdateTime(new Date());
        announcementMapper.updateById(announcement);
        return true;
    }

    public void updateStatus(Long id, Integer status) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement != null && announcement.getDeleted() == 0) {
            announcement.setStatus(status);
            announcement.setUpdateTime(new Date());
            announcementMapper.updateById(announcement);
        }
    }
}
