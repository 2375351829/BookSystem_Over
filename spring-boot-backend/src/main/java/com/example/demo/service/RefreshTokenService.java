package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.model.RefreshToken;
import com.example.demo.model.UserAccount;
import com.example.demo.repository.RefreshTokenMapper;
import com.example.demo.repository.UserAccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(RefreshTokenService.class);

    @Value("${jwt.refreshExpiration:604800000}")
    private long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenMapper refreshTokenMapper;

    @Autowired
    private UserAccountMapper userAccountMapper;

    public RefreshToken findByToken(String token) {
        QueryWrapper<RefreshToken> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("token", token);
        return refreshTokenMapper.selectOne(queryWrapper);
    }

    public RefreshToken findByUserId(Long userId) {
        QueryWrapper<RefreshToken> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return refreshTokenMapper.selectOne(queryWrapper);
    }

    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = findByUserId(userId);
        
        if (refreshToken != null) {
            refreshToken.setToken(UUID.randomUUID().toString());
            refreshToken.setExpiryDate(calculateExpiryDate());
            refreshToken.setRevoked(0);
            refreshTokenMapper.updateById(refreshToken);
            logger.info("更新RefreshToken: userId={}", userId);
        } else {
            refreshToken = new RefreshToken();
            refreshToken.setUserId(userId);
            refreshToken.setToken(UUID.randomUUID().toString());
            refreshToken.setExpiryDate(calculateExpiryDate());
            refreshToken.setCreateTime(new Date());
            refreshToken.setRevoked(0);
            refreshTokenMapper.insert(refreshToken);
            logger.info("创建RefreshToken: userId={}", userId);
        }
        
        return refreshToken;
    }

    public boolean verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(new Date()) < 0) {
            refreshTokenMapper.deleteById(token.getId());
            logger.warn("RefreshToken已过期: tokenId={}", token.getId());
            return false;
        }
        
        if (token.getRevoked() != null && token.getRevoked() == 1) {
            logger.warn("RefreshToken已被撤销: tokenId={}", token.getId());
            return false;
        }
        
        return true;
    }

    @Transactional
    public int deleteByUserId(Long userId) {
        QueryWrapper<RefreshToken> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        int result = refreshTokenMapper.delete(queryWrapper);
        logger.info("删除用户RefreshToken: userId={}, 删除数量={}", userId, result);
        return result;
    }

    @Transactional
    public void revokeToken(String token) {
        RefreshToken refreshToken = findByToken(token);
        if (refreshToken != null) {
            refreshToken.setRevoked(1);
            refreshTokenMapper.updateById(refreshToken);
            logger.info("撤销RefreshToken: token={}", token);
        }
    }

    @Transactional
    public int deleteByToken(String token) {
        QueryWrapper<RefreshToken> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("token", token);
        int result = refreshTokenMapper.delete(queryWrapper);
        logger.info("删除RefreshToken: token={}", token);
        return result;
    }

    private Date calculateExpiryDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND, (int) refreshTokenDurationMs);
        return calendar.getTime();
    }
}
