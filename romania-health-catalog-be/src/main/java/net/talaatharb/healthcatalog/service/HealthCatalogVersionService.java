package net.talaatharb.healthcatalog.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import net.talaatharb.healthcatalog.model.DrugEntity;
import net.talaatharb.healthcatalog.model.HealthCatalogVersionEntity;

public interface HealthCatalogVersionService {

	List<HealthCatalogVersionEntity> getAllAvailableVersions();

	void saveDrugs(List<DrugEntity> drugs, HealthCatalogVersionEntity version);

	HealthCatalogVersionEntity saveVersion(HealthCatalogVersionEntity newVersion);

	Page<DrugEntity> searchDrugs(UUID versionId, String searchTerm, Pageable pageable);

	DrugEntity getDrug(UUID drugId);

}
