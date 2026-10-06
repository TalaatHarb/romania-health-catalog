package net.talaatharb.healthcatalog.facade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import net.talaatharb.healthcatalog.dto.CatalogItemTypeDto;
import net.talaatharb.healthcatalog.mapper.CatalogItemMapper;
import net.talaatharb.healthcatalog.model.CatalogItemType;
import net.talaatharb.healthcatalog.service.CatalogItemService;
import net.talaatharb.healthcatalog.service.HealthCatalogVersionService;

@ExtendWith(MockitoExtension.class)
class CatalogItemFacadeImplTest {

	@InjectMocks
	private CatalogItemFacadeImpl catalogItemFacade;

	@Mock
	private CatalogItemService catalogItemService;

	@Mock
	private HealthCatalogVersionService healthCatalogVersionService;

	@Mock
	private CatalogItemMapper catalogItemMapper;

	@Test
	void testGetItemTypes_IncludesDrugsAndCounts() {
		UUID versionId = UUID.randomUUID();
		when(catalogItemService.countItemsByType(versionId)).thenReturn(Map.of(CatalogItemType.CITY, 5L));
		when(healthCatalogVersionService.countDrugs(versionId)).thenReturn(3L);

		List<CatalogItemTypeDto> types = catalogItemFacade.getItemTypes(versionId);

		assertEquals(CatalogItemType.values().length, types.size());
		assertEquals(new CatalogItemTypeDto(CatalogItemType.DRUG, "Drugs", 3), types.get(0));
		assertEquals(5L, types.stream().filter(t -> t.getType() == CatalogItemType.CITY).findFirst().orElseThrow().getCount());
		assertEquals(0L, types.stream().filter(t -> t.getType() == CatalogItemType.STREET).findFirst().orElseThrow().getCount());
	}

	@Test
	void testSearch_DelegatesToService() {
		UUID versionId = UUID.randomUUID();
		var pageable = PageRequest.of(0, 5);
		when(catalogItemService.searchItems(versionId, CatalogItemType.CITY, "x", pageable)).thenReturn(Page.empty());

		catalogItemFacade.search(versionId, CatalogItemType.CITY, "x", pageable);

		verify(catalogItemMapper).fromEntityToDto(any(Page.class));
	}

	@Test
	void testSearch_WithDrugType_Throws() {
		var pageable = PageRequest.of(0, 5);
		UUID versionId = UUID.randomUUID();
		assertThrows(IllegalArgumentException.class,
				() -> catalogItemFacade.search(versionId, CatalogItemType.DRUG, "x", pageable));
	}
}
