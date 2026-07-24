package com.quiverly.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${app.uploads}") // get upload directory path from app.prop
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Path.of(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadPath); //create directory or throw error
        } catch (IOException e) {
            throw new RuntimeException("Unable to create upload directory :/", e);
        }
        // maps the public URL pattern to physical filesystem location
        registry.addResourceHandler("/uploads/**").addResourceLocations(uploadPath.toUri().toString());
    }
}
