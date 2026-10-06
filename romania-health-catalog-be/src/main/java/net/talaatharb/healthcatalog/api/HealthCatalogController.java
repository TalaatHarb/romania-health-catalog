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
import net.talaatharb.healthcatalog.dto.DrugDto;
import net.talaatharb.healthcatalog.dto.HealthCatalogVersionDto;
import net.talaatharb.healthcatalog.facade.HealthCatalogFacade;
import net.talaatharb.healthcatalog.utils.FileUtils;

@RequiredArgsConstructor
@RestController
@Slf4j
public class HealthCatalogController implements HealthCatalogApi{

	private final HealthCatalogFacade healthCatalogFacade;
	
	@Override
	public List<HealthCatalogVersionDto> getAllAvailableVersions(){
		return healthCatalogFacade.getAllAvailableVersions();
	}

	@Override
	public HealthCatalogVersionDto uploadFile(MultipartFile file) throws IOException {
		var catalog = FileUtils.readCatalogFromZipUpload(file);
		return healthCatalogFacade.saveVersion(catalog);
	}

	@Override
	public Page<DrugDto> searchForDrugs(UUID versionId, String searchTerm, Pageable pageable) {
		return healthCatalogFacade.search(versionId, searchTerm, pageable);
	}

	@Override
	public DrugDto getDrug(UUID drugId) {
		return healthCatalogFacade.getDrug(drugId);
	}
}
