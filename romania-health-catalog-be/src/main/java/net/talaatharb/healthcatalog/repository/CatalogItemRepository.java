package net.talaatharb.healthcatalog.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.talaatharb.healthcatalog.model.CatalogItemEntity;
import net.talaatharb.healthcatalog.model.CatalogItemType;

public interface CatalogItemRepository extends JpaRepository<CatalogItemEntity, UUID> {

	interface TypeCount {
		CatalogItemType getType();

		long getCount();
	}

	@Query("""
			select i from CatalogItemEntity i
			where i.version.id = :versionId and i.type = :type
			and (lower(i.name) like lower(concat('%', :searchTerm, '%'))
			  or lower(i.code) like lower(concat('%', :searchTerm, '%')))
			""")
	Page<CatalogItemEntity> search(@Param("versionId") UUID versionId, @Param("type") CatalogItemType type,
			@Param("searchTerm") String searchTerm, Pageable pageable);

	@Query("select i.type as type, count(i) as count from CatalogItemEntity i where i.version.id = :versionId group by i.type")
	List<TypeCount> countByType(@Param("versionId") UUID versionId);

	@Modifying
	@Query("delete from CatalogItemEntity i where i.version.id = :versionId")
	int deleteAllByVersionId(@Param("versionId") UUID versionId);
}
