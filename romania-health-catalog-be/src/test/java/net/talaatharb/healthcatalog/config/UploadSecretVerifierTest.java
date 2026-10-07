package net.talaatharb.healthcatalog.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import net.talaatharb.healthcatalog.config.UploadSecretVerifier.InvalidUploadSecretException;

class UploadSecretVerifierTest {

	private final UploadSecretVerifier verifier = new UploadSecretVerifier("UPLOAD_SECREET");

	@Test
	void testMatchingSecretIsAccepted() {
		assertDoesNotThrow(() -> verifier.verify("UPLOAD_SECREET"));
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { "upload_secreet", "UPLOAD_SECREET ", "UPLOAD_SECRE", "wrong" })
	void testMissingOrDifferentSecretIsRejected(String secret) {
		assertThrows(InvalidUploadSecretException.class, () -> verifier.verify(secret));
	}

	@Test
	void testBlankConfiguredSecretRejectsEveryUpload() {
		UploadSecretVerifier disabled = new UploadSecretVerifier("");

		assertThrows(InvalidUploadSecretException.class, () -> disabled.verify(""));
	}
}
