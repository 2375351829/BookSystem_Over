package com.example.demo.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.model.UserAccount;
import com.example.demo.repository.UserAccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserAccountMapper userAccountMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        QueryWrapper<UserAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", "admin");
        UserAccount admin = userAccountMapper.selectOne(queryWrapper);

        if (admin == null) {
            admin = new UserAccount();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("cifs123456"));
            admin.setUserType("ADMIN");
            admin.setRealName("系统管理员");
            admin.setRole("ADMIN");
            admin.setStatus(1);
            admin.setDeleted(0);
            java.util.Date now = new java.util.Date();
            admin.setCreateTime(now);
            admin.setUpdateTime(now);
            userAccountMapper.insert(admin);
            System.out.println("Admin user created with password: cifs123456");
        } else {
            System.out.println("Admin user exists: " + admin.getUsername() + ", role: " + admin.getRole() + ", userType: " + admin.getUserType());
            admin.setPassword(passwordEncoder.encode("cifs123456"));
            admin.setUserType("ADMIN");
            admin.setRole("ADMIN");
            admin.setStatus(1);
            admin.setDeleted(0);
            admin.setUpdateTime(new java.util.Date());
            userAccountMapper.updateById(admin);
            System.out.println("Admin password has been reset to: cifs123456");
        }

        // Reset all user passwords to cifs123456
        List<UserAccount> allUsers = userAccountMapper.selectList(new QueryWrapper<>());
        for (UserAccount user : allUsers) {
            if (!"admin".equals(user.getUsername())) {
                user.setPassword(passwordEncoder.encode("cifs123456"));
                user.setUpdateTime(new java.util.Date());
                userAccountMapper.updateById(user);
            }
        }
        System.out.println("All user passwords have been reset to: cifs123456");
    }
}
