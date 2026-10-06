package net.talaatharb.healthcatalog.api;

import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.InputStream;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import com.fasterxml.jackson.databind.JsonNode;

import net.talaatharb.healthcatalog.constants.ApiConstants;
import net.talaatharb.healthcatalog.model.CatalogItemType;

class CatalogItemApiIT extends AbstractAPIIT {

	private String versionId;

	@BeforeEach
	void uploadSample() throws Exception {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("Sample.zip");
		MockMultipartFile file = new MockMultipartFile("file", "Sample.zip", "application/zip", inputStream);
		String response = mvc.perform(multipart(ApiConstants.VERSIONS_API_V1).file(file)).andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();
		versionId = objectMapper.readTree(response).get("id").asText();
	}

	@Test
	void testGetItemTypes_ListsAllTypesWithCounts() throws Exception {
		mvc.perform(get(ApiConstants.VERSIONS_API_V1 + "/" + versionId + ApiConstants.ITEM_TYPES)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(CatalogItemType.values().length))
			.andExpect(jsonPath("$[0].type").value("DRUG"))
			.andExpect(jsonPath("$[0].count", greaterThanOrEqualTo(1)))
			.andExpect(jsonPath("$[?(@.type == 'CITY')].count").value(18))
			.andExpect(jsonPath("$[?(@.type == 'STREET')].count").value(12))
			.andExpect(jsonPath("$[?(@.type == 'DISTRICT')].count").value(9))
			.andExpect(jsonPath("$[?(@.type == 'PHYSICIAN')].count").value(17))
			.andExpect(jsonPath("$[?(@.type == 'COUNTRY')].count").value(126));
	}

	@Test
	void testUploadingSameVersionTwice_DoesNotDuplicateItems() throws Exception {
		uploadSample();

		mvc.perform(get(ApiConstants.VERSIONS_API_V1 + "/" + versionId + ApiConstants.ITEM_TYPES))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[?(@.type == 'CITY')].count").value(18));
	}

	@ParameterizedTest
	@CsvSource({ "CITY,Rupea", "STREET,Posada", "DISTRICT,ARAD", "PHYSICIAN,COSTEA", "SPECIALITY,ORTODONTIE",
			"INSURANCE_HOUSE,Vrancea", "ICD10,Hipoparatiroidia", "COUNTRY,GEORGIA", "HOLIDAY,Craciun" })
	void testSearchForItems_FindsMatchingItems(String type, String searchTerm) throws Exception {
		mvc.perform(get(ApiConstants.VERSIONS_API_V1 + "/" + versionId + ApiConstants.ITEMS)
				.param("type", type)
				.param("searchTerm", searchTerm.toLowerCase())
				.param("page", "0")
				.param("size", "10")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.totalElements", greaterThanOrEqualTo(1)))
			.andExpect(jsonPath("$.content[0].type").value(type))
			.andExpect(jsonPath("$.content[0].label").exists())
			.andExpect(jsonPath("$.content[0].name", containsStringIgnoringCase(searchTerm)))
			.andExpect(jsonPath("$.content[0].details").isMap());
	}

	@Test
	void testSearchForItems_ByCode() throws Exception {
		mvc.perform(get(ApiConstants.VERSIONS_API_V1 + "/" + versionId + ApiConstants.ITEMS)
				.param("type", "PHYSICIAN")
				.param("searchTerm", "E98533"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.totalElements").value(1))
			.andExpect(jsonPath("$.content[0].code").value("E98533"))
			.andExpect(jsonPath("$.content[0].details.validFrom").value("1976-01-25"));
	}

	@Test
	void testSearchForItems_WithoutSearchTerm_ReturnsAllOfType() throws Exception {
		mvc.perform(get(ApiConstants.VERSIONS_API_V1 + "/" + versionId + ApiConstants.ITEMS)
				.param("type", "DISTRICT"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.totalElements").value(9));
	}

	@Test
	void testSearchForItems_WithDrugType_IsBadRequest() throws Exception {
		mvc.perform(get(ApiConstants.VERSIONS_API_V1 + "/" + versionId + ApiConstants.ITEMS)
				.param("type", "DRUG")
				.param("searchTerm", "a"))
			.andExpect(status().isBadRequest());
	}

	@Test
	void testSearchForItems_WithUnknownType_IsBadRequest() throws Exception {
		mvc.perform(get(ApiConstants.VERSIONS_API_V1 + "/" + versionId + ApiConstants.ITEMS)
				.param("type", "UNKNOWN"))
			.andExpect(status().isBadRequest());
	}

	@Test
	void testGetItem() throws Exception {
		String searchResponse = mvc.perform(get(ApiConstants.VERSIONS_API_V1 + "/" + versionId + ApiConstants.ITEMS)
				.param("type", "STREET")
				.param("searchTerm", "Posada"))
			.andReturn().getResponse().getContentAsString();
		JsonNode first = objectMapper.readTree(searchResponse).get("content").get(0);
		String itemId = first.get("id").asText();

		mvc.perform(get(ApiConstants.API_V1 + ApiConstants.ITEMS + "/" + itemId).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(itemId))
			.andExpect(jsonPath("$.type").value("STREET"))
			.andExpect(jsonPath("$.label").value("Streets"))
			.andExpect(jsonPath("$.name").value("Posada"))
			.andExpect(jsonPath("$.details.cityCode").value(4020))
			.andExpect(jsonPath("$.details.streetType").value("STR"));
	}

	@Test
	void testGetItem_WithInvalidId_IsNotFound() throws Exception {
		mvc.perform(get(ApiConstants.API_V1 + ApiConstants.ITEMS + "/" + UUID.randomUUID()))
			.andExpect(status().isNotFound());
	}

	@Test
	void testSearchForDrugs_ByCode() throws Exception {
		String searchResponse = mvc.perform(get(ApiConstants.VERSIONS_API_V1 + "/" + versionId + "/drugs")
				.param("searchTerm", "NUTRIFLEX"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.totalElements", greaterThanOrEqualTo(1)))
			.andReturn().getResponse().getContentAsString();
		String code = objectMapper.readTree(searchResponse).get("content").get(0).get("code").asText();

		mvc.perform(get(ApiConstants.VERSIONS_API_V1 + "/" + versionId + "/drugs").param("searchTerm", code))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content[0].code").value(code));
	}
}
