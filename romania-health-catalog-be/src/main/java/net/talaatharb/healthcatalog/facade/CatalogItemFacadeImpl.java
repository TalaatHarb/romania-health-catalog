package net.talaatharb.healthcatalog.facade;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import net.talaatharb.healthcatalog.dto.CatalogItemDto;
import net.talaatharb.healthcatalog.dto.CatalogItemTypeDto;
import net.talaatharb.healthcatalog.mapper.CatalogItemMapper;
import net.talaatharb.healthcatalog.model.CatalogItemType;
import net.talaatharb.healthcatalog.service.CatalogItemService;
import net.talaatharb.healthcatalog.service.HealthCatalogVersionService;

@RequiredArgsConstructor
@Service
public class CatalogItemFacadeImpl implements CatalogItemFacade {

	private final CatalogItemService catalogItemService;
	private final HealthCatalogVersionService healthCatalogVersionService;
	private final CatalogItemMapper catalogItemMapper;

	@Override
	public List<CatalogItemTypeDto> getItemTypes(UUID versionId) {
		Map<CatalogItemType, Long> counts = catalogItemService.countItemsByType(versionId);
		return Arrays.stream(CatalogItemType.values()).map(type -> {
			long count = type.isGeneric() ? counts.getOrDefault(type, 0L)
					: healthCatalogVersionService.countDrugs(versionId);
			return new CatalogItemTypeDto(type, type.getLabel(), count);
		}).toList();
	}

	@Override
	public Page<CatalogItemDto> search(UUID versionId, CatalogItemType type, String searchTerm, Pageable pageable) {
		if (type == null || !type.isGeneric()) {
			throw new IllegalArgumentException("Type " + type + " can't be searched as a generic item, use its dedicated API");
		}
		return catalogItemMapper.fromEntityToDto(catalogItemService.searchItems(versionId, type, searchTerm, pageable));
	}

	@Override
	public CatalogItemDto getItem(UUID itemId) {
		return catalogItemMapper.fromEntityToDto(catalogItemService.getItem(itemId));
	}
}
