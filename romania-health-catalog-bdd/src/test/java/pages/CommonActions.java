package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import hooks.GlobalHooks;

public class CommonActions {
	
	private final WebDriver webDriver;

	public CommonActions(GlobalHooks globalHooks) {
		this.webDriver = globalHooks.getWebDriver();
	}
	
	public void goToURL(String url) {
		webDriver.get(url);
	}
	
	public String getCurrnetPageURL() {
		return webDriver.getCurrentUrl();
	}
	
	public String getCurrentPageTitle() {
		return webDriver.getTitle();
	}
	
	public void closeBrowser() {
		webDriver.quit();
	}
	
	public void clickOnLink(String linkText) {
		webDriver.findElement(By.linkText(linkText)).click();
	}
}
