package net.talaatharb.healthcatalog.config;

import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.talaatharb.healthcatalog.mapper.DrugMapper;
import net.talaatharb.healthcatalog.mapper.HealthCatalogVersionMapper;

@Configuration
public class MapperConfigurations {
	
	@Bean
	HealthCatalogVersionMapper getHealthCatalogVersionMapper() {
		return Mappers.getMapper(HealthCatalogVersionMapper.class);
	}
	
	@Bean
	DrugMapper getDrugMapper() {
		return Mappers.getMapper(DrugMapper.class);
	}

}