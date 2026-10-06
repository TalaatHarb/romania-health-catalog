package hooks;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import lombok.Getter;
import utils.WebDriverUtils;

public class GlobalHooks {

	@Getter
	private WebDriver webDriver;

	@Before(order = 100)
	public void commonSetup() {
		webDriver = WebDriverUtils.createWebDriver();
	}

	@After(order = 100)
	public void commonTearDown(Scenario scenario) {
		takeScreenShotIfFailed(scenario);
		webDriver.quit();
		webDriver = null;
	}

	private void takeScreenShotIfFailed(Scenario scenario) {
		if (scenario.isFailed()) {
			var srcBytes = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.BYTES);
			scenario.attach(srcBytes, "image/png", scenario.getName());
		}
	}
}
