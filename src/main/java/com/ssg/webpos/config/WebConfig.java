package com.ssg.webpos.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry
                .addMapping("/api/**")
                .allowedOrigins("http://localhost:3000", "http://3.36.45.238:3000", "http://3.34.250.96:3000", "http://localhost:3001");
    }
}
