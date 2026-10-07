package net.talaatharb.healthcatalog.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FilterInputStream;
import java.io.PushbackInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.springframework.web.multipart.MultipartFile;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.talaatharb.healthcatalog.dto.xml.Catalog;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils {

	public static final String readXmlFromZipResource(String zipFileName) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream zipStream = classLoader.getResourceAsStream(zipFileName)) {
            if (zipStream == null) {
                String message = "File not found: " + zipFileName;
                log.error(message);
				throw new IllegalArgumentException(message);
            }

            try (ZipInputStream zis = new ZipInputStream(zipStream)) {
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    String name = entry.getName();
					if (name.endsWith(".xml")) {
						log.debug("Loading data from {}", name);
                        StringBuilder content = new StringBuilder();
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(zis))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                content.append(line).append("\n");
                            }
                        }
                        return content.toString();
                    }
                }
            }
        }
        String message = "No .xml file found in the zip: " + zipFileName;
        log.error(message);
		throw new IllegalArgumentException(message);
    }
	
	public static final String readXmlFromZipUpload(MultipartFile file) throws IOException {
		if (file.isEmpty()) {
			throw new IllegalArgumentException("Uploaded file is invalid");
        }
        try (InputStream zipStream = file.getInputStream()) {
            if (zipStream == null) {
                String message = "Unable to check file";
                log.error(message);
				throw new IllegalArgumentException(message);
            }

            try (ZipInputStream zis = new ZipInputStream(zipStream)) {
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    String name = entry.getName();
					if (name.endsWith(".xml")) {
						log.debug("Loading data from {}", name);
                        StringBuilder content = new StringBuilder();
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(zis))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                content.append(line).append("\n");
                            }
                        }
                        return content.toString();
                    }
                }
            }
        }
        String message = "No .xml file found in the zip";
        log.error(message);
		throw new IllegalArgumentException(message);
    }
	
	public static final Catalog readCatalogFromZipResource(String zipFileName) throws IOException {
		final var contents = readXmlFromZipResource(zipFileName);
		return XMLUtils.fromXmlString(contents, Catalog.class);
	}
	
	public static final Catalog readCatalogFromZipUpload(MultipartFile file) throws IOException {
		if (file.isEmpty()) {
			log.warn("Rejecting empty upload '{}'", file.getOriginalFilename());
			throw new IllegalArgumentException("Uploaded file is invalid");
		}
		log.info("Reading catalog from uploaded file '{}'", file.getOriginalFilename());
		try (var input = file.getInputStream()) {
			var catalog = readCatalog(input);
			log.info("Parsed catalog from uploaded file '{}', issue date {}", file.getOriginalFilename(),
					catalog.getIssueDate());
			return catalog;
		}
	}

	public static Catalog readCatalog(InputStream input) throws IOException {
		try (var stream = new PushbackInputStream(input, 2)) {
			byte[] signature = stream.readNBytes(2);
			stream.unread(signature);
			if (signature.length == 2 && signature[0] == 'P' && signature[1] == 'K') {
				log.info("Upload is a zip archive, looking for the XML entry");
				try (var zip = new ZipInputStream(stream)) {
					ZipEntry entry;
					while ((entry = zip.getNextEntry()) != null) {
						if (!entry.isDirectory() && entry.getName().toLowerCase(java.util.Locale.ROOT).endsWith(".xml")) {
							log.info("Parsing zip entry {}", entry.getName());
							return XMLUtils.createXMLObjectMapper().readValue(new LimitedXmlStream(zip), Catalog.class);
						}
					}
				}
				log.warn("No .xml file found in the uploaded zip");
				throw new IllegalArgumentException("No .xml file found in the zip");
			}
			log.info("Upload is a plain XML document, parsing it");
			return XMLUtils.createXMLObjectMapper().readValue(new LimitedXmlStream(stream), Catalog.class);
		}
	}

	// Bound decompressed input as well as the downloaded archive.
	private static class LimitedXmlStream extends FilterInputStream {
		private long remaining = 512L * 1024 * 1024;

		LimitedXmlStream(InputStream input) { super(input); }

		@Override
		public int read() throws IOException {
			int value = in.read();
			if (value != -1 && --remaining < 0) throw new IllegalArgumentException("Catalog XML exceeds 512 MiB");
			return value;
		}

		@Override
		public int read(byte[] bytes, int offset, int length) throws IOException {
			int count = in.read(bytes, offset, (int) Math.min(length, Math.max(1, remaining)));
			if (count > 0 && (remaining -= count) < 0) throw new IllegalArgumentException("Catalog XML exceeds 512 MiB");
			return count;
		}
	}
}
