package net.talaatharb.healthcatalog.utils;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class XMLUtils {
	
	public static final ObjectMapper createXMLObjectMapper() {
		JacksonXmlModule xmlModule = new JacksonXmlModule();
		xmlModule.setDefaultUseWrapper(false);
		ObjectMapper objectMapper = new XmlMapper(xmlModule);
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		return objectMapper;
	}
	
	public static final <T> T fromXmlString(String xml, Class<T> valueType) throws IOException {
        var objectMapper = createXMLObjectMapper();
        return objectMapper.readValue(xml, valueType);
    }

}
