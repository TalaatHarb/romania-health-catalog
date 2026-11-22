package net.talaatharb.healthcatalog.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import net.talaatharb.healthcatalog.model.CatalogImportEntity;

public interface CatalogImportRepository extends JpaRepository<CatalogImportEntity, UUID> {
	List<CatalogImportEntity> findByStatus(CatalogImportEntity.Status status);
}
