package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.model.UserAccount;
import com.example.demo.repository.UserAccountMapper;
import com.example.demo.util.PasswordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Date;
import java.util.List;

@Service
public class UserService {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(UserService.class);
    private static final String PASSWORD_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
    private static final int PASSWORD_LENGTH = 8;
    private static final SecureRandom RANDOM = new SecureRandom();

    private static final java.util.Map<String, String> SEARCH_FIELD_MAP = new java.util.HashMap<>();
    static {
        SEARCH_FIELD_MAP.put("username", "username");
        SEARCH_FIELD_MAP.put("identityId", "identity_id");
        SEARCH_FIELD_MAP.put("realName", "real_name");
        SEARCH_FIELD_MAP.put("userType", "user_type");
        SEARCH_FIELD_MAP.put("phone", "phone");
        SEARCH_FIELD_MAP.put("email", "email");
        SEARCH_FIELD_MAP.put("campus", "campus");
        SEARCH_FIELD_MAP.put("college", "college");
        SEARCH_FIELD_MAP.put("grade", "grade");
        SEARCH_FIELD_MAP.put("className", "class_name");
    }

    @Autowired
    private UserAccountMapper userAccountMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String generateInitialPassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            password.append(PASSWORD_CHARS.charAt(RANDOM.nextInt(PASSWORD_CHARS.length())));
        }
        return password.toString();
    }

    public UserAccount getUserByStudentId(String studentId) {
        if (studentId == null || studentId.isEmpty()) {
            return null;
        }
        QueryWrapper<UserAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId).eq("deleted", 0);
        return userAccountMapper.selectOne(queryWrapper);
    }

    public UserAccount getUserByFacultyId(String facultyId) {
        if (facultyId == null || facultyId.isEmpty()) {
            return null;
        }
        QueryWrapper<UserAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("faculty_id", facultyId).eq("deleted", 0);
        return userAccountMapper.selectOne(queryWrapper);
    }

    public UserAccount getUserByUserId(String userId) {
        if (userId == null || userId.isEmpty()) {
            return null;
        }
        QueryWrapper<UserAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_code", userId).eq("deleted", 0);
        return userAccountMapper.selectOne(queryWrapper);
    }

    public UserAccount getUserByIdentityId(String identityId) {
        if (identityId == null || identityId.isEmpty()) {
            return null;
        }
        UserAccount user = getUserByStudentId(identityId);
        if (user != null) {
            return user;
        }
        user = getUserByFacultyId(identityId);
        if (user != null) {
            return user;
        }
        return getUserByUserId(identityId);
    }

    public boolean isStudentIdExists(String studentId) {
        if (studentId == null || studentId.isEmpty()) {
            return false;
        }
        QueryWrapper<UserAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId).eq("deleted", 0);
        return userAccountMapper.selectCount(queryWrapper) > 0;
    }

    public boolean isFacultyIdExists(String facultyId) {
        if (facultyId == null || facultyId.isEmpty()) {
            return false;
        }
        QueryWrapper<UserAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("faculty_id", facultyId).eq("deleted", 0);
        return userAccountMapper.selectCount(queryWrapper) > 0;
    }

    public boolean isUserIdExists(String userId) {
        if (userId == null || userId.isEmpty()) {
            return false;
        }
        QueryWrapper<UserAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_code", userId).eq("deleted", 0);
        return userAccountMapper.selectCount(queryWrapper) > 0;
    }

    public boolean registerUser(UserAccount userAccount) {
        QueryWrapper<UserAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userAccount.getUsername());
        if (userAccountMapper.selectOne(queryWrapper) != null) {
            return false;
        }

        PasswordValidator.ValidationResult validationResult = PasswordValidator.validate(userAccount.getPassword());
        if (!validationResult.isValid()) {
            logger.warn("密码强度验证失败: {}", validationResult.getMessage());
            throw new RuntimeException(validationResult.getMessage());
        }

        userAccount.setPassword(passwordEncoder.encode(userAccount.getPassword()));
        userAccount.setStatus(1);
        userAccount.setDeleted(0);
        userAccount.setCreateTime(new Date());
        userAccount.setUpdateTime(new Date());

        userAccountMapper.insert(userAccount);
        logger.info("用户注册成功: username={}", userAccount.getUsername());
        return true;
    }

    public String createUserWithNewFields(UserAccount userAccount) {
        if (userAccount.getUsername() != null && !userAccount.getUsername().isEmpty()) {
            QueryWrapper<UserAccount> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", userAccount.getUsername());
            if (userAccountMapper.selectOne(queryWrapper) != null) {
                throw new RuntimeException("用户名已存在");
            }
        }

        if (userAccount.getStudentId() != null && !userAccount.getStudentId().isEmpty()) {
            if (isStudentIdExists(userAccount.getStudentId())) {
                throw new RuntimeException("学号已存在");
            }
        }

        if (userAccount.getFacultyId() != null && !userAccount.getFacultyId().isEmpty()) {
            if (isFacultyIdExists(userAccount.getFacultyId())) {
                throw new RuntimeException("教职工号已存在");
            }
        }

        if (userAccount.getUserId() != null && !userAccount.getUserId().isEmpty()) {
            if (isUserIdExists(userAccount.getUserId())) {
                throw new RuntimeException("用户编号已存在");
            }
        }

        String initialPassword = generateInitialPassword();
        userAccount.setPassword(passwordEncoder.encode(initialPassword));
        userAccount.setStatus(1);
        userAccount.setDeleted(0);
        userAccount.setCreateTime(new Date());
        userAccount.setUpdateTime(new Date());

        if (userAccount.getViolationCount() == null) {
            userAccount.setViolationCount(0);
        }

        userAccountMapper.insert(userAccount);
        logger.info("用户创建成功: username={}, studentId={}, facultyId={}, userId={}", 
            userAccount.getUsername(), userAccount.getStudentId(), userAccount.getFacultyId(), userAccount.getUserId());
        
        return initialPassword;
    }

    // 根据用户名获取用户信息
    public UserAccount getUserByUsername(String username) {
        QueryWrapper<UserAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username).eq("deleted", 0);
        return userAccountMapper.selectOne(queryWrapper);
    }

    // 更新用户信息
    public boolean updateUser(UserAccount userAccount) {
        userAccount.setUpdateTime(new Date());
        int result = userAccountMapper.updateById(userAccount);
        return result > 0;
    }

    public boolean updatePassword(String username, String newPassword) {
        PasswordValidator.ValidationResult validationResult = PasswordValidator.validate(newPassword);
        if (!validationResult.isValid()) {
            logger.warn("密码强度验证失败: {}", validationResult.getMessage());
            throw new RuntimeException(validationResult.getMessage());
        }

        QueryWrapper<UserAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username).eq("deleted", 0);
        UserAccount userAccount = userAccountMapper.selectOne(queryWrapper);
        if (userAccount != null) {
            userAccount.setPassword(passwordEncoder.encode(newPassword));
            userAccount.setUpdateTime(new Date());
            userAccountMapper.updateById(userAccount);
            logger.info("用户密码更新成功: username={}", username);
            return true;
        }
        return false;
    }

    // 获取所有用户（分页）
    public IPage<UserAccount> getAllUsers(int page, int size) {
        Page<UserAccount> pageInfo = new Page<>(page, size);
        QueryWrapper<UserAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0);
        return userAccountMapper.selectPage(pageInfo, queryWrapper);
    }

    // 获取所有用户（分页，支持筛选）
    public IPage<UserAccount> getUsers(int page, int size, String keyword, String type, Integer status, String searchField) {
        Page<UserAccount> pageInfo = new Page<>(page, size);
        QueryWrapper<UserAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0);
        if (keyword != null && !keyword.isEmpty()) {
            if (searchField != null && !searchField.isEmpty() && SEARCH_FIELD_MAP.containsKey(searchField)) {
                String dbColumn = SEARCH_FIELD_MAP.get(searchField);
                if ("identity_id".equals(dbColumn)) {
                    queryWrapper.and(qw -> qw.like("student_id", keyword)
                            .or().like("faculty_id", keyword)
                            .or().like("user_code", keyword));
                } else {
                    queryWrapper.like(dbColumn, keyword);
                }
            } else {
                queryWrapper.and(qw -> qw.like("username", keyword)
                        .or().like("real_name", keyword)
                        .or().like("phone", keyword)
                        .or().like("email", keyword)
                        .or().like("student_id", keyword)
                        .or().like("faculty_id", keyword)
                        .or().like("user_code", keyword)
                        .or().like("campus", keyword)
                        .or().like("college", keyword)
                        .or().like("grade", keyword)
                        .or().like("class_name", keyword)
                        .or().like("counselor", keyword));
            }
        }
        if (type != null && !type.isEmpty()) {
            queryWrapper.eq("user_type", type);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        queryWrapper.orderByDesc("create_time");
        return userAccountMapper.selectPage(pageInfo, queryWrapper);
    }

    public IPage<UserAccount> getUsersByFilters(int page, int size, String keyword, String type, Integer status,
                                                 String campus, String college, String grade, String className) {
        Page<UserAccount> pageInfo = new Page<>(page, size);
        QueryWrapper<UserAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0);
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.and(qw -> qw.like("username", keyword)
                    .or().like("real_name", keyword)
                    .or().like("phone", keyword)
                    .or().like("email", keyword)
                    .or().like("student_id", keyword)
                    .or().like("faculty_id", keyword)
                    .or().like("user_code", keyword));
        }
        if (type != null && !type.isEmpty()) {
            queryWrapper.eq("user_type", type);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        if (campus != null && !campus.isEmpty()) {
            queryWrapper.eq("campus", campus);
        }
        if (college != null && !college.isEmpty()) {
            queryWrapper.like("college", college);
        }
        if (grade != null && !grade.isEmpty()) {
            queryWrapper.eq("grade", grade);
        }
        if (className != null && !className.isEmpty()) {
            queryWrapper.like("class_name", className);
        }
        queryWrapper.orderByDesc("create_time");
        return userAccountMapper.selectPage(pageInfo, queryWrapper);
    }

    // 根据ID获取用户
    public UserAccount getUserById(Long id) {
        return userAccountMapper.selectById(id);
    }

    // 更新用户状态
    public boolean updateUserStatus(Long id, int status) {
        UserAccount userAccount = userAccountMapper.selectById(id);
        if (userAccount != null) {
            userAccount.setStatus(status);
            userAccount.setUpdateTime(new Date());
            int result = userAccountMapper.updateById(userAccount);
            return result > 0;
        }
        return false;
    }

    // 删除用户（软删除）
    public boolean deleteUser(Long id) {
        UserAccount userAccount = userAccountMapper.selectById(id);
        if (userAccount != null) {
            userAccount.setDeleted(1);
            userAccount.setUpdateTime(new Date());
            int result = userAccountMapper.updateById(userAccount);
            return result > 0;
        }
        return false;
    }

    // 根据条件查询用户
    public List<UserAccount> searchUsers(String keyword) {
        QueryWrapper<UserAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0)
                .and(qw -> qw.like("username", keyword)
                        .or().like("real_name", keyword)
                        .or().like("phone", keyword)
                        .or().like("email", keyword)
                        .or().like("student_id", keyword)
                        .or().like("faculty_id", keyword)
                        .or().like("user_code", keyword)
                        .or().like("campus", keyword)
                        .or().like("college", keyword)
                        .or().like("grade", keyword)
                        .or().like("class_name", keyword)
                        .or().like("counselor", keyword));
        return userAccountMapper.selectList(queryWrapper);
    }

    public List<UserAccount> getUsersByCampus(String campus) {
        QueryWrapper<UserAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("campus", campus).eq("deleted", 0);
        return userAccountMapper.selectList(queryWrapper);
    }

    public List<UserAccount> getUsersByCollege(String college) {
        QueryWrapper<UserAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("college", college).eq("deleted", 0);
        return userAccountMapper.selectList(queryWrapper);
    }

    public List<UserAccount> getUsersByGrade(String grade) {
        QueryWrapper<UserAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("grade", grade).eq("deleted", 0);
        return userAccountMapper.selectList(queryWrapper);
    }

    public List<UserAccount> getUsersByClassName(String className) {
        QueryWrapper<UserAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("class_name", className).eq("deleted", 0);
        return userAccountMapper.selectList(queryWrapper);
    }

    public List<UserAccount> getUsersByCounselor(String counselor) {
        QueryWrapper<UserAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("counselor", counselor).eq("deleted", 0);
        return userAccountMapper.selectList(queryWrapper);
    }
}