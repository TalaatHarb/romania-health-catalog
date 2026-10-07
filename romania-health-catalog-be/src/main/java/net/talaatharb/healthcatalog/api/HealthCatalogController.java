package net.talaatharb.healthcatalog.api;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.talaatharb.healthcatalog.config.UploadSecretVerifier;
import net.talaatharb.healthcatalog.dto.DrugDetailsDto;
import net.talaatharb.healthcatalog.dto.DrugDto;
import net.talaatharb.healthcatalog.dto.HealthCatalogVersionDto;
import net.talaatharb.healthcatalog.facade.HealthCatalogFacade;
import net.talaatharb.healthcatalog.utils.FileUtils;
import net.talaatharb.healthcatalog.service.CatalogUrlImporter;

@RequiredArgsConstructor
@RestController
@Slf4j
public class HealthCatalogController implements HealthCatalogApi{

	private final HealthCatalogFacade healthCatalogFacade;

	private final UploadSecretVerifier uploadSecretVerifier;
	private final CatalogUrlImporter catalogUrlImporter;
	
	@Override
	public List<HealthCatalogVersionDto> getAllAvailableVersions(){
		return healthCatalogFacade.getAllAvailableVersions();
	}

	@Override
	public HealthCatalogVersionDto uploadFile(MultipartFile file, String uploadSecretHeader, String uploadSecretParam)
			throws IOException {
		log.info("Received catalog upload '{}' ({} bytes)", file.getOriginalFilename(), file.getSize());
		uploadSecretVerifier.verify(uploadSecretHeader != null ? uploadSecretHeader : uploadSecretParam);
		var catalog = FileUtils.readCatalogFromZipUpload(file);
		var version = healthCatalogFacade.saveVersion(catalog);
		log.info("Catalog upload '{}' completed as version {}", file.getOriginalFilename(), version.getId());
		return version;
	}

	@Override
	public HealthCatalogVersionDto uploadUrl(String url, String uploadSecretHeader, String uploadSecretParam) {
		log.info("Received catalog import request from URL");
		uploadSecretVerifier.verify(uploadSecretHeader != null ? uploadSecretHeader : uploadSecretParam);
		var version = healthCatalogFacade.saveVersion(catalogUrlImporter.download(url));
		log.info("Catalog URL import completed as version {}", version.getId());
		return version;
	}

	@Override
	public Page<DrugDto> searchForDrugs(UUID versionId, String searchTerm, Pageable pageable) {
		return healthCatalogFacade.search(versionId, searchTerm, pageable);
	}

	@Override
	public DrugDto getDrug(UUID drugId) {
		return healthCatalogFacade.getDrug(drugId);
	}

	@Override
	public DrugDetailsDto getDrugDetails(UUID drugId) {
		return healthCatalogFacade.getDrugDetails(drugId);
	}
}
