package net.talaatharb.healthcatalog.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import net.talaatharb.healthcatalog.dto.DrugDto;
import net.talaatharb.healthcatalog.dto.xml.Drug;
import net.talaatharb.healthcatalog.model.DrugEntity;

@Mapper
public interface DrugMapper extends DefaultMapper<DrugEntity, DrugDto> {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "creationDate", ignore = true)
	@Mapping(target = "updateDate", ignore = true)
	@Mapping(target = "version", ignore = true)
	DrugEntity fromXMLDtoToEntity(Drug drug);

	List<DrugEntity> fromXMLDtoToEntity(List<Drug> drugs);
	
	@Mapping(target = "version", ignore = true)
	DrugEntity fromDtoToEntity(DrugDto dto);
}
