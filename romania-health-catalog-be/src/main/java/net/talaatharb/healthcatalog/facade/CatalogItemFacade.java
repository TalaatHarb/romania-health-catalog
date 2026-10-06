package net.talaatharb.healthcatalog.facade;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import net.talaatharb.healthcatalog.dto.CatalogItemDto;
import net.talaatharb.healthcatalog.dto.CatalogItemTypeDto;
import net.talaatharb.healthcatalog.model.CatalogItemType;

public interface CatalogItemFacade {

	/**
	 * @param versionId the catalog version
	 * @return every searchable object type (drugs first) with the number of items
	 *         available in that version
	 */
	List<CatalogItemTypeDto> getItemTypes(UUID versionId);

	Page<CatalogItemDto> search(UUID versionId, CatalogItemType type, String searchTerm, Pageable pageable);

	CatalogItemDto getItem(UUID itemId);
}
