package com.anthill.ofhelperredditmvc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:3000",
                        "http://127.0.0.1:3000",
                        "https://redditer-app.herokuapp.com",
                        "https://reddit-client-bot.ngrok.io",
                        "https://redditer-tg-bot.herokuapp.com")
                .allowCredentials(true)
                .allowedMethods("POST", "GET", "PUT", "DELETE", "PATCH")
                .allowedHeaders("*");
    }
}
