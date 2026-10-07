package net.talaatharb.healthcatalog.api;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import net.talaatharb.healthcatalog.service.CatalogUrlImporter;
import net.talaatharb.healthcatalog.utils.FileUtils;

class CatalogUrlApiIT extends AbstractAPIIT {

	private static final String ENDPOINT = "/api/v1/versions/from-url";
	private static final String URL = "https://www.casmb.ro/catalog.zip";

	@MockitoBean
	private CatalogUrlImporter importer;

	@Test
	void importsWithSecretHeader() throws Exception {
		when(importer.download(URL)).thenReturn(FileUtils.readCatalogFromZipResource("Sample.zip"));
		mvc.perform(post(ENDPOINT).param("url", URL).header("uploadSecret", DEFAULT_UPLOAD_SECRET))
			.andExpect(status().isCreated()).andExpect(jsonPath("$.id").exists());
	}

	@Test
	void importsWithSecretParameter() throws Exception {
		when(importer.download(URL)).thenReturn(FileUtils.readCatalogFromZipResource("Sample.zip"));
		mvc.perform(post(ENDPOINT).param("url", URL).param("uploadSecret", DEFAULT_UPLOAD_SECRET))
			.andExpect(status().isCreated());
	}

	@Test
	void rejectsBeforeDownloadingWithoutSecret() throws Exception {
		mvc.perform(post(ENDPOINT).param("url", URL)).andExpect(status().isForbidden());
		verifyNoInteractions(importer);
	}

	@Test
	void wrongHeaderOverridesValidParameter() throws Exception {
		mvc.perform(post(ENDPOINT).param("url", URL).param("uploadSecret", DEFAULT_UPLOAD_SECRET)
				.header("uploadSecret", "wrong")).andExpect(status().isForbidden());
		verifyNoInteractions(importer);
	}

	@Test
	void invalidUrlReturnsProblem() throws Exception {
		when(importer.download(URL)).thenThrow(new IllegalArgumentException("Invalid catalog URL"));
		mvc.perform(post(ENDPOINT).param("url", URL).header("uploadSecret", DEFAULT_UPLOAD_SECRET))
			.andExpect(status().isBadRequest()).andExpect(jsonPath("$.detail").value("Invalid catalog URL"));
	}

	@Test
	void downloadFailureReturnsProblem() throws Exception {
		when(importer.download(URL)).thenThrow(new CatalogUrlImporter.DownloadException("Catalog download returned HTTP 404"));
		mvc.perform(post(ENDPOINT).param("url", URL).header("uploadSecret", DEFAULT_UPLOAD_SECRET))
			.andExpect(status().isBadGateway()).andExpect(jsonPath("$.title").value("Catalog download failed"));
	}
}
