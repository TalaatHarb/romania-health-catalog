package net.talaatharb.healthcatalog.facade;

import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.talaatharb.healthcatalog.dto.DrugDto;
import net.talaatharb.healthcatalog.dto.HealthCatalogVersionDto;
import net.talaatharb.healthcatalog.dto.xml.Catalog;
import net.talaatharb.healthcatalog.dto.xml.Drug;
import net.talaatharb.healthcatalog.mapper.DrugMapper;
import net.talaatharb.healthcatalog.mapper.HealthCatalogVersionMapper;
import net.talaatharb.healthcatalog.model.HealthCatalogVersionEntity;
import net.talaatharb.healthcatalog.service.HealthCatalogVersionService;

@RequiredArgsConstructor
@Service
@Slf4j
public class HealthCatalogFacadeImpl implements HealthCatalogFacade {

	private final HealthCatalogVersionMapper healthCatalogVersionMapper;
	private final HealthCatalogVersionService healthCatalogVersionService;
	private final DrugMapper drugMapper;

	@Transactional(value = TxType.REQUIRED)
	@Override
	public List<HealthCatalogVersionDto> getAllAvailableVersions() {
		log.debug("Getting list of all available versions");
		var versionList = healthCatalogVersionService.getAllAvailableVersions();
		log.debug("Found {} versions", versionList.size());
		log.debug("Versions: {}", versionList.toString());

		return healthCatalogVersionMapper.fromEntityToDto(versionList).stream()
			    .sorted(Comparator.comparing(HealthCatalogVersionDto::getIssueDate).reversed()).toList();
	}

	@Transactional(value = TxType.REQUIRED)
	@Override
	public HealthCatalogVersionDto saveVersion(Catalog catalog) {
		Date issueDate = catalog.getIssueDate();
		log.debug("Saving version with issue date: {}", issueDate.toString());
		HealthCatalogVersionEntity newVersion = new HealthCatalogVersionEntity();
		newVersion.setId(UUID.nameUUIDFromBytes(issueDate.toString().getBytes(StandardCharsets.UTF_8)));
		newVersion.setIssueDate(issueDate.toInstant());
		newVersion = healthCatalogVersionService.saveVersion(newVersion);

		List<Drug> drugList = catalog.getDrugs().getDrugList();
		log.debug("Saving {} drugs", drugList.size());
		healthCatalogVersionService.saveDrugs(drugMapper.fromXMLDtoToEntity(drugList), newVersion);
		
		log.debug("Save drugs successful");
		return healthCatalogVersionMapper.fromEntityToDto(newVersion);
	}

	@Override
	public Page<DrugDto> search(UUID versionId, String searchTerm, Pageable pageable) {
		return drugMapper.fromEntityToDto(healthCatalogVersionService.searchDrugs(versionId, searchTerm, pageable));
	}

	@Override
	public DrugDto getDrug(UUID drugId) {
		return drugMapper.fromEntityToDto(healthCatalogVersionService.getDrug(drugId));
	}
}
