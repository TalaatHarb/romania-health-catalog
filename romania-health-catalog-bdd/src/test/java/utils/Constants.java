package utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

	public static final String DEFAULT_URL = "http://localhost:5173";
	public static final String URL_PROPERTY = "site.url";
	public static final String MAIN_URL = System.getProperty(URL_PROPERTY, DEFAULT_URL);
	public static final String HOME_PAGE_URL = MAIN_URL + "/";
	public static final String SAMPLE_FILE = "./files/Sample.zip";

}
