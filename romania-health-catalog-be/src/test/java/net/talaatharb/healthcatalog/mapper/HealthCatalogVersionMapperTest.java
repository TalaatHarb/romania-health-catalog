package net.talaatharb.healthcatalog.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Instant;

import org.mapstruct.factory.Mappers;

import net.talaatharb.healthcatalog.dto.HealthCatalogVersionDto;
import net.talaatharb.healthcatalog.model.HealthCatalogVersionEntity;

class HealthCatalogVersionMapperTest implements DefaultMapperTest<HealthCatalogVersionEntity, HealthCatalogVersionDto> {

	private static final Instant ISSUE_DATE_SAMPLE = Instant.ofEpochMilli(584236800000L);

	@Override
	public void assertEqualEntityAndDto(HealthCatalogVersionEntity e, HealthCatalogVersionDto d) {
		if(e == null) {
			assertNull(d);
		}
		
		if(d == null) {
			assertNull(e);
		}
		
		assertEquals(e.getIssueDate(), d.getIssueDate());

	}

	@Override
	public HealthCatalogVersionDto createDTO() {
		var healthCatalogVersion = new HealthCatalogVersionDto();
		healthCatalogVersion.setIssueDate(ISSUE_DATE_SAMPLE);
		return healthCatalogVersion;
	}

	@Override
	public HealthCatalogVersionEntity createEntity() {
		var healthCatalogVersion = new HealthCatalogVersionEntity();
		healthCatalogVersion.setIssueDate(ISSUE_DATE_SAMPLE);
		return healthCatalogVersion;
	}

	@Override
	public DefaultMapper<HealthCatalogVersionEntity, HealthCatalogVersionDto> getMapper() {
		return Mappers.getMapper(HealthCatalogVersionMapper.class);
	}

}
