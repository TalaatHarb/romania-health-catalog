package net.talaatharb.healthcatalog.api;

import java.io.IOException;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.talaatharb.healthcatalog.config.UploadSecretVerifier;
import net.talaatharb.healthcatalog.constants.ApiConstants;
import net.talaatharb.healthcatalog.dto.CatalogImportDto;
import net.talaatharb.healthcatalog.service.CatalogImportService;

@RestController
@RequestMapping(ApiConstants.API_V1 + "/imports")
@RequiredArgsConstructor
@Slf4j
public class CatalogImportController {

	private final CatalogImportService imports;
	private final UploadSecretVerifier secrets;

	@PostMapping
	@ResponseStatus(HttpStatus.ACCEPTED)
	public CatalogImportDto uploadFile(@RequestParam MultipartFile file,
			@RequestHeader(name = UploadSecretVerifier.UPLOAD_SECRET, required = false) String secret) throws IOException {
		secrets.verify(secret);
		return imports.startFile(file);
	}

	@PostMapping("/from-url")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public CatalogImportDto uploadUrl(@RequestParam String url,
			@RequestHeader(name = UploadSecretVerifier.UPLOAD_SECRET, required = false) String secret) {
		secrets.verify(secret);
		return imports.startUrl(url);
	}

	@GetMapping("/{id}")
	public CatalogImportDto getStatus(@PathVariable UUID id,
			@RequestHeader(name = UploadSecretVerifier.UPLOAD_SECRET, required = false) String secret) {
		secrets.verify(secret);
		return imports.getStatus(id);
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ProblemDetail handleImportRequestError(ResponseStatusException ex) {
		log.warn("Catalog import request rejected: {}", ex.getReason());
		return ex.getBody();
	}
}
