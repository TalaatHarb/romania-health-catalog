package net.talaatharb.healthcatalog.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.Test;

class CatalogUrlImporterTest {

	private CatalogUrlImporter importer() {
		return spy(new CatalogUrlImporter(List.of("www.casmb.ro", "www.cnas.ro")));
	}

	private HttpURLConnection connection(int status) throws Exception {
		var connection = mock(HttpURLConnection.class);
		when(connection.getResponseCode()).thenReturn(status);
		when(connection.getContentLengthLong()).thenReturn(-1L);
		return connection;
	}

	@Test
	void downloadsAndParsesZip() throws Exception {
		var importer = importer();
		var connection = connection(200);
		when(connection.getInputStream()).thenReturn(getClass().getClassLoader().getResourceAsStream("Sample.zip"));
		doReturn(connection).when(importer).openConnection(any());
		assertNotNull(importer.download("https://www.casmb.ro/catalog.zip").getDrugs());
		verify(connection).disconnect();
		verify(connection).setInstanceFollowRedirects(false);
	}

	@Test
	void downloadsAndParsesXml() throws Exception {
		var importer = importer();
		var connection = connection(200);
		var xml = net.talaatharb.healthcatalog.utils.FileUtils.readXmlFromZipResource("Sample.zip");
		when(connection.getInputStream()).thenReturn(new ByteArrayInputStream(xml.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
		doReturn(connection).when(importer).openConnection(any());
		assertNotNull(importer.download("https://www.cnas.ro/catalog.xml").getDrugs());
	}

	@Test
	void rejectsUnsafeUrls() {
		var importer = importer();
		for (String url : List.of("http://www.casmb.ro/a", "https://localhost/a", "file:///tmp/a",
				"https://www.casmb.ro.evil.example/a", "https://user:password@www.casmb.ro/a",
				"https://www.casmb.ro:8443/a")) {
			assertThrows(IllegalArgumentException.class, () -> importer.download(url));
		}
	}

	@Test
	void rejectsRedirectToUnapprovedHost() throws Exception {
		var importer = importer();
		var connection = connection(302);
		when(connection.getHeaderField("Location")).thenReturn("https://localhost/internal");
		doReturn(connection).when(importer).openConnection(any());
		assertThrows(IllegalArgumentException.class, () -> importer.download("https://www.casmb.ro/a"));
		verify(connection).disconnect();
	}

	@Test
	void followsAllowedRelativeRedirect() throws Exception {
		var importer = importer();
		var redirect = connection(302);
		when(redirect.getHeaderField("Location")).thenReturn("/catalog.zip");
		var download = connection(200);
		when(download.getInputStream()).thenReturn(getClass().getClassLoader().getResourceAsStream("Sample.zip"));
		doReturn(redirect).when(importer).openConnection(URI.create("https://www.casmb.ro/start"));
		doReturn(download).when(importer).openConnection(URI.create("https://www.casmb.ro/catalog.zip"));
		assertNotNull(importer.download("https://www.casmb.ro/start").getDrugs());
	}

	@Test
	void rejectsOversizedDownload() throws Exception {
		var importer = importer();
		var connection = connection(200);
		when(connection.getContentLengthLong()).thenReturn(51L * 1024 * 1024);
		doReturn(connection).when(importer).openConnection(any());
		assertThrows(IllegalArgumentException.class, () -> importer.download("https://www.casmb.ro/a"));
		verify(connection).disconnect();
	}

	@Test
	void reportsUpstreamFailure() throws Exception {
		var importer = importer();
		doReturn(connection(404)).when(importer).openConnection(any());
		assertThrows(CatalogUrlImporter.DownloadException.class, () -> importer.download("https://www.casmb.ro/a"));
	}
}
