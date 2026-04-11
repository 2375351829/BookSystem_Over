package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.model.UserAccount;
import com.example.demo.repository.UserAccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserAccountMapper userAccountMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount userAccount = null;

        QueryWrapper<UserAccount> usernameQuery = new QueryWrapper<>();
        usernameQuery.eq("username", username).eq("deleted", 0);
        userAccount = userAccountMapper.selectOne(usernameQuery);

        if (userAccount == null) {
            QueryWrapper<UserAccount> studentIdQuery = new QueryWrapper<>();
            studentIdQuery.eq("student_id", username).eq("deleted", 0);
            userAccount = userAccountMapper.selectOne(studentIdQuery);
        }

        if (userAccount == null) {
            QueryWrapper<UserAccount> facultyIdQuery = new QueryWrapper<>();
            facultyIdQuery.eq("faculty_id", username).eq("deleted", 0);
            userAccount = userAccountMapper.selectOne(facultyIdQuery);
        }

        if (userAccount == null) {
            QueryWrapper<UserAccount> userIdQuery = new QueryWrapper<>();
            userIdQuery.eq("user_code", username).eq("deleted", 0);
            userAccount = userAccountMapper.selectOne(userIdQuery);
        }

        if (userAccount == null) {
            throw new UsernameNotFoundException("User not found with username or identity ID: " + username);
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        String role = userAccount.getRole();
        if (role != null) {
            role = role.toUpperCase();
            if (role.equals("ADMIN") || role.equals("ROLE_ADMIN") || role.equals("ADMINISTRATOR")) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }
            if (role.equals("VIP") || role.equals("ROLE_VIP")) {
                authorities.add(new SimpleGrantedAuthority("ROLE_VIP"));
            }
        }

        String userType = userAccount.getUserType();
        if (userType != null) {
            userType = userType.toUpperCase();
            if (userType.equals("VIP")) {
                authorities.add(new SimpleGrantedAuthority("ROLE_VIP"));
            }
        }

        return new org.springframework.security.core.userdetails.User(
                userAccount.getUsername(),
                userAccount.getPassword(),
                authorities
        );
    }
}
