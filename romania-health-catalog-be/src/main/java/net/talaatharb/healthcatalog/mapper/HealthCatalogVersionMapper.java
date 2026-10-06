package net.talaatharb.healthcatalog.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import net.talaatharb.healthcatalog.dto.HealthCatalogVersionDto;
import net.talaatharb.healthcatalog.model.HealthCatalogVersionEntity;

@Mapper
public interface HealthCatalogVersionMapper extends DefaultMapper<HealthCatalogVersionEntity, HealthCatalogVersionDto> {

	@Mapping(target = "drugs", ignore = true)
	HealthCatalogVersionEntity fromDtoToEntity(HealthCatalogVersionDto dto);
}
