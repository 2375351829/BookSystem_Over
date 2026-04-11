package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

@RestController
@RequestMapping("/api/logs/stream")
public class LogStreamController {

    @Value("${logging.file.path:logs}")
    private String logPath;

    @Value("${logging.file.name:spring.log}")
    private String logFileName;

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, Boolean> pausedStatus = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamLogs(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false, defaultValue = "100") int initialLines) {
        
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        String sessionId = UUID.randomUUID().toString();
        
        emitters.put(sessionId, emitter);
        pausedStatus.put(sessionId, false);

        emitter.onCompletion(() -> cleanup(sessionId));
        emitter.onTimeout(() -> cleanup(sessionId));
        emitter.onError(e -> cleanup(sessionId));

        executor.execute(() -> {
            try {
                Path logFile = getLogFile();
                if (logFile == null || !Files.exists(logFile)) {
                    emitter.send(SseEmitter.event()
                        .name("error")
                        .data("日志文件不存在: " + logPath + "/" + logFileName));
                    emitter.complete();
                    return;
                }

                sendInitialLines(emitter, logFile, initialLines, filter);
                
                tailLogFile(emitter, sessionId, logFile, filter);
                
            } catch (Exception e) {
                try {
                    emitter.send(SseEmitter.event()
                        .name("error")
                        .data("读取日志失败: " + e.getMessage()));
                } catch (IOException ignored) {}
                emitter.complete();
            }
        });

        return emitter;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/pause/{sessionId}")
    public ResponseEntity<Map<String, Object>> pauseStream(@PathVariable String sessionId) {
        if (!emitters.containsKey(sessionId)) {
            return ResponseEntity.notFound().build();
        }
        pausedStatus.put(sessionId, true);
        return ResponseEntity.ok(Map.of("status", "paused", "sessionId", sessionId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/resume/{sessionId}")
    public ResponseEntity<Map<String, Object>> resumeStream(@PathVariable String sessionId) {
        if (!emitters.containsKey(sessionId)) {
            return ResponseEntity.notFound().build();
        }
        pausedStatus.put(sessionId, false);
        return ResponseEntity.ok(Map.of("status", "resumed", "sessionId", sessionId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> closeStream(@PathVariable String sessionId) {
        SseEmitter emitter = emitters.get(sessionId);
        if (emitter != null) {
            emitter.complete();
            cleanup(sessionId);
        }
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getLogStatus() {
        Path logFile = getLogFile();
        Map<String, Object> status = new HashMap<>();
        
        if (logFile != null && Files.exists(logFile)) {
            try {
                status.put("exists", true);
                status.put("path", logFile.toString());
                status.put("size", Files.size(logFile));
                status.put("lastModified", Files.getLastModifiedTime(logFile).toString());
            } catch (IOException e) {
                status.put("exists", false);
                status.put("error", e.getMessage());
            }
        } else {
            status.put("exists", false);
            status.put("configuredPath", logPath + "/" + logFileName);
        }
        
        return ResponseEntity.ok(status);
    }

    private Path getLogFile() {
        Path path = Paths.get(logPath, logFileName);
        if (Files.exists(path)) {
            return path;
        }
        
        Path alternativePath = Paths.get("logs", "spring.log");
        if (Files.exists(alternativePath)) {
            return alternativePath;
        }
        
        Path springLogPath = Paths.get("spring.log");
        if (Files.exists(springLogPath)) {
            return springLogPath;
        }
        
        return path;
    }

    private void sendInitialLines(SseEmitter emitter, Path logFile, int maxLines, String filter) throws IOException {
        List<String> lines = new ArrayList<>();
        
        try (RandomAccessFile raf = new RandomAccessFile(logFile.toFile(), "r")) {
            long fileLength = raf.length();
            long position = fileLength - 1;
            int lineCount = 0;
            StringBuilder currentLine = new StringBuilder();
            
            while (position >= 0 && lineCount < maxLines) {
                raf.seek(position);
                int ch = raf.read();
                
                if (ch == '\n') {
                    if (currentLine.length() > 0) {
                        String line = currentLine.reverse().toString();
                        if (filter == null || filter.isEmpty() || line.toLowerCase().contains(filter.toLowerCase())) {
                            lines.add(0, line);
                            lineCount++;
                        }
                        currentLine = new StringBuilder();
                    }
                } else {
                    currentLine.append((char) ch);
                }
                position--;
            }
            
            if (currentLine.length() > 0 && lineCount < maxLines) {
                String line = currentLine.reverse().toString();
                if (filter == null || filter.isEmpty() || line.toLowerCase().contains(filter.toLowerCase())) {
                    lines.add(0, line);
                }
            }
        }
        
        for (String line : lines) {
            emitter.send(SseEmitter.event()
                .name("log")
                .data(line));
        }
        
        emitter.send(SseEmitter.event()
            .name("connected")
            .data(Map.of("sessionId", emitters.keySet().iterator().next(), "initialLines", lines.size())));
    }

    private void tailLogFile(SseEmitter emitter, String sessionId, Path logFile, String filter) {
        try (RandomAccessFile raf = new RandomAccessFile(logFile.toFile(), "r")) {
            raf.seek(raf.length());
            
            while (emitters.containsKey(sessionId)) {
                Boolean paused = pausedStatus.get(sessionId);
                if (paused != null && paused) {
                    Thread.sleep(100);
                    continue;
                }
                
                long fileLength = raf.length();
                if (fileLength < raf.getFilePointer()) {
                    raf.seek(0);
                }
                
                if (fileLength > raf.getFilePointer()) {
                    String line = raf.readLine();
                    if (line != null) {
                        line = new String(line.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                        
                        if (filter == null || filter.isEmpty() || line.toLowerCase().contains(filter.toLowerCase())) {
                            emitter.send(SseEmitter.event()
                                .name("log")
                                .data(line));
                        }
                    }
                } else {
                    Thread.sleep(100);
                }
            }
        } catch (Exception e) {
            try {
                emitter.send(SseEmitter.event()
                    .name("error")
                    .data("监控日志失败: " + e.getMessage()));
            } catch (IOException ignored) {}
        }
    }

    private void cleanup(String sessionId) {
        emitters.remove(sessionId);
        pausedStatus.remove(sessionId);
    }
}
