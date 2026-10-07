package net.talaatharb.healthcatalog.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.file.Files;
import java.time.Duration;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import net.talaatharb.healthcatalog.dto.xml.Catalog;
import net.talaatharb.healthcatalog.utils.FileUtils;

@Service
public class CatalogUrlImporter {

	private static final long MAX_DOWNLOAD_BYTES = 50L * 1024 * 1024;
	private final List<String> allowedHosts;

	public CatalogUrlImporter(@Value("${health-catalog.import.allowed-hosts}") List<String> allowedHosts) {
		this.allowedHosts = allowedHosts.stream().map(String::trim)
				.map(host -> host.toLowerCase(Locale.ROOT)).toList();
	}

	public Catalog download(String url) {
		URI uri = URI.create(url);
		long deadline = System.nanoTime() + Duration.ofMinutes(2).toNanos();
		for (int redirects = 0; redirects <= 5; redirects++) {
			if (System.nanoTime() > deadline) throw new DownloadException("Catalog download timed out");
			validate(uri);
			try {
				HttpURLConnection connection = openConnection(uri);
				connection.setInstanceFollowRedirects(false);
				connection.setConnectTimeout(10_000);
				connection.setReadTimeout(30_000);
				try {
					int status = connection.getResponseCode();
					if (status == 301 || status == 302 || status == 303 || status == 307 || status == 308) {
						String location = connection.getHeaderField("Location");
						if (location == null) throw new IllegalArgumentException("Download redirect has no Location");
						uri = uri.resolve(location);
						continue;
					}
					if (status != 200) throw new DownloadException("Catalog download returned HTTP " + status);
					if (connection.getContentLengthLong() > MAX_DOWNLOAD_BYTES) {
						throw new IllegalArgumentException("Catalog download exceeds 50 MiB");
					}
					var temporaryFile = Files.createTempFile("catalog-download-", ".tmp");
					try {
						try (var input = connection.getInputStream(); var output = Files.newOutputStream(temporaryFile)) {
							byte[] buffer = new byte[8192];
							long total = 0;
							int count;
							while ((count = input.read(buffer)) != -1) {
								total += count;
								if (total > MAX_DOWNLOAD_BYTES) throw new IllegalArgumentException("Catalog download exceeds 50 MiB");
								if (System.nanoTime() > deadline) throw new DownloadException("Catalog download timed out");
								output.write(buffer, 0, count);
							}
						}
						try (var input = Files.newInputStream(temporaryFile)) {
							return FileUtils.readCatalog(input);
						}
					} finally {
						Files.deleteIfExists(temporaryFile);
					}
				} finally {
					connection.disconnect();
				}
			} catch (IOException ex) {
				throw new DownloadException("Unable to download or read the catalog", ex);
			}
		}
		throw new IllegalArgumentException("Too many catalog download redirects");
	}

	private void validate(URI uri) {
		if (!"https".equalsIgnoreCase(uri.getScheme()) || uri.getHost() == null
				|| uri.getUserInfo() != null || uri.getFragment() != null
				|| (uri.getPort() != -1 && uri.getPort() != 443)
				|| !allowedHosts.contains(uri.getHost().toLowerCase(Locale.ROOT))) {
			throw new IllegalArgumentException("Catalog URL must use HTTPS on an allowed download host");
		}
	}

	HttpURLConnection openConnection(URI uri) throws IOException {
		return (HttpURLConnection) uri.toURL().openConnection();
	}

	public static class DownloadException extends RuntimeException {
		public DownloadException(String message) { super(message); }
		public DownloadException(String message, Throwable cause) { super(message, cause); }
	}
}
