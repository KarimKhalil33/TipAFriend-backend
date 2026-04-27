package com.tipafriend.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * Loads .env file at application startup before Spring initializes properties.
 * This ensures that environment variables from .env are available for @ConfigurationProperties
 */
public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            // Load .env file if it exists
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();

            // Create a map of all properties from .env
            Map<String, Object> properties = new HashMap<>();
            dotenv.entries().forEach(entry ->
                properties.put(entry.getKey(), entry.getValue())
            );

            // Add to Spring's property sources (first so env vars take precedence)
            if (!properties.isEmpty()) {
                environment.getPropertySources()
                        .addFirst(new MapPropertySource("dotenv", properties));
                System.out.println("✅ .env file loaded successfully with " + properties.size() + " properties");
                System.out.println("✅ STRIPE_SECRET_KEY loaded: " + (properties.containsKey("STRIPE_SECRET_KEY") ? "YES" : "NO"));
            } else {
                System.out.println("⚠️  .env file exists but is empty");
            }
        } catch (Exception e) {
            // .env file not found or error loading - not critical
            System.out.println("⚠️  No .env file found or error loading it. Will use system environment variables or defaults.");
            e.printStackTrace();
        }
    }
}




