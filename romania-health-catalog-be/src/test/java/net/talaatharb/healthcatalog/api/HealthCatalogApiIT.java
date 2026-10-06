package net.talaatharb.healthcatalog.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.InputStream;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import net.talaatharb.healthcatalog.constants.ApiConstants;

class HealthCatalogApiIT extends AbstractAPIIT{

	@Test
	void testCallVersionsAPI() throws Exception {
		final ResultActions result = mvc.perform(get(ApiConstants.VERSIONS_API_V1).accept(MediaType.APPLICATION_JSON));

		result.andExpect(MockMvcResultMatchers.status().is(200));
	}

	@Test
	void testUploadFile() throws Exception {
		// Load the Sample.zip file from resources
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("Sample.zip");
		MockMultipartFile file = new MockMultipartFile(
			"file", 
			"Sample.zip", 
			"application/zip", 
			inputStream
		);

		final ResultActions result = mvc.perform(
			multipart(ApiConstants.VERSIONS_API_V1)
				.file(file)
		);

		result.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").exists())
			.andExpect(jsonPath("$.issueDate").exists());
	}

	@Test
	void testUploadFile_WithEmptyZip_ShouldFail() throws Exception {
		// Load the empty.zip file from resources
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("empty.zip");
		MockMultipartFile file = new MockMultipartFile(
			"file", 
			"empty.zip", 
			"application/zip", 
			inputStream
		);

		final ResultActions result = mvc.perform(
			multipart(ApiConstants.VERSIONS_API_V1)
				.file(file)
		);

		result.andExpect(status().isBadRequest());
	}

	@Test
	void testSearchForDrugs() throws Exception {
		// First upload a catalog
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("Sample.zip");
		MockMultipartFile file = new MockMultipartFile(
			"file", 
			"Sample.zip", 
			"application/zip", 
			inputStream
		);

		final ResultActions uploadResult = mvc.perform(
			multipart(ApiConstants.VERSIONS_API_V1)
				.file(file)
		);

		String responseContent = uploadResult.andReturn().getResponse().getContentAsString();
		String versionId = objectMapper.readTree(responseContent).get("id").asText();

		// Now search for drugs
		final ResultActions result = mvc.perform(
			get(ApiConstants.API_V1 + "/versions/" + versionId + "/drugs")
				.param("searchTerm", "paracetamol")
				.param("page", "0")
				.param("size", "10")
				.accept(MediaType.APPLICATION_JSON)
		);

		result.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.pageable").exists())
			.andExpect(jsonPath("$.totalElements").exists());
	}

	@Test
	void testSearchForDrugs_WithInvalidVersionId_ReturnsEmptyResult() throws Exception {
		UUID randomVersionId = UUID.randomUUID();

		// The service doesn't validate if version exists, it just returns empty results
		final ResultActions result = mvc.perform(
			get(ApiConstants.API_V1 + "/versions/" + randomVersionId + "/drugs")
				.param("searchTerm", "paracetamol")
				.param("page", "0")
				.param("size", "10")
				.accept(MediaType.APPLICATION_JSON)
		);

		result.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.content").isEmpty())
			.andExpect(jsonPath("$.totalElements").value(0));
	}

	@Test
	void testGetDrug() throws Exception {
		// First upload a catalog
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("Sample.zip");
		MockMultipartFile file = new MockMultipartFile(
			"file", 
			"Sample.zip", 
			"application/zip", 
			inputStream
		);

		mvc.perform(
			multipart(ApiConstants.VERSIONS_API_V1)
				.file(file)
		);

		// Search for a drug to get a valid drug ID
		String responseContent = mvc.perform(
			get(ApiConstants.VERSIONS_API_V1)
				.accept(MediaType.APPLICATION_JSON)
		).andReturn().getResponse().getContentAsString();

		String versionId = objectMapper.readTree(responseContent).get(0).get("id").asText();

		String searchResponse = mvc.perform(
			get(ApiConstants.API_V1 + "/versions/" + versionId + "/drugs")
				.param("searchTerm", "")
				.param("page", "0")
				.param("size", "1")
				.accept(MediaType.APPLICATION_JSON)
		).andReturn().getResponse().getContentAsString();

		String drugId = objectMapper.readTree(searchResponse)
			.get("content").get(0).get("id").asText();

		// Now get the specific drug
		final ResultActions result = mvc.perform(
			get(ApiConstants.API_V1 + "/drugs/" + drugId)
				.accept(MediaType.APPLICATION_JSON)
		);

		result.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(drugId))
			.andExpect(jsonPath("$.code").exists())
			.andExpect(jsonPath("$.name").exists());
	}

	@Test
	void testGetDrug_WithInvalidDrugId_ShouldFail() throws Exception {
		UUID randomDrugId = UUID.randomUUID();

		final ResultActions result = mvc.perform(
			get(ApiConstants.API_V1 + "/drugs/" + randomDrugId)
				.accept(MediaType.APPLICATION_JSON)
		);

		result.andExpect(status().is4xxClientError());
	}

}
