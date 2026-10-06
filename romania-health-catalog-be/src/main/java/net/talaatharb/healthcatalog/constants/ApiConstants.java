package net.talaatharb.healthcatalog.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiConstants {

	public static final String API_V1 = "/api/v1";
	public static final String VERSIONS = "/versions";
	public static final String VERSIONS_API_V1 = API_V1 + VERSIONS;
}
