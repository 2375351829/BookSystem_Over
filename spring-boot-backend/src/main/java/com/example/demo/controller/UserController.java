package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.annotation.OperationLog;
import com.example.demo.model.UserAccount;
import com.example.demo.repository.UserAccountMapper;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.UserService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserAccountMapper userAccountMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserAccount userAccount) {
        if (userService.registerUser(userAccount)) {
            return new ResponseEntity<>("User registered successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Username already exists", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        UserAccount userAccount = userService.getUserByUsername(username);

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("user", userAccount);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAccount userAccount = userService.getUserByUsername(username);
        if (userAccount != null) {
            return new ResponseEntity<>(userAccount, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateCurrentUser(@RequestBody UserAccount userAccount) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAccount existingUser = userService.getUserByUsername(username);
        if (existingUser != null) {
            userAccount.setId(existingUser.getId());
            userAccount.setUsername(username);
            userAccount.setPassword(existingUser.getPassword());
            if (userService.updateUser(userAccount)) {
                return new ResponseEntity<>("User updated successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Failed to update user", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/me/password")
    public ResponseEntity<?> updatePassword(@RequestBody Map<String, String> passwordRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String newPassword = passwordRequest.get("newPassword");
        if (userService.updatePassword(username, newPassword)) {
            return new ResponseEntity<>("Password updated successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed to update password", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/list")
    public ResponseEntity<?> getAllUsers(@RequestParam int page, @RequestParam int size) {
        IPage<UserAccount> users = userService.getAllUsers(page, size);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String searchField) {
        IPage<UserAccount> users = userService.getUsers(page, size, keyword, type, status, searchField);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        UserAccount userAccount = userService.getUserById(id);
        if (userAccount != null) {
            return new ResponseEntity<>(userAccount, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateUserStatusNew(@PathVariable Long id, @RequestBody Map<String, Integer> statusRequest) {
        int status = statusRequest.get("status");
        if (userService.updateUserStatus(id, status)) {
            return new ResponseEntity<>("User status updated successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed to update user status", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/admin/{id}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Integer> statusRequest) {
        int status = statusRequest.get("status");
        if (userService.updateUserStatus(id, status)) {
            return new ResponseEntity<>("User status updated successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed to update user status", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/admin/{id}")
    @OperationLog(module = "用户管理", operation = "删除用户", description = "管理员删除用户")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (userService.deleteUser(id)) {
            return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed to delete user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/search")
    public ResponseEntity<?> searchUsers(@RequestParam String keyword) {
        List<UserAccount> users = userService.searchUsers(keyword);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping
    @OperationLog(module = "用户管理", operation = "创建用户", description = "管理员创建新用户")
    public ResponseEntity<?> createUser(@RequestBody UserAccount userAccount) {
        String identityId = userAccount.getStudentId() != null ? userAccount.getStudentId() :
                           userAccount.getFacultyId() != null ? userAccount.getFacultyId() :
                           userAccount.getUserId();

        if (identityId != null && !identityId.isEmpty()) {
            UserAccount existing = userService.getUserByIdentityId(identityId);
            if (existing != null) {
                return new ResponseEntity<>("身份标识已存在", HttpStatus.BAD_REQUEST);
            }
        }

        if (userAccount.getPassword() == null || userAccount.getPassword().isEmpty()) {
            String initialPassword = userService.generateInitialPassword();
            userAccount.setPassword(passwordEncoder.encode(initialPassword));
        } else {
            userAccount.setPassword(passwordEncoder.encode(userAccount.getPassword()));
        }

        if (userAccount.getUserType() == null || userAccount.getUserType().isEmpty()) {
            userAccount.setUserType("READER");
        }
        userAccount.setRole("ROLE_" + userAccount.getUserType());
        userAccount.setStatus(1);
        userAccount.setDeleted(0);
        if (userAccount.getLanguage() == null) {
            userAccount.setLanguage("zh-CN");
        }

        int result = userAccountMapper.insert(userAccount);
        if (result > 0) {
            return new ResponseEntity<>(userAccount, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("创建用户失败", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    @OperationLog(module = "用户管理", operation = "更新用户", description = "管理员更新用户信息")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserAccount userAccount) {
        UserAccount existingUser = userService.getUserById(id);
        if (existingUser == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        userAccount.setId(id);
        userAccount.setPassword(existingUser.getPassword());
        if (userService.updateUser(userAccount)) {
            return new ResponseEntity<>(userAccount, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed to update user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/reset-password")
    public ResponseEntity<?> resetUserPassword(@PathVariable Long id) {
        UserAccount user = userService.getUserById(id);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        String defaultPassword = "123456";
        if (userService.updatePassword(user.getUsername(), defaultPassword)) {
            return new ResponseEntity<>("Password reset successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed to reset password", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/batch/status")
    public ResponseEntity<?> batchUpdateStatus(@RequestBody Map<String, Object> request) {
        List<Long> ids = (List<Long>) request.get("ids");
        Integer status = (Integer) request.get("status");
        
        if (ids == null || ids.isEmpty() || status == null) {
            return new ResponseEntity<>("Invalid request", HttpStatus.BAD_REQUEST);
        }

        int updated = 0;
        for (Long id : ids) {
            if (userService.updateUserStatus(id, status)) {
                updated++;
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("updated", updated);
        result.put("message", "成功更新 " + updated + " 个用户状态");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/batch")
    public ResponseEntity<?> batchDeleteUsers(@RequestBody Map<String, Object> request) {
        List<Long> ids = (List<Long>) request.get("ids");
        
        if (ids == null || ids.isEmpty()) {
            return new ResponseEntity<>("Invalid request", HttpStatus.BAD_REQUEST);
        }

        int deleted = 0;
        for (Long id : ids) {
            if (userService.deleteUser(id)) {
                deleted++;
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("deleted", deleted);
        result.put("message", "成功删除 " + deleted + " 个用户");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/import/template")
    public ResponseEntity<?> downloadImportTemplate() {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("用户导入模板");

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            String[] headers = {"身份标识(必填)", "真实姓名(必填)", "用户类型(必填)", "手机号", "邮箱", 
                               "校区(必填)", "院系(必填)", "年级(学生)", "班级(学生)", "辅导员(学生)"};
            
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 5000);
            }

            Row exampleRow1 = sheet.createRow(1);
            exampleRow1.createCell(0).setCellValue("STU20240001");
            exampleRow1.createCell(1).setCellValue("张三");
            exampleRow1.createCell(2).setCellValue("STUDENT");
            exampleRow1.createCell(3).setCellValue("13800138000");
            exampleRow1.createCell(4).setCellValue("zhangsan@example.com");
            exampleRow1.createCell(5).setCellValue("主校区");
            exampleRow1.createCell(6).setCellValue("计算机学院");
            exampleRow1.createCell(7).setCellValue("2024级");
            exampleRow1.createCell(8).setCellValue("软件工程2401班");
            exampleRow1.createCell(9).setCellValue("李老师");

            Row exampleRow2 = sheet.createRow(2);
            exampleRow2.createCell(0).setCellValue("FAC20240001");
            exampleRow2.createCell(1).setCellValue("李教授");
            exampleRow2.createCell(2).setCellValue("TEACHER");
            exampleRow2.createCell(3).setCellValue("13900139000");
            exampleRow2.createCell(4).setCellValue("lilaoshi@example.com");
            exampleRow2.createCell(5).setCellValue("主校区");
            exampleRow2.createCell(6).setCellValue("计算机学院");

            Row exampleRow3 = sheet.createRow(3);
            exampleRow3.createCell(0).setCellValue("USR20240001");
            exampleRow3.createCell(1).setCellValue("王读者");
            exampleRow3.createCell(2).setCellValue("READER");
            exampleRow3.createCell(3).setCellValue("13700137000");
            exampleRow3.createCell(4).setCellValue("wang@example.com");
            exampleRow3.createCell(5).setCellValue("主校区");
            exampleRow3.createCell(6).setCellValue("图书馆");

            Row noteRow = sheet.createRow(5);
            noteRow.createCell(0).setCellValue("说明：");
            CellStyle noteStyle = workbook.createCellStyle();
            noteStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            noteStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            noteRow.getCell(0).setCellStyle(noteStyle);

            sheet.createRow(6).createCell(0).setCellValue("1. 用户类型: STUDENT-学生, TEACHER-教师, READER-普通读者, VIP-VIP读者");
            sheet.createRow(7).createCell(0).setCellValue("2. 学生必须填写: 身份标识、姓名、类型、校区、院系、年级、班级");
            sheet.createRow(8).createCell(0).setCellValue("3. 教师必须填写: 身份标识、姓名、类型、校区、院系");
            sheet.createRow(9).createCell(0).setCellValue("4. 密码将自动生成，首次登录后请修改密码");

            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            workbook.write(out);
            workbook.close();

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=user-import-template.xlsx")
                    .contentType(org.springframework.http.MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(out.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("message", "模板生成失败: " + e.getMessage()));
        }
    }

    @PostMapping("/import/parse")
    public ResponseEntity<?> parseImportFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "文件不能为空"));
            }

            String filename = file.getOriginalFilename();
            if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls") && !filename.endsWith(".csv"))) {
                return ResponseEntity.badRequest().body(Map.of("message", "仅支持 .xlsx、.xls、.csv 格式"));
            }

            InputStream inputStream = file.getInputStream();
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            List<Map<String, Object>> users = new ArrayList<>();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Map<String, Object> user = new LinkedHashMap<>();
                user.put("identityId", getCellValue(row.getCell(0)));
                user.put("realName", getCellValue(row.getCell(1)));
                user.put("userType", getCellValue(row.getCell(2)));
                user.put("phone", getCellValue(row.getCell(3)));
                user.put("email", getCellValue(row.getCell(4)));
                user.put("campus", getCellValue(row.getCell(5)));
                user.put("college", getCellValue(row.getCell(6)));
                user.put("grade", getCellValue(row.getCell(7)));
                user.put("className", getCellValue(row.getCell(8)));
                user.put("counselor", getCellValue(row.getCell(9)));
                users.add(user);
            }

            workbook.close();
            inputStream.close();

            return ResponseEntity.ok(users);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("message", "文件解析失败: " + e.getMessage()));
        }
    }

    @PostMapping("/import/execute")
    @OperationLog(module = "用户管理", operation = "批量导入用户", description = "管理员批量导入用户数据")
    public ResponseEntity<?> executeImport(@RequestBody Map<String, Object> request) {
        try {
            List<Map<String, Object>> users = (List<Map<String, Object>>) request.get("users");
            
            if (users == null || users.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "没有可导入的数据"));
            }

            int success = 0;
            int fail = 0;

            for (Map<String, Object> userData : users) {
                try {
                    String identityId = (String) userData.get("identityId");
                    if (identityId == null || identityId.isEmpty()) {
                        fail++;
                        continue;
                    }

                    String userType = (String) userData.get("userType");
                    if (userType == null || userType.isEmpty()) {
                        userType = "READER";
                    }

                    UserAccount existing = userService.getUserByIdentityId(identityId);
                    if (existing != null) {
                        fail++;
                        continue;
                    }

                    UserAccount user = new UserAccount();
                    String initialPassword = userService.generateInitialPassword();

                    user.setUsername(identityId);
                    user.setRealName((String) userData.get("realName"));
                    user.setPhone((String) userData.get("phone"));
                    user.setEmail((String) userData.get("email"));
                    user.setUserType(userType);
                    user.setPassword(passwordEncoder.encode(initialPassword));
                    user.setRole("ROLE_" + userType);
                    user.setStatus(1);
                    user.setDeleted(0);
                    user.setLanguage("zh-CN");

                    user.setCampus((String) userData.get("campus"));
                    user.setCollege((String) userData.get("college"));
                    user.setGrade((String) userData.get("grade"));
                    user.setClassName((String) userData.get("className"));
                    user.setCounselor((String) userData.get("counselor"));

                    switch (userType) {
                        case "STUDENT":
                            user.setStudentId(identityId);
                            break;
                        case "TEACHER":
                            user.setFacultyId(identityId);
                            break;
                        default:
                            user.setUserId(identityId);
                            break;
                    }

                    userAccountMapper.insert(user);
                    success++;
                } catch (Exception e) {
                    e.printStackTrace();
                    fail++;
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("success", success);
            result.put("fail", fail);
            result.put("message", "导入完成：成功 " + success + " 条，失败 " + fail + " 条");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("message", "导入失败: " + e.getMessage()));
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                double value = cell.getNumericCellValue();
                return value == Math.floor(value) ? String.valueOf((long) value) : String.valueOf(value);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }
}
