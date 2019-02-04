package com.buttongames.butterflywebui.spring.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Bean config class for HTTP-related classes.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Configuration
@ComponentScan({"com.buttongames.butterflywebui.spring.configuration",
        "com.buttongames.butterflydao.spring.configuration"})
@PropertySource("classpath:butterflyserver.properties")
public class HttpConfiguration {
}
