package net.talaatharb.healthcatalog.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import net.talaatharb.healthcatalog.config.UploadSecretVerifier;
import net.talaatharb.healthcatalog.constants.ApiConstants;

/**
 * By default any origin may call the API (local Vite dev server, e2e tests)
 */
class CorsApiIT extends AbstractAPIIT {

	private static final String ORIGIN = "http://localhost:5173";

	@Test
	void testUploadPreflight_AllowsAnyOriginAndUploadSecretHeader() throws Exception {
		mvc.perform(options(ApiConstants.VERSIONS_API_V1)
				.header(HttpHeaders.ORIGIN, ORIGIN)
				.header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
				.header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, UploadSecretVerifier.UPLOAD_SECRET))
			.andExpect(status().isOk())
			.andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ORIGIN))
			.andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, UploadSecretVerifier.UPLOAD_SECRET))
			.andExpect(header().string(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600"));
	}

	@Test
	void testGet_ReturnsAllowOriginHeader() throws Exception {
		mvc.perform(get(ApiConstants.VERSIONS_API_V1).header(HttpHeaders.ORIGIN, "https://somewhere.example"))
			.andExpect(status().isOk())
			.andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "https://somewhere.example"));
	}
}
