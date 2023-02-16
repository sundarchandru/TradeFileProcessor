package com.trade.file.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
public @Data class ConfigForTrade {
    private String baseDirectory;
    private String receivedDirectory;
    private String errorDirectory;
    private String doneDirectory;
    private String outputDirectory;

}
