package utils;

import java.time.Duration;
import java.util.Map;
import java.util.function.Function;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PageUtils {

	private static final int MAX_WAIT = 10;
	private static final int MAX_UPLOAD_WAIT = 120;
	private static final Map<String, String> PAGES = Map.of("Home", Constants.HOME_PAGE_URL);
	private static final Map<String, String> FILES = Map.of("2024 - 3", Constants.SAMPLE_FILE);

	public static final String mapPageNameToURL(String pageName) {
		return PAGES.get(pageName);
	}
	
	public static final String mapVersionToFileName(String version) {
		return FILES.get(version);
	}

	public static final void waitUntilElementVanish(WebDriver webDriver, WebElement element) {
		waitUntilElementVanish(webDriver, element, MAX_WAIT);
	}

	/**
	 * Waits for long running operations like uploading a whole catalog (all objects get persisted)
	 */
	public static final void waitUntilUploadFinishes(WebDriver webDriver, WebElement loadingIndicator) {
		waitUntilElementVanish(webDriver, loadingIndicator, MAX_UPLOAD_WAIT);
	}

	private static void waitUntilElementVanish(WebDriver webDriver, WebElement element, int seconds) {
		WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(seconds));
	    wait.until(ExpectedConditions.invisibilityOfAllElements(element));
	}

	public static final void waitUntilVisible(WebDriver webDriver, WebElement element) {
		WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(MAX_WAIT));
		wait.until(ExpectedConditions.visibilityOf(element));
	}

	public static final <T> T waitUntil(WebDriver webDriver, Function<WebDriver, T> condition) {
		WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(MAX_WAIT));
		wait.ignoring(StaleElementReferenceException.class);
		return wait.until(condition);
	}
	
	public static final void waitUntilClickable(WebDriver webDriver, WebElement element) {
		WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
		wait.until(ExpectedConditions.elementToBeClickable(element));
	}
}
