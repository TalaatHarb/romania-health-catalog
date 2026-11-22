package net.talaatharb.healthcatalog.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import net.talaatharb.healthcatalog.service.CatalogUrlImporter;
import net.talaatharb.healthcatalog.utils.FileUtils;

class CatalogImportApiIT extends AbstractAPIIT {

	private static final String ENDPOINT = "/api/v1/imports";
	private static final String URL = "https://www.casmb.ro/catalog.zip";

	@MockitoBean
	private CatalogUrlImporter importer;

	@Test
	void acceptsUrlWithoutWaitingAndReturnsPersistedVersionWhenFinished() throws Exception {
		var releaseDownload = new CountDownLatch(1);
		var catalog = FileUtils.readCatalogFromZipResource("Sample.zip");
		when(importer.download(URL)).thenAnswer(call -> {
			if (!releaseDownload.await(10, TimeUnit.SECONDS)) throw new IllegalStateException("Test download not released");
			return catalog;
		});
		String id;
		try {
			var response = mvc.perform(post(ENDPOINT + "/from-url").param("url", URL)
					.header("uploadSecret", DEFAULT_UPLOAD_SECRET))
					.andExpect(status().isAccepted()).andExpect(jsonPath("$.status").value("RUNNING"))
					.andReturn().getResponse().getContentAsString();
			id = objectMapper.readTree(response).get("id").asText();
			mvc.perform(get(ENDPOINT + "/" + id).header("uploadSecret", DEFAULT_UPLOAD_SECRET))
					.andExpect(status().isOk()).andExpect(jsonPath("$.status").value("RUNNING"))
					.andExpect(jsonPath("$.version").isEmpty());
			mvc.perform(post(ENDPOINT + "/from-url").param("url", URL)
					.header("uploadSecret", DEFAULT_UPLOAD_SECRET))
					.andExpect(status().isConflict())
					.andExpect(jsonPath("$.detail").value("A catalog import is already running. Try again later."));
		} finally {
			releaseDownload.countDown();
		}
		var job = awaitCompletion(id);
		assertEquals("SUCCEEDED", job.get("status").asText());
		var versions = mvc.perform(get("/api/v1/versions")).andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		assertTrue(versions.contains(job.get("version").get("id").asText()));
	}

	@Test
	void processesStagedFileAndPublishesFailures() throws Exception {
		var file = new MockMultipartFile("file", "invalid.xml", "application/xml", "not xml".getBytes());
		var response = mvc.perform(multipart(ENDPOINT).file(file).header("uploadSecret", DEFAULT_UPLOAD_SECRET))
				.andExpect(status().isAccepted()).andReturn().getResponse().getContentAsString();
		var job = awaitCompletion(objectMapper.readTree(response).get("id").asText());
		assertEquals("FAILED", job.get("status").asText());
		assertFalse(job.get("error").asText().isBlank());
	}

	@Test
	void protectsSubmissionAndStatusWithTheUploadSecret() throws Exception {
		mvc.perform(post(ENDPOINT + "/from-url").param("url", URL)).andExpect(status().isForbidden());
		mvc.perform(get(ENDPOINT + "/" + UUID.randomUUID())).andExpect(status().isForbidden());
		mvc.perform(get(ENDPOINT + "/" + UUID.randomUUID()).header("uploadSecret", DEFAULT_UPLOAD_SECRET))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.detail").value("Catalog import not found"));
		verifyNoInteractions(importer);
	}

	private com.fasterxml.jackson.databind.JsonNode awaitCompletion(String id) throws Exception {
		long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(20);
		do {
			var response = mvc.perform(get(ENDPOINT + "/" + id).header("uploadSecret", DEFAULT_UPLOAD_SECRET))
					.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			var job = objectMapper.readTree(response);
			if (!"RUNNING".equals(job.get("status").asText())) return job;
			Thread.sleep(50);
		} while (System.nanoTime() < deadline);
		throw new AssertionError("Import did not complete");
	}
}
