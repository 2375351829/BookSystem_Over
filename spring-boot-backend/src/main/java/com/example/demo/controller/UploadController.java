package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Value("${upload.path:uploads}")
    private String uploadPath;

    @Value("${upload.url-prefix:/uploads/}")
    private String urlPrefix;

    private Path getUploadDir(String subDir) throws IOException {
        Path baseDir = Paths.get(System.getProperty("user.dir"), uploadPath);
        Path targetDir = baseDir.resolve(subDir);
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }
        return targetDir;
    }

    @PostMapping("/cover")
    public ResponseEntity<?> uploadCover(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 400);
            error.put("message", "请选择要上传的文件");
            return ResponseEntity.badRequest().body(error);
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            String newFilename = UUID.randomUUID().toString() + extension;
            
            Path uploadDir = getUploadDir("covers");
            Path filePath = uploadDir.resolve(newFilename);
            file.transferTo(filePath.toFile());
            
            String fileUrl = urlPrefix + "covers/" + newFilename;
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "上传成功");
            response.put("url", fileUrl);
            response.put("filename", newFilename);
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("message", "文件上传失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PostMapping("/avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 400);
            error.put("message", "请选择要上传的文件");
            return ResponseEntity.badRequest().body(error);
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            String newFilename = UUID.randomUUID().toString() + extension;
            
            Path uploadDir = getUploadDir("avatars");
            Path filePath = uploadDir.resolve(newFilename);
            file.transferTo(filePath.toFile());
            
            String fileUrl = urlPrefix + "avatars/" + newFilename;
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "上传成功");
            response.put("url", fileUrl);
            response.put("filename", newFilename);
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("message", "文件上传失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
