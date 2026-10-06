package net.talaatharb.healthcatalog.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.talaatharb.healthcatalog.model.DrugEntity;

public interface DrugRepository extends JpaRepository<DrugEntity, UUID>{

	@Query("""
			select d from DrugEntity d
			where d.version.id = :versionId and d.validTo is null
			and (lower(d.name) like lower(concat('%', :searchTerm, '%'))
			  or lower(d.code) like lower(concat('%', :searchTerm, '%')))
			""")
	Page<DrugEntity> searchActiveDrugs(@Param("versionId") UUID versionId, @Param("searchTerm") String searchTerm,
			Pageable pageable);

	long countByVersionIdAndValidToIsNull(UUID versionId);
}
