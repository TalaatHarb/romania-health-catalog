package net.talaatharb.healthcatalog.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;

/**
 * Checks the 'postgres' profile without a running PostgreSQL server
 */
class PostgresProfileTest {

	private static final String URL = "spring.datasource.url";

	private static StandardEnvironment postgresEnvironment(Map<String, Object> overrides) throws IOException {
		var environment = new StandardEnvironment();
		var sources = environment.getPropertySources();
		sources.addFirst(new MapPropertySource("overrides", overrides));
		new YamlPropertySourceLoader().load("postgres", new ClassPathResource("application-postgres.yml"))
				.forEach(sources::addLast);
		return environment;
	}

	@Test
	void testDefaultsPointToLocalPostgres() throws IOException {
		var environment = postgresEnvironment(Map.of());

		assertEquals("jdbc:postgresql://localhost:5432/health_catalog?reWriteBatchedInserts=true",
				environment.getProperty(URL));
		assertEquals("health_catalog", environment.getProperty("spring.datasource.username"));
		assertEquals("health_catalog", environment.getProperty("spring.datasource.password"));
	}

	@Test
	void testConnectionCanBeOverriddenByEnvironmentVariables() throws IOException {
		var environment = postgresEnvironment(Map.of("DB_URL", "jdbc:postgresql://db:5432/catalog",
				"DB_USERNAME", "user", "DB_PASSWORD", "secret"));

		assertEquals("jdbc:postgresql://db:5432/catalog", environment.getProperty(URL));
		assertEquals("user", environment.getProperty("spring.datasource.username"));
		assertEquals("secret", environment.getProperty("spring.datasource.password"));
	}

	@Test
	void testConfiguredDriverIsAvailableAndAcceptsUrl() throws IOException, ReflectiveOperationException, SQLException {
		var environment = postgresEnvironment(Map.of());

		var driver = (Driver) Class.forName(environment.getProperty("spring.datasource.driver-class-name"))
				.getDeclaredConstructor().newInstance();

		assertTrue(driver.acceptsURL(environment.getProperty(URL)));
	}

	@Test
	void testHibernatePostgresDialectIsAvailable() throws ClassNotFoundException {
		assertNotNull(Class.forName("org.hibernate.dialect.PostgreSQLDialect"));
	}
}
