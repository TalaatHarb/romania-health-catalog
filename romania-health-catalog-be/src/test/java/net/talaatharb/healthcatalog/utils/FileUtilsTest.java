package net.talaatharb.healthcatalog.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class FileUtilsTest {

	@Test
	void testReadXmlFromZip() throws IOException {
		final var fileName = "Sample.zip";
		final var contents = FileUtils.readXmlFromZipResource(fileName);
		
		assertNotNull(contents);
	}
	
	@Test
	void testReadXmlFromNonExistingZip() {
		final var fileName = "fake.zip";
		final Executable action = () -> FileUtils.readXmlFromZipResource(fileName);
		
		assertThrows(IllegalArgumentException.class, action);
	}
	
	@Test
	void testReadXmlFromEmptyZip() {
		final var fileName = "empty.zip";
		final Executable action = () -> FileUtils.readXmlFromZipResource(fileName);
		
		assertThrows(IllegalArgumentException.class, action);
	}
	
	@Test
	void testReadCatalogFromZip() throws IOException {
		final var fileName = "Sample.zip";
		final var catalog = FileUtils.readCatalogFromZipResource(fileName);
		
		assertNotNull(catalog);
		assertNotNull(catalog.getDrugs());
		assertNotNull(catalog.getDrugs().getDrugList());
		assertFalse(catalog.getDrugs().getDrugList().isEmpty());
	}
}
