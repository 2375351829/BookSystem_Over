package com.example.demo.aspect;

import com.example.demo.annotation.OperationLog;
import com.example.demo.model.UserAccount;
import com.example.demo.repository.UserAccountMapper;
import com.example.demo.service.LogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class OperationLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(OperationLogAspect.class);

    @Autowired
    private LogService logService;

    @Autowired
    private UserAccountMapper userAccountMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Around("@annotation(com.example.demo.annotation.OperationLog)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        Object result = null;
        Exception exception = null;

        try {
            result = point.proceed();
            return result;
        } catch (Exception e) {
            exception = e;
            throw e;
        } finally {
            long executeTime = System.currentTimeMillis() - beginTime;
            saveLog(point, result, executeTime, exception);
        }
    }

    private void saveLog(ProceedingJoinPoint joinPoint, Object result, long executeTime, Exception exception) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            OperationLog operationLog = method.getAnnotation(OperationLog.class);

            if (operationLog == null) {
                return;
            }

            com.example.demo.model.OperationLog log = new com.example.demo.model.OperationLog();

            log.setModule(operationLog.module());
            log.setOperation(operationLog.operation());
            log.setMethod(joinPoint.getTarget().getClass().getName() + "." + method.getName());

            String params = getRequestParams(joinPoint);
            log.setParams(params);

            if (result != null) {
                try {
                    String resultStr = objectMapper.writeValueAsString(result);
                    if (resultStr.length() > 2000) {
                        resultStr = resultStr.substring(0, 2000) + "...";
                    }
                    log.setResult(resultStr);
                } catch (Exception e) {
                    log.setResult("无法序列化结果");
                }
            }

            HttpServletRequest request = getRequest();
            if (request != null) {
                log.setIpAddress(getIpAddress(request));
                log.setUserAgent(request.getHeader("User-Agent"));
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
                String username = authentication.getName();
                UserAccount user = userAccountMapper.selectByUsername(username);
                if (user != null) {
                    log.setUserId(user.getId());
                    log.setUsername(user.getUsername());
                }
            }

            if (exception != null) {
                log.setStatus(0);
                String errorMsg = exception.getMessage();
                if (errorMsg != null && errorMsg.length() > 500) {
                    errorMsg = errorMsg.substring(0, 500);
                }
                log.setErrorMessage(errorMsg);
            } else {
                log.setStatus(1);
            }

            log.setExecuteTime(executeTime);
            log.setCreateTime(new Date());

            logService.saveLog(log);

        } catch (Exception e) {
            logger.error("保存操作日志失败", e);
        }
    }

    private String getRequestParams(ProceedingJoinPoint joinPoint) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String[] paramNames = signature.getParameterNames();
            Object[] paramValues = joinPoint.getArgs();

            if (paramNames == null || paramNames.length == 0) {
                return "";
            }

            Map<String, Object> params = new HashMap<>();
            for (int i = 0; i < paramNames.length; i++) {
                String paramName = paramNames[i];
                Object paramValue = paramValues[i];

                if (paramValue instanceof MultipartFile) {
                    params.put(paramName, "[文件]");
                } else if (paramValue instanceof HttpServletRequest) {
                    params.put(paramName, "[Request]");
                } else {
                    params.put(paramName, paramValue);
                }
            }

            String paramsStr = objectMapper.writeValueAsString(params);
            if (paramsStr.length() > 2000) {
                paramsStr = paramsStr.substring(0, 2000) + "...";
            }
            return paramsStr;
        } catch (Exception e) {
            logger.error("获取请求参数失败", e);
            return "";
        }
    }

    private HttpServletRequest getRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
