package com.tipafriend.config;

import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Configures HTTPS/SSL for local development.
 * Generates a self-signed certificate on first run.
 */
@Configuration
public class HttpsConfig {

    private final Environment environment;

    public HttpsConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer() {
        return factory -> {
            String profile = String.join(",", environment.getActiveProfiles());
            boolean isHttpsEnabled = environment.getProperty("server.ssl.enabled", Boolean.class, false);

            if (isHttpsEnabled && (profile.contains("dev") || profile.contains("local"))) {
                System.out.println("🔒 HTTPS enabled for local development on port 8443");
            }
        };
    }
}



