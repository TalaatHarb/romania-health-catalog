package net.talaatharb.healthcatalog.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import net.talaatharb.healthcatalog.dto.CatalogItemDto;
import net.talaatharb.healthcatalog.model.CatalogItemEntity;

@Mapper
public interface CatalogItemMapper extends DefaultMapper<CatalogItemEntity, CatalogItemDto> {

	@Mapping(target = "label", expression = "java(entity.getType() == null ? null : entity.getType().getLabel())")
	CatalogItemDto fromEntityToDto(CatalogItemEntity entity);

	@Mapping(target = "version", ignore = true)
	CatalogItemEntity fromDtoToEntity(CatalogItemDto dto);
}
