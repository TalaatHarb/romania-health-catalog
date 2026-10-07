package net.talaatharb.healthcatalog.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

/**
 * Guards the search performance: with hundreds of thousands of drugs/items per version the contains-searches need
 * compact search indexes to avoid scanning (and sorting) the full rows of the version
 */
@SpringBootTest
@ActiveProfiles(profiles = "test")
@Tag("integration")
class SearchIndexIT {

	private static final String INDEX_COLUMNS = """
			SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.INDEX_COLUMNS
			WHERE TABLE_NAME = ? AND INDEX_NAME = ?
			ORDER BY ORDINAL_POSITION
			""";

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	void testDrugSearchIndexIsCreatedWithSearchColumns() {
		List<String> columns = jdbcTemplate.queryForList(INDEX_COLUMNS, String.class, "DRUG_ENTITY",
				"IDX_DRUG_SEARCH");

		assertEquals(List.of("VERSION_ID", "VALID_TO", "NAME", "CODE"), columns);
	}

	@Test
	void testCatalogItemSearchIndexIsCreatedWithSearchColumns() {
		List<String> columns = jdbcTemplate.queryForList(INDEX_COLUMNS, String.class, "CATALOG_ITEM_ENTITY",
				"IDX_CATALOG_ITEM_SEARCH");

		assertEquals(List.of("VERSION_ID", "TYPE", "NAME", "CODE"), columns);
	}

	@Test
	void testSupersededCatalogItemIndexIsDropped() {
		List<String> columns = jdbcTemplate.queryForList(INDEX_COLUMNS, String.class, "CATALOG_ITEM_ENTITY",
				"IDX_CATALOG_ITEM_VERSION_TYPE");

		assertTrue(columns.isEmpty());
	}

	@Test
	void testCatalogItemSearchPageIsReadInIndexOrder() {
		String plan = jdbcTemplate.queryForObject("""
				EXPLAIN SELECT i.* FROM catalog_item_entity i
				WHERE i.version_id = ? AND i.type = 'CITY'
				AND (lower(i.name) LIKE ? OR lower(i.code) LIKE ?)
				ORDER BY i.version_id, i.type, i.name
				OFFSET 0 ROWS FETCH FIRST 20 ROWS ONLY
				""", String.class, UUID.randomUUID(), "%%", "%%");

		assertTrue(plan.contains("IDX_CATALOG_ITEM_SEARCH") && plan.contains("index sorted"),
				"Item search page isn't read in search index order: " + plan);
	}
}