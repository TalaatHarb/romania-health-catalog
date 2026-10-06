package net.talaatharb.healthcatalog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class HelperBeanConfigurations {

	@Bean
	ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		return objectMapper;
	}
}