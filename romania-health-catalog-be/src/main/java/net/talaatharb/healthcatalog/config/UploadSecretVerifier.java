package net.talaatharb.healthcatalog.config;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Guards catalog uploads with a shared secret, configured via the {@code UPLOAD_SECRET} environment variable
 */
@Component
public class UploadSecretVerifier {

	public static final String UPLOAD_SECRET = "uploadSecret";

	private final byte[] expectedSecret;

	public UploadSecretVerifier(@Value("${health-catalog.upload-secret}") String uploadSecret) {
		this.expectedSecret = uploadSecret.getBytes(StandardCharsets.UTF_8);
	}

	/**
	 * @throws InvalidUploadSecretException when the secret is missing or doesn't match the configured one
	 */
	public void verify(String providedSecret) {
		if (expectedSecret.length == 0 || providedSecret == null || providedSecret.isEmpty()
				// constant-time comparison so the secret can't be guessed from response times
				|| !MessageDigest.isEqual(expectedSecret, providedSecret.getBytes(StandardCharsets.UTF_8))) {
			throw new InvalidUploadSecretException();
		}
	}

	public static class InvalidUploadSecretException extends RuntimeException {

		private static final long serialVersionUID = 1L;

		public InvalidUploadSecretException() {
			super("Missing or invalid upload secret");
		}
	}
}
