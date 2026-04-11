package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiVersionConfig implements WebMvcConfigurer {

    public static final String API_VERSION = "v1";
    public static final String API_PREFIX = "/api";

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // Disabled version prefix - controllers now use their original paths
    }
}
