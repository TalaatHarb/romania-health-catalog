package net.talaatharb.healthcatalog.facade;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import net.talaatharb.healthcatalog.dto.DrugDto;
import net.talaatharb.healthcatalog.dto.HealthCatalogVersionDto;
import net.talaatharb.healthcatalog.dto.xml.Catalog;

public interface HealthCatalogFacade {

	List<HealthCatalogVersionDto> getAllAvailableVersions();

	HealthCatalogVersionDto saveVersion(Catalog catalog);

	Page<DrugDto> search(UUID versionId, String searchTerm, Pageable pageable);

	DrugDto getDrug(UUID drugId);
}
