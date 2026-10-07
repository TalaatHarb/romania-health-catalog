package net.talaatharb.healthcatalog.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;

import net.talaatharb.healthcatalog.config.UploadSecretVerifier;
import net.talaatharb.healthcatalog.constants.ApiConstants;

/**
 * Allowed origins restricted the same way the k8s deployment does it with CORS_ALLOWED_ORIGINS
 */
@TestPropertySource(properties = "CORS_ALLOWED_ORIGINS=https://rhc.talaatharb.net, https://*.preview.talaatharb.net")
class CorsRestrictedOriginsApiIT extends AbstractAPIIT {

	private static final String FE_ORIGIN = "https://rhc.talaatharb.net";

	@Test
	void testUploadPreflight_FromFrontendOrigin_IsAllowed() throws Exception {
		mvc.perform(options(ApiConstants.VERSIONS_API_V1)
				.header(HttpHeaders.ORIGIN, FE_ORIGIN)
				.header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
				.header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, UploadSecretVerifier.UPLOAD_SECRET))
			.andExpect(status().isOk())
			.andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, FE_ORIGIN));
	}

	@Test
	void testGet_FromOriginMatchingPattern_IsAllowed() throws Exception {
		String origin = "https://pr-1.preview.talaatharb.net";
		mvc.perform(get(ApiConstants.VERSIONS_API_V1).header(HttpHeaders.ORIGIN, origin))
			.andExpect(status().isOk())
			.andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin));
	}

	@Test
	void testPreflight_FromOtherOrigin_IsRejected() throws Exception {
		mvc.perform(options(ApiConstants.VERSIONS_API_V1)
				.header(HttpHeaders.ORIGIN, "https://evil.example")
				.header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
			.andExpect(status().isForbidden())
			.andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
	}
}
