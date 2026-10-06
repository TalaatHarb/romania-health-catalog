package net.talaatharb.healthcatalog.model;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Stores a map of attributes as a JSON string column
 */
@Converter
public class JsonMapConverter implements AttributeConverter<Map<String, Object>, String> {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private static final TypeReference<LinkedHashMap<String, Object>> MAP_TYPE = new TypeReference<>() {
	};

	@Override
	public String convertToDatabaseColumn(Map<String, Object> attribute) {
		if (attribute == null) {
			return null;
		}
		try {
			return OBJECT_MAPPER.writeValueAsString(attribute);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException("Unable to serialize item details", e);
		}
	}

	@Override
	public Map<String, Object> convertToEntityAttribute(String dbData) {
		if (dbData == null || dbData.isBlank()) {
			return new LinkedHashMap<>();
		}
		try {
			return OBJECT_MAPPER.readValue(dbData, MAP_TYPE);
		} catch (IOException e) {
			throw new IllegalStateException("Unable to deserialize item details", e);
		}
	}
}
