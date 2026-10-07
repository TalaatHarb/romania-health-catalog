package net.talaatharb.healthcatalog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.talaatharb.healthcatalog.model.CatalogItemEntity;
import net.talaatharb.healthcatalog.model.CatalogItemType;
import net.talaatharb.healthcatalog.model.DrugEntity;
import net.talaatharb.healthcatalog.model.HealthCatalogVersionEntity;

@ExtendWith(MockitoExtension.class)
class DrugDetailsServiceImplTest {

	@Mock
	private CatalogItemService catalogItemService;

	private DrugDetailsServiceImpl service;
	private DrugEntity drug;
	private UUID versionId;

	@BeforeEach
	void setUp() {
		service = new DrugDetailsServiceImpl(catalogItemService);
		var version = new HealthCatalogVersionEntity();
		versionId = UUID.randomUUID();
		version.setId(versionId);
		drug = new DrugEntity();
		drug.setId(UUID.randomUUID());
		drug.setVersion(version);
		drug.setCode("DRUG");
		drug.setActiveSubstance("SUBSTANCE");
	}

	@ParameterizedTest
	@CsvSource({"1,true", "2,true", "0,false", "-1,false"})
	void classifiesNumericCatalogFlag(int flag, boolean expected) {
		drug.setIsNarcotic(flag);
		assertEquals(expected, service.getDetails(drug).getRestrictions().isNarcotic());
	}

	@ParameterizedTest
	@ValueSource(strings = {"TRAMADOL", "XANAX"})
	void restrictedBrandsWithFlagTwoAreNarcotic(String name) {
		drug.setName(name);
		drug.setIsNarcotic(2);
		assertTrue(service.getDetails(drug).getRestrictions().isNarcotic());
	}

	@Test
	void missingFlagIsNotNarcotic() {
		assertFalse(service.getDetails(drug).getRestrictions().isNarcotic());
	}

	@Test
	void removesExactInsuranceDuplicatesButPreservesDistinctDetailsAndSources() {
		var first = item(Map.of("validFrom", "2024-01-01", "referencePrice", 10, "needApproval", "1"));
		var duplicate = item(Map.of("needApproval", "1", "referencePrice", 10, "validFrom", "2024-01-01"));
		var changedPrice = item(Map.of("validFrom", "2024-01-01", "referencePrice", 20));
		var changedDate = item(Map.of("validFrom", "2025-01-01", "referencePrice", 10));
		when(catalogItemService.findByName(versionId, CatalogItemType.COPAYMENT_LIST_DRUG, "DRUG"))
				.thenReturn(List.of(first, duplicate, changedPrice, changedDate));
		when(catalogItemService.findByName(versionId, CatalogItemType.COPAYMENT_LIST_ACTIVE_SUBSTANCE, "SUBSTANCE"))
				.thenReturn(List.of(first, duplicate));
		var type = item(Map.of("percent", 90));
		type.setName("List A");
		when(catalogItemService.findByCode(versionId, CatalogItemType.COPAYMENT_LIST_TYPE, "A"))
				.thenReturn(List.of(type));

		var details = service.getDetails(drug);
		var insurance = List.copyOf(details.getInsurance());
		assertEquals(4, insurance.size());
		assertEquals(List.of(first.getDetails(), changedPrice.getDetails(), changedDate.getDetails(), first.getDetails()),
				insurance.stream().map(entry -> entry.getDetails()).toList());
		assertEquals(List.of("DRUG", "DRUG", "DRUG", "ACTIVE_SUBSTANCE"),
				insurance.stream().map(entry -> entry.getSource()).toList());
		assertTrue(details.getRestrictions().isNeedsApproval());
		assertEquals(10.0, details.getPricing().getReferencePrice());
		assertEquals("List A", insurance.getFirst().getListDescription());
		assertEquals(90, insurance.getFirst().getPercent());
		var json = new ObjectMapper().valueToTree(details);
		assertTrue(json.get("insurance").isArray());
		assertEquals(4, json.get("insurance").size());
	}

	private CatalogItemEntity item(Map<String, Object> data) {
		var item = new CatalogItemEntity();
		item.setId(UUID.randomUUID());
		item.setCode("A");
		item.setDetails(data);
		return item;
	}
}
