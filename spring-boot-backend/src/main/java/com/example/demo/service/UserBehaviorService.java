package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.model.UserBehavior;
import com.example.demo.repository.UserBehaviorMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserBehaviorService {

    private static final Logger logger = LoggerFactory.getLogger(UserBehaviorService.class);

    @Autowired
    private UserBehaviorMapper userBehaviorMapper;

    public boolean recordBehavior(UserBehavior behavior) {
        logger.info("记录用户行为: userId={}, actionType={}, bookId={}", 
                   behavior.getUserId(), behavior.getActionType(), behavior.getBookId());
        
        behavior.setCreateTime(new Date());
        int result = userBehaviorMapper.insert(behavior);
        
        if (result > 0) {
            logger.info("用户行为记录成功: behaviorId={}", behavior.getId());
        }
        return result > 0;
    }

    public boolean deleteBehavior(Long id) {
        logger.info("删除用户行为记录: behaviorId={}", id);
        
        int result = userBehaviorMapper.deleteById(id);
        
        if (result > 0) {
            logger.info("用户行为记录删除成功: behaviorId={}", id);
        }
        return result > 0;
    }

    public UserBehavior getBehaviorById(Long id) {
        return userBehaviorMapper.selectById(id);
    }

    public List<UserBehavior> getBehaviorsByUserId(Long userId) {
        return userBehaviorMapper.selectByUserId(userId);
    }

    public List<UserBehavior> getBehaviorsByUserIdAndActionType(Long userId, String actionType) {
        return userBehaviorMapper.selectByUserIdAndActionType(userId, actionType);
    }

    public List<UserBehavior> getBehaviorsByBookId(Long bookId) {
        return userBehaviorMapper.selectByBookId(bookId);
    }

    public Integer countByUserIdAndActionType(Long userId, String actionType) {
        return userBehaviorMapper.countByUserIdAndActionType(userId, actionType);
    }

    public Integer countViewUsersByBookId(Long bookId) {
        return userBehaviorMapper.countViewUsersByBookId(bookId);
    }

    public IPage<UserBehavior> getBehaviorsByPage(int page, int size) {
        Page<UserBehavior> pageInfo = new Page<>(page, size);
        QueryWrapper<UserBehavior> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");
        return userBehaviorMapper.selectPage(pageInfo, queryWrapper);
    }

    public IPage<UserBehavior> getBehaviorsByUserIdByPage(Long userId, int page, int size) {
        Page<UserBehavior> pageInfo = new Page<>(page, size);
        QueryWrapper<UserBehavior> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).orderByDesc("create_time");
        return userBehaviorMapper.selectPage(pageInfo, queryWrapper);
    }

    public boolean recordViewBehavior(Long userId, Long bookId, String ipAddress, String userAgent) {
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(userId);
        behavior.setBookId(bookId);
        behavior.setActionType("VIEW");
        behavior.setIpAddress(ipAddress);
        behavior.setUserAgent(userAgent);
        return recordBehavior(behavior);
    }

    public boolean recordSearchBehavior(Long userId, String searchKeyword, String ipAddress, String userAgent) {
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(userId);
        behavior.setActionType("SEARCH");
        behavior.setActionDetail(searchKeyword);
        behavior.setIpAddress(ipAddress);
        behavior.setUserAgent(userAgent);
        return recordBehavior(behavior);
    }

    public boolean recordBorrowBehavior(Long userId, Long bookId) {
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(userId);
        behavior.setBookId(bookId);
        behavior.setActionType("BORROW");
        return recordBehavior(behavior);
    }

    public boolean recordReturnBehavior(Long userId, Long bookId) {
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(userId);
        behavior.setBookId(bookId);
        behavior.setActionType("RETURN");
        return recordBehavior(behavior);
    }

    public boolean recordFavoriteBehavior(Long userId, Long bookId) {
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(userId);
        behavior.setBookId(bookId);
        behavior.setActionType("FAVORITE");
        return recordBehavior(behavior);
    }
}
