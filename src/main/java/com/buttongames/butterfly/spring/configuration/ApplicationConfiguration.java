package com.buttongames.butterfly.spring.configuration;

import com.buttongames.butterfly.util.PathUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Bean config class for the top-level application.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Configuration
@ComponentScan({"com.buttongames.butterfly.spring.configuration"})
public class ApplicationConfiguration {

    @Bean
    public PathUtils pathUtils() {
        return new PathUtils();
    }
}
