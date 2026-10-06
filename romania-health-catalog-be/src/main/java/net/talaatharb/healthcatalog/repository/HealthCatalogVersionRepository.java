package net.talaatharb.healthcatalog.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import net.talaatharb.healthcatalog.model.HealthCatalogVersionEntity;

public interface HealthCatalogVersionRepository extends JpaRepository<HealthCatalogVersionEntity, UUID>{
	
	List<HealthCatalogVersionEntity> findAllByOrderByIssueDateDesc();
}
