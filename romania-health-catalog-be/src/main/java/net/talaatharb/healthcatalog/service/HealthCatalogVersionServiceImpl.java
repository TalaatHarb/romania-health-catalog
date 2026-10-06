package net.talaatharb.healthcatalog.service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import lombok.RequiredArgsConstructor;
import net.talaatharb.healthcatalog.model.DrugEntity;
import net.talaatharb.healthcatalog.model.HealthCatalogVersionEntity;
import net.talaatharb.healthcatalog.repository.DrugRepository;
import net.talaatharb.healthcatalog.repository.HealthCatalogVersionRepository;

@RequiredArgsConstructor
@Service
public class HealthCatalogVersionServiceImpl implements HealthCatalogVersionService {

	private final HealthCatalogVersionRepository healthCatalogVersionRepository;
	private final DrugRepository drugRepository;

	@Override
	public List<HealthCatalogVersionEntity> getAllAvailableVersions() {
		return healthCatalogVersionRepository.findAllByOrderByIssueDateDesc();
	}

	@Override
	public void saveDrugs(List<DrugEntity> drugs, HealthCatalogVersionEntity version) {
		drugs = drugs.stream().map(d -> {
			d.setVersion(version);
			d.setId(UUID.nameUUIDFromBytes((d.getCode() + d.getValidFrom().toString() + d.getPricePerPackage()
					+ version.getIssueDate().toString()).getBytes(StandardCharsets.UTF_8)));
			return d;
		}).toList();

		drugRepository.saveAll(drugs);
	}

	@Transactional(value = TxType.REQUIRED)
	@Override
	public HealthCatalogVersionEntity saveVersion(HealthCatalogVersionEntity newVersion) {
		return healthCatalogVersionRepository.save(newVersion);
	}

	@Override
	public Page<DrugEntity> searchDrugs(UUID versionId, String searchTerm, Pageable pageable) {
		return drugRepository.findAllByVersionIdAndValidToIsNullAndNameContainingIgnoreCase(versionId, searchTerm, pageable);
	}

	@Override
	public DrugEntity getDrug(UUID drugId) {
		Optional<DrugEntity> drugOptional = drugRepository.findById(drugId);
		if(drugOptional.isPresent()) {
			return drugOptional.get();
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No drug with that Id");
	}

}
