package net.talaatharb.healthcatalog.api;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import net.talaatharb.healthcatalog.config.UploadSecretVerifier;
import net.talaatharb.healthcatalog.constants.ApiConstants;
import net.talaatharb.healthcatalog.dto.DrugDetailsDto;
import net.talaatharb.healthcatalog.dto.DrugDto;
import net.talaatharb.healthcatalog.dto.HealthCatalogVersionDto;

@RequestMapping(ApiConstants.API_V1)
public interface HealthCatalogApi {

	@GetMapping(ApiConstants.VERSIONS)
	@ResponseStatus(HttpStatus.OK)
	List<HealthCatalogVersionDto> getAllAvailableVersions();
	
	/**
	 * Uploads a catalog zip. Requires the upload secret as an {@code uploadSecret} header or request (query/form)
	 * parameter; the header wins when both are sent.
	 */
	@PostMapping(ApiConstants.VERSIONS)
	@ResponseStatus(HttpStatus.CREATED)
	HealthCatalogVersionDto uploadFile(@RequestParam MultipartFile file,
			@RequestHeader(name = UploadSecretVerifier.UPLOAD_SECRET, required = false) String uploadSecretHeader,
			@RequestParam(name = UploadSecretVerifier.UPLOAD_SECRET, required = false) String uploadSecretParam)
			throws IOException;

	@PostMapping("/versions/from-url")
	@ResponseStatus(HttpStatus.CREATED)
	HealthCatalogVersionDto uploadUrl(@RequestParam String url,
			@RequestHeader(name = UploadSecretVerifier.UPLOAD_SECRET, required = false) String uploadSecretHeader,
			@RequestParam(name = UploadSecretVerifier.UPLOAD_SECRET, required = false) String uploadSecretParam);
	
	@GetMapping(path = "/versions/{versionId}/drugs", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	Page<DrugDto> searchForDrugs(@PathVariable UUID versionId, @RequestParam String searchTerm, Pageable pageable);
	
	@GetMapping(path = "/drugs/{drugId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	DrugDto getDrug(@PathVariable UUID drugId);

	@GetMapping(path = "/drugs/{drugId}/details", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	DrugDetailsDto getDrugDetails(@PathVariable UUID drugId);
}
