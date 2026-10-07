package net.talaatharb.healthcatalog.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SearchPatternsTest {

	@Test
	void testContainsPatternIsTrimmedAndLowerCase() {
		assertEquals("%nutriflex%", SearchPatterns.contains("  NutriFlex "));
	}

	@Test
	void testContainsPatternOfMissingTermMatchesEverything() {
		assertEquals("%%", SearchPatterns.contains(null));
		assertEquals("%%", SearchPatterns.contains("   "));
	}

	@Test
	void testContainsPatternKeepsDiacritics() {
		assertEquals("%cătălin%", SearchPatterns.contains("CĂTĂLIN"));
	}
}
