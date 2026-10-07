package net.talaatharb.healthcatalog.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.talaatharb.healthcatalog.model.DrugEntity;

public interface DrugRepository extends JpaRepository<DrugEntity, UUID>{

	/**
	 * The count uses count(*) since count(d) counts the id, which isn't in the search index and would load every
	 * matching row
	 */
	@Query(value = """
			select d from DrugEntity d
			where d.version.id = :versionId and d.validTo is null
			and (lower(d.name) like :pattern
			  or lower(d.code) like :pattern)
			""", countQuery = """
			select count(*) from DrugEntity d
			where d.version.id = :versionId and d.validTo is null
			and (lower(d.name) like :pattern
			  or lower(d.code) like :pattern)
			""")
	Page<DrugEntity> searchActiveDrugs(@Param("versionId") UUID versionId, @Param("pattern") String pattern,
			Pageable pageable);

	@Query("select count(*) from DrugEntity d where d.version.id = :versionId and d.validTo is null")
	long countByVersionIdAndValidToIsNull(@Param("versionId") UUID versionId);
}
