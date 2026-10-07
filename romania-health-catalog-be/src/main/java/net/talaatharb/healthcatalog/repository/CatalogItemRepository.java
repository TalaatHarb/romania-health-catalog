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

	/**
	 * The constant version/type prefix of the order by lets the requested sort (name) be read in the order of the
	 * search index instead of sorting all items of the type. The count uses count(*) since count(i) counts the id,
	 * which isn't in the search index and would load every matching row.
	 */
	@Query(value = """
			select i from CatalogItemEntity i
			where i.version.id = :versionId and i.type = :type
			and (lower(i.name) like :pattern
			  or lower(i.code) like :pattern)
			order by i.version.id, i.type
			""", countQuery = """
			select count(*) from CatalogItemEntity i
			where i.version.id = :versionId and i.type = :type
			and (lower(i.name) like :pattern
			  or lower(i.code) like :pattern)
			""")
	Page<CatalogItemEntity> search(@Param("versionId") UUID versionId, @Param("type") CatalogItemType type,
			@Param("pattern") String pattern, Pageable pageable);

	@Query("select i.type as type, count(*) as count from CatalogItemEntity i where i.version.id = :versionId group by i.type")
	List<TypeCount> countByType(@Param("versionId") UUID versionId);

	@Modifying
	@Query("delete from CatalogItemEntity i where i.version.id = :versionId")
	int deleteAllByVersionId(@Param("versionId") UUID versionId);

	List<CatalogItemEntity> findByVersionIdAndTypeAndName(UUID versionId, CatalogItemType type, String name);

	List<CatalogItemEntity> findByVersionIdAndTypeAndCode(UUID versionId, CatalogItemType type, String code);

	List<CatalogItemEntity> findByVersionIdAndType(UUID versionId, CatalogItemType type);
}
