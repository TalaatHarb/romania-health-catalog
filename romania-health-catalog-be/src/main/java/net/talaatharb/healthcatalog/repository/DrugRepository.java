package net.talaatharb.healthcatalog.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.talaatharb.healthcatalog.model.DrugEntity;

public interface DrugRepository extends JpaRepository<DrugEntity, UUID>{

	Page<DrugEntity> findAllByVersionIdAndValidToIsNullAndNameContainingIgnoreCase(UUID versionId, String searchTerm,
			Pageable pageable);

}
