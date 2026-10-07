package net.talaatharb.healthcatalog.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.talaatharb.healthcatalog.model.CatalogItemEntity;

/**
 * Schema clean-up that Hibernate's {@code ddl-auto: update} doesn't do: it creates new indexes but never drops
 * superseded ones. A leftover (version_id, type) index would keep being chosen by the database for item searches,
 * which prevents the search index from serving name-sorted pages without a full sort.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SchemaMaintenance implements ApplicationRunner {

	private final JdbcTemplate jdbcTemplate;

	@Override
	public void run(ApplicationArguments args) {
		jdbcTemplate.execute("DROP INDEX IF EXISTS " + CatalogItemEntity.LEGACY_VERSION_TYPE_INDEX);
		log.debug("Ensured superseded index {} is removed", CatalogItemEntity.LEGACY_VERSION_TYPE_INDEX);
	}
}
