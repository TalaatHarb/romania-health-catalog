package net.talaatharb.healthcatalog.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS for the SPA, which is served from a different origin than the API.
 * The allowed origins come from CORS_ALLOWED_ORIGINS (comma separated, patterns allowed), '*' by default.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

	static final long MAX_AGE_SECONDS = 3600;

	private final List<String> allowedOrigins;

	public CorsConfig(@Value("${health-catalog.cors.allowed-origins}") List<String> allowedOrigins) {
		this.allowedOrigins = allowedOrigins.stream().map(String::trim).filter(origin -> !origin.isEmpty()).toList();
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOriginPatterns(allowedOrigins.toArray(String[]::new))
			.allowedMethods("GET", "HEAD", "POST", "OPTIONS")
			// includes the custom 'uploadSecret' header
			.allowedHeaders("*")
			.maxAge(MAX_AGE_SECONDS);
	}
}
