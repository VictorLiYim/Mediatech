package com.users.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Autorise le front Vue.js (par défaut http://localhost:5173) à appeler l'API. */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

	private final String allowedOrigin;

	public CorsConfig(@Value("${app.cors.allowed-origin:http://localhost:5173}") String allowedOrigin) {
		this.allowedOrigin = allowedOrigin;
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOrigins(allowedOrigin)
				.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
				.allowedHeaders("*");
	}
}
