package utils;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeDriverService;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebDriverUtils {

	public static final String BROWSER_PROPERTY = "browser";
	public static final String DEFAULT_BROWSER = "chrome";
	public static final String FIREFOX = "firefox";
	public static final String EDGE = "edge";
	public static final String BROWSER = System.getProperty(BROWSER_PROPERTY, DEFAULT_BROWSER);
	public static final long IMPLICIT_WAIT = 30L;

	public static final List<String> HEADLESS_BROWSER_ARGUMENTS = List.of("headless", "disable-gpu");
	public static final List<String> BROWSER_ARGUMENTS = List.of("disable-dev-shm-usage", "disable-extensions",
			"window-size=1920,1080", "no-sandbox", "ignore-certificate-errors");

	public static final WebDriver createWebDriver() {
		log.debug("Openning {}", BROWSER);
		switch (BROWSER) {
		case FIREFOX:
			return createFirefoxDriver();
		case EDGE:
			return createEdgeDriver();
		case DEFAULT_BROWSER:
		default:
			return createChromeDriver();
		}
	}

	private static final WebDriver createChromeDriver() {
		final ChromeDriverService service = (new ChromeDriverService.Builder()).build();
		final ChromeOptions options = new ChromeOptions();
		options.addArguments(BROWSER_ARGUMENTS);

		String os = System.getProperty("os.name");
		log.debug("OS: {}", os);
		if (!os.contains("Windows")) {
			options.addArguments(HEADLESS_BROWSER_ARGUMENTS);
		}

		ChromeDriver chromeDriver = new ChromeDriver(service, options);
		chromeDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(IMPLICIT_WAIT));
		chromeDriver.manage().window().maximize();

		return chromeDriver;
	}

	private static final WebDriver createEdgeDriver() {
		final EdgeDriverService service = (new EdgeDriverService.Builder()).build();
		final EdgeOptions options = new EdgeOptions();
		options.addArguments(BROWSER_ARGUMENTS);

		String os = System.getProperty("os.name");
		log.debug("OS: {}", os);
		if (!os.contains("Windows")) {
			options.addArguments(HEADLESS_BROWSER_ARGUMENTS);
		}

		EdgeDriver edgeDriver = new EdgeDriver(service, options);
		edgeDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(IMPLICIT_WAIT));
		edgeDriver.manage().window().maximize();

		return edgeDriver;
	}

	private static final WebDriver createFirefoxDriver() {

		FirefoxOptions options = new FirefoxOptions();
		options.addArguments(BROWSER_ARGUMENTS.stream().map(s -> "--" + s).toList());

		String os = System.getProperty("os.name");
		log.debug("OS: {}", os);
		if (!os.contains("Windows")) {
			log.debug("Running in headless mode");
			options.addArguments(HEADLESS_BROWSER_ARGUMENTS.stream().map(s -> "--" + s).toList());
		}

		WebDriver direfoxDriver = new FirefoxDriver(options);
		direfoxDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(IMPLICIT_WAIT));
		direfoxDriver.manage().window().maximize();

		return direfoxDriver;
	}
}
