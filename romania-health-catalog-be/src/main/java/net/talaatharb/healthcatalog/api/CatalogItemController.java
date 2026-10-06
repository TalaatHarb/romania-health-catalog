package net.talaatharb.healthcatalog.api;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.talaatharb.healthcatalog.dto.CatalogItemDto;
import net.talaatharb.healthcatalog.dto.CatalogItemTypeDto;
import net.talaatharb.healthcatalog.facade.CatalogItemFacade;
import net.talaatharb.healthcatalog.model.CatalogItemType;

@RequiredArgsConstructor
@RestController
public class CatalogItemController implements CatalogItemApi {

	private final CatalogItemFacade catalogItemFacade;

	@Override
	public List<CatalogItemTypeDto> getItemTypes(UUID versionId) {
		return catalogItemFacade.getItemTypes(versionId);
	}

	@Override
	public Page<CatalogItemDto> searchForItems(UUID versionId, CatalogItemType type, String searchTerm,
			Pageable pageable) {
		return catalogItemFacade.search(versionId, type, searchTerm, pageable);
	}

	@Override
	public CatalogItemDto getItem(UUID itemId) {
		return catalogItemFacade.getItem(itemId);
	}
}
