package net.talaatharb.healthcatalog.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;

import net.talaatharb.healthcatalog.config.UploadSecretVerifier;
import net.talaatharb.healthcatalog.constants.ApiConstants;

/**
 * The upload secret is overridden the same way the UPLOAD_SECRET environment variable does it
 */
@TestPropertySource(properties = "UPLOAD_SECRET=custom-secret")
class UploadSecretOverrideApiIT extends AbstractAPIIT {

	@Test
	void testUploadFile_WithOverriddenSecret() throws Exception {
		mvc.perform(multipart(ApiConstants.VERSIONS_API_V1).file(emptyZip())
				.header(UploadSecretVerifier.UPLOAD_SECRET, "custom-secret"))
			// passes the secret check and fails on the empty zip
			.andExpect(status().isBadRequest());
	}

	@Test
	void testUploadFile_WithDefaultSecret_IsForbiddenOnceOverridden() throws Exception {
		mvc.perform(multipart(ApiConstants.VERSIONS_API_V1).file(emptyZip())
				.header(UploadSecretVerifier.UPLOAD_SECRET, DEFAULT_UPLOAD_SECRET))
			.andExpect(status().isForbidden());
	}

	private MockMultipartFile emptyZip() throws Exception {
		return new MockMultipartFile("file", "empty.zip", "application/zip",
				getClass().getClassLoader().getResourceAsStream("empty.zip"));
	}
}
