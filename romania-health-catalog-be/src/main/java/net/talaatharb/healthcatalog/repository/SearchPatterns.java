package net.talaatharb.healthcatalog.repository;

import java.util.Locale;

/**
 * Builds the lower-case contains-pattern of the searches in Java: binding a ready pattern lets the database compile it
 * once per query instead of concatenating and lower-casing it for every scanned row
 */
public final class SearchPatterns {

	private SearchPatterns() {
	}

	public static String contains(String searchTerm) {
		String term = searchTerm == null ? "" : searchTerm.trim().toLowerCase(Locale.ROOT);
		return "%" + term + "%";
	}
}
