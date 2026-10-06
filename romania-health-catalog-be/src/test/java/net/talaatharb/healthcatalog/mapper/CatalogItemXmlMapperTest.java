package net.talaatharb.healthcatalog.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.talaatharb.healthcatalog.dto.xml.BlackListRow;
import net.talaatharb.healthcatalog.dto.xml.Catalog;
import net.talaatharb.healthcatalog.dto.xml.Physician;
import net.talaatharb.healthcatalog.model.CatalogItemEntity;
import net.talaatharb.healthcatalog.model.CatalogItemType;
import net.talaatharb.healthcatalog.utils.FileUtils;

class CatalogItemXmlMapperTest {

	private final CatalogItemXmlMapper mapper = new CatalogItemXmlMapper();

	@Test
	void testFromXmlObject_MapsCodeNameAndDetails() {
		Physician physician = new Physician();
		physician.setName("DOCTOR HOUSE");
		physician.setStencil("A123");
		physician.setValidFrom(new Date(0));

		CatalogItemEntity entity = mapper.fromXmlObject(CatalogItemType.PHYSICIAN, physician);

		assertEquals(CatalogItemType.PHYSICIAN, entity.getType());
		assertEquals("A123", entity.getCode());
		assertEquals("DOCTOR HOUSE", entity.getName());
		assertEquals("1970-01-01", entity.getDetails().get("validFrom"));
		assertTrue(!entity.getDetails().containsKey("validTo"), "null attributes should be skipped");
	}

	@Test
	void testFromXmlObject_FormatsLargeNumbersAsPlainText() {
		BlackListRow row = new BlackListRow();
		row.setPersonPID(1520529240029d);

		CatalogItemEntity entity = mapper.fromXmlObject(CatalogItemType.BLACK_LIST, row);

		assertEquals("1520529240029", entity.getCode());
	}

	@Test
	void testAsText_Null() {
		assertNull(CatalogItemXmlMapper.asText(null));
	}

	@Test
	void testEveryGenericTypeIsExtractedFromSample() throws IOException {
		Catalog catalog = FileUtils.readCatalogFromZipResource("Sample.zip");

		List<CatalogItemType> emptyTypes = Arrays.stream(CatalogItemType.values()).filter(CatalogItemType::isGeneric)
				.filter(t -> t.extractFrom(catalog).isEmpty()).toList();

		assertTrue(emptyTypes.isEmpty(), "Types without data in the sample: " + emptyTypes);

		Arrays.stream(CatalogItemType.values()).filter(CatalogItemType::isGeneric).forEach(type -> {
			List<CatalogItemEntity> entities = mapper.fromXmlObjects(type, type.extractFrom(catalog));
			assertTrue(entities.stream().allMatch(e -> e.getName() != null || e.getCode() != null),
					"Every item should have a code or a name: " + type);
		});
	}

	@Test
	void testExtractFrom_EmptyCatalog() {
		assertTrue(CatalogItemType.CITY.extractFrom(new Catalog()).isEmpty());
		assertTrue(CatalogItemType.DRUG.extractFrom(new Catalog()).isEmpty());
		assertTrue(CatalogItemType.CITY.extractFrom(null).isEmpty());
	}
}
