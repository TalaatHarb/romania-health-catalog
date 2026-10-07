package net.talaatharb.healthcatalog.service;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.talaatharb.healthcatalog.dto.xml.Catalog;
import net.talaatharb.healthcatalog.mapper.CatalogItemXmlMapper;
import net.talaatharb.healthcatalog.model.CatalogItemEntity;
import net.talaatharb.healthcatalog.model.CatalogItemType;
import net.talaatharb.healthcatalog.model.HealthCatalogVersionEntity;
import net.talaatharb.healthcatalog.repository.CatalogItemRepository;
import net.talaatharb.healthcatalog.repository.SearchPatterns;

@RequiredArgsConstructor
@Service
@Slf4j
public class CatalogItemServiceImpl implements CatalogItemService {

	static final int BATCH_SIZE = 500;

	private final CatalogItemRepository catalogItemRepository;
	private final CatalogItemXmlMapper catalogItemXmlMapper;
	private final EntityManager entityManager;

	@Transactional(value = TxType.REQUIRED)
	@Override
	public long saveItems(Catalog catalog, HealthCatalogVersionEntity version) {
		int deleted = catalogItemRepository.deleteAllByVersionId(version.getId());
		log.info("Removed {} previously stored items of version {}", deleted, version.getId());

		long saved = 0;
		for (CatalogItemType type : getGenericTypes()) {
			List<?> xmlObjects = type.extractFrom(catalog);
			if (xmlObjects.isEmpty()) {
				continue;
			}
			log.info("Saving {} items of type {}", xmlObjects.size(), type);
			for (int start = 0; start < xmlObjects.size(); start += BATCH_SIZE) {
				List<?> chunk = xmlObjects.subList(start, Math.min(start + BATCH_SIZE, xmlObjects.size()));
				List<CatalogItemEntity> entities = catalogItemXmlMapper.fromXmlObjects(type, chunk);
				entities.forEach(e -> e.setVersion(version));
				catalogItemRepository.saveAll(entities);
				saved += entities.size();
				// keep the persistence context small for large catalogs
				entityManager.flush();
				entityManager.clear();
			}
		}
		log.info("Saved {} generic catalog items for version {}", saved, version.getId());
		return saved;
	}

	@Override
	public Page<CatalogItemEntity> searchItems(UUID versionId, CatalogItemType type, String searchTerm,
			Pageable pageable) {
		return catalogItemRepository.search(versionId, type, SearchPatterns.contains(searchTerm), pageable);
	}

	@Override
	public CatalogItemEntity getItem(UUID itemId) {
		return catalogItemRepository.findById(itemId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No item with that Id"));
	}

	@Override
	public Map<CatalogItemType, Long> countItemsByType(UUID versionId) {
		Map<CatalogItemType, Long> counts = new EnumMap<>(CatalogItemType.class);
		catalogItemRepository.countByType(versionId).forEach(c -> counts.put(c.getType(), c.getCount()));
		return counts;
	}

	@Override
	public List<CatalogItemType> getGenericTypes() {
		return Arrays.stream(CatalogItemType.values()).filter(CatalogItemType::isGeneric).toList();
	}

	@Override
	public List<CatalogItemEntity> findByName(UUID versionId, CatalogItemType type, String name) {
		if (name == null) {
			return List.of();
		}
		return catalogItemRepository.findByVersionIdAndTypeAndName(versionId, type, name);
	}

	@Override
	public List<CatalogItemEntity> findByCode(UUID versionId, CatalogItemType type, String code) {
		if (code == null) {
			return List.of();
		}
		return catalogItemRepository.findByVersionIdAndTypeAndCode(versionId, type, code);
	}

	@Override
	public List<CatalogItemEntity> findAllOfType(UUID versionId, CatalogItemType type) {
		return catalogItemRepository.findByVersionIdAndType(versionId, type);
	}
}
