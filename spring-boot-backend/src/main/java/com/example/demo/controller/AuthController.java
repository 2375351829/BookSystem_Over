package com.example.demo.controller;

import com.example.demo.annotation.OperationLog;
import com.example.demo.model.RefreshToken;
import com.example.demo.model.UserAccount;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.RefreshTokenService;
import com.example.demo.service.UserService;
import com.example.demo.util.FormValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private static final Map<String, String> CAPTCHA_CACHE = new HashMap<>();

    public AuthController(AuthenticationManager authenticationManager,
                        UserService userService,
                        JwtTokenProvider tokenProvider,
                        RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.tokenProvider = tokenProvider;
        this.refreshTokenService = refreshTokenService;
    }

    @GetMapping("/captcha")
    public ResponseEntity<?> getCaptcha() throws IOException {
        String captchaText = generateCaptchaText();
        String captchaId = String.valueOf(System.currentTimeMillis());
        CAPTCHA_CACHE.put(captchaId, captchaText);

        BufferedImage image = generateCaptchaImage(captchaText);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] imageBytes = baos.toByteArray();

        String base64Image = "data:image/png;base64," + java.util.Base64.getEncoder().encodeToString(imageBytes);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("captchaId", captchaId);
        response.put("captchaImage", base64Image);

        return ResponseEntity.ok(response);
    }

    private String generateCaptchaText() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder captchaText = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            captchaText.append(chars.charAt(random.nextInt(chars.length())));
        }
        return captchaText.toString();
    }

    private BufferedImage generateCaptchaImage(String captchaText) {
        int width = 120;
        int height = 40;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        g.setFont(new Font("Arial", Font.BOLD, 28));
        Random random = new Random();
        for (int i = 0; i < captchaText.length(); i++) {
            g.setColor(new Color(random.nextInt(150), random.nextInt(150), random.nextInt(150)));
            int x = 20 + i * 25;
            int y = 25 + random.nextInt(10);
            g.drawString(String.valueOf(captchaText.charAt(i)), x, y);
        }

        for (int i = 0; i < 3; i++) {
            g.setColor(new Color(random.nextInt(200), random.nextInt(200), random.nextInt(200)));
            g.drawLine(random.nextInt(width), random.nextInt(height), random.nextInt(width), random.nextInt(height));
        }

        g.dispose();
        return image;
    }

    @PostMapping("/login")
    @OperationLog(module = "认证管理", operation = "登录", description = "用户登录系统")
    public ResponseEntity<?> loginUser(@RequestBody UserAccount loginRequest) {
        log.info("Login attempt for user: {}", loginRequest.getUsername());
        
        FormValidator.ValidationResult formValidation = FormValidator.validateLogin(
            loginRequest.getUsername(),
            loginRequest.getPassword()
        );
        
        if (!formValidation.isValid()) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", formValidation.getErrorMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String accessToken = tokenProvider.generateToken(authentication);

            UserAccount userAccount = userService.getUserByUsername(authentication.getName());
            
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userAccount.getId());

            String identityId = null;
            String identityType = null;
            if (userAccount.getStudentId() != null && !userAccount.getStudentId().isEmpty()) {
                identityId = userAccount.getStudentId();
                identityType = "student";
            } else if (userAccount.getFacultyId() != null && !userAccount.getFacultyId().isEmpty()) {
                identityId = userAccount.getFacultyId();
                identityType = "faculty";
            } else if (userAccount.getUserId() != null && !userAccount.getUserId().isEmpty()) {
                identityId = userAccount.getUserId();
                identityType = "reader";
            }

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "Login successful");
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken.getToken());
            response.put("tokenType", "Bearer");
            response.put("user", userAccount);
            response.put("identityId", identityId);
            response.put("identityType", identityType);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Login failed for user: {}, error: {}", loginRequest.getUsername(), e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("code", 401);
            response.put("message", "Login failed: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String requestRefreshToken = request.get("refreshToken");
        
        if (requestRefreshToken == null || requestRefreshToken.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", "Refresh token is required");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        RefreshToken refreshToken = refreshTokenService.findByToken(requestRefreshToken);
        if (refreshToken == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 401);
            response.put("message", "Invalid refresh token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        
        if (!refreshTokenService.verifyExpiration(refreshToken)) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 401);
            response.put("message", "Refresh token has expired");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        
        UserAccount user = userService.getUserById(refreshToken.getUserId());
        if (user == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 404);
            response.put("message", "User not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        
        String accessToken = tokenProvider.generateTokenFromUsername(user.getUsername());
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("accessToken", accessToken);
        response.put("tokenType", "Bearer");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/logout")
    @OperationLog(module = "认证管理", operation = "登出", description = "用户退出系统")
    public ResponseEntity<?> logoutUser(@RequestBody(required = false) Map<String, String> request) {
        if (request != null) {
            String requestRefreshToken = request.get("refreshToken");
            if (requestRefreshToken != null && !requestRefreshToken.isEmpty()) {
                refreshTokenService.deleteByToken(requestRefreshToken);
            }
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "Logout successful");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/info")
    public ResponseEntity<?> getUserInfo() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAccount userAccount = userService.getUserByUsername(username);
        if (userAccount != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("data", userAccount);
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 404);
            response.put("message", "User not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}