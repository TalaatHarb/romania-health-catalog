package net.talaatharb.healthcatalog.mapper;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import net.talaatharb.healthcatalog.model.CatalogItemEntity;
import net.talaatharb.healthcatalog.model.CatalogItemType;

/**
 * Converts any XML catalog object into a generic {@link CatalogItemEntity},
 * keeping all of its attributes as details.
 */
@Component
public class CatalogItemXmlMapper {

	private static final TypeReference<LinkedHashMap<String, Object>> MAP_TYPE = new TypeReference<>() {
	};

	private final ObjectMapper objectMapper;

	public CatalogItemXmlMapper() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		objectMapper.setDateFormat(dateFormat);
	}

	public CatalogItemEntity fromXmlObject(CatalogItemType type, Object xmlObject) {
		Map<String, Object> details = objectMapper.convertValue(xmlObject, MAP_TYPE);
		CatalogItemEntity entity = new CatalogItemEntity();
		entity.setType(type);
		entity.setDetails(details);
		entity.setCode(asText(details.get(type.getCodeProperty())));
		entity.setName(asText(details.get(type.getNameProperty())));
		return entity;
	}

	public List<CatalogItemEntity> fromXmlObjects(CatalogItemType type, List<?> xmlObjects) {
		List<CatalogItemEntity> entities = new ArrayList<>(xmlObjects.size());
		for (Object xmlObject : xmlObjects) {
			if (xmlObject != null) {
				entities.add(fromXmlObject(type, xmlObject));
			}
		}
		return entities;
	}

	static String asText(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Double || value instanceof Float) {
			return new BigDecimal(value.toString()).stripTrailingZeros().toPlainString();
		}
		return value.toString();
	}
}
