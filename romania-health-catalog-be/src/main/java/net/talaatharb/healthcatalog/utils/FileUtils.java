package net.talaatharb.healthcatalog.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
		final var contents = readXmlFromZipUpload(file);
		return XMLUtils.fromXmlString(contents, Catalog.class);
	}
}
