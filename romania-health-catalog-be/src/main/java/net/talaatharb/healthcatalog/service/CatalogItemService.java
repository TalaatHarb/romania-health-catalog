package net.talaatharb.healthcatalog.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import net.talaatharb.healthcatalog.dto.xml.Catalog;
import net.talaatharb.healthcatalog.model.CatalogItemEntity;
import net.talaatharb.healthcatalog.model.CatalogItemType;
import net.talaatharb.healthcatalog.model.HealthCatalogVersionEntity;

public interface CatalogItemService {

	/**
	 * Replace all generic catalog items of the version with the ones in the catalog
	 * 
	 * @param catalog the parsed catalog
	 * @param version the version the items belong to
	 * @return number of saved items
	 */
	long saveItems(Catalog catalog, HealthCatalogVersionEntity version);

	Page<CatalogItemEntity> searchItems(UUID versionId, CatalogItemType type, String searchTerm, Pageable pageable);

	CatalogItemEntity getItem(UUID itemId);

	Map<CatalogItemType, Long> countItemsByType(UUID versionId);

	List<CatalogItemType> getGenericTypes();

	/**
	 * @return the items of the type in the version whose name equals the given value
	 */
	List<CatalogItemEntity> findByName(UUID versionId, CatalogItemType type, String name);

	/**
	 * @return the items of the type in the version whose code equals the given value
	 */
	List<CatalogItemEntity> findByCode(UUID versionId, CatalogItemType type, String code);

	/**
	 * @return every item of the type in the version
	 */
	List<CatalogItemEntity> findAllOfType(UUID versionId, CatalogItemType type);
}
