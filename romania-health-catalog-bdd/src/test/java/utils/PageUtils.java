package utils;

import java.time.Duration;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PageUtils {

	private static final int MAX_WAIT = 10;
	private static final Map<String, String> PAGES = Map.of("Home", Constants.HOME_PAGE_URL);
	private static final Map<String, String> FILES = Map.of("2024 - 3", Constants.SAMPLE_FILE);

	public static final String mapPageNameToURL(String pageName) {
		return PAGES.get(pageName);
	}
	
	public static final String mapVersionToFileName(String version) {
		return FILES.get(version);
	}

	public static final void waitUntilElementVanish(WebDriver webDriver, WebElement element) {
		WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(MAX_WAIT));
	    wait.until(ExpectedConditions.invisibilityOfAllElements(element));
	}
	
	public static final void waitUntilClickable(WebDriver webDriver, WebElement element) {
		WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
		wait.until(ExpectedConditions.elementToBeClickable(element));
	}
}
