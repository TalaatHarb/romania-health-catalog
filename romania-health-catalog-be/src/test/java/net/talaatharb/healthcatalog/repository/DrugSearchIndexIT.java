package net.talaatharb.healthcatalog.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

/**
 * Guards the drug search performance: with hundreds of thousands of drugs per version the contains-search needs the
 * compact search index to avoid scanning the full rows of the version
 */
@SpringBootTest
@ActiveProfiles(profiles = "test")
@Tag("integration")
class DrugSearchIndexIT {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	void testSearchIndexIsCreatedWithSearchColumns() {
		List<String> columns = jdbcTemplate.queryForList("""
				SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.INDEX_COLUMNS
				WHERE TABLE_NAME = 'DRUG_ENTITY' AND INDEX_NAME = 'IDX_DRUG_SEARCH'
				ORDER BY ORDINAL_POSITION
				""", String.class);

		assertEquals(List.of("VERSION_ID", "VALID_TO", "NAME", "CODE"), columns);
	}
}
