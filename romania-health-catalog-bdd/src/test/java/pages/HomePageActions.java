package pages;

import java.nio.file.Paths;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import hooks.GlobalHooks;
import pages.locators.HomePageElementsLocator;
import utils.PageUtils;

public class HomePageActions {

	private final HomePageElementsLocator homePageElements;
	private final WebDriver webDriver;

	public HomePageActions(GlobalHooks globalHooks) {
		webDriver = globalHooks.getWebDriver();
		this.homePageElements = new HomePageElementsLocator(webDriver);
	}

	public void enterSearchTerm(String searchTerm) {
		homePageElements.searchBox.sendKeys(searchTerm);
	}

	public void clickSearchButton() {
		homePageElements.searchButton.click();
	}

	public void clickOnVersionsMenu() {
		homePageElements.versionsMenu.click();
	}

	public void clickOnVersion(String version) {
		var versions = homePageElements.versions;

		for (var versionElement : versions) {
			if (version.equals(versionElement.getText())) {
				versionElement.click();
				break;
			}
		}
	}
	
	public List<WebElement> getVersions(){
		return homePageElements.versions;
	}

	public void importFile(String filePath) {
		var absolutePath = Paths.get(filePath).toAbsolutePath().toString();
		homePageElements.importButton.click();
		
		homePageElements.fileUploadInput.sendKeys(absolutePath);
		PageUtils.waitUntilClickable(webDriver, homePageElements.uploadButton);
		homePageElements.uploadButton.click();
        PageUtils.waitUntilElementVanish(webDriver, homePageElements.loadingIndicator);
		
        PageUtils.waitUntilClickable(webDriver, homePageElements.closeModalButton);
		homePageElements.closeModalButton.click();
		PageUtils.waitUntilElementVanish(webDriver, homePageElements.modal);
	}
	
	public boolean hasVersion(String issueDate) {
		return homePageElements.versions.stream().anyMatch(v -> v.getText().contains(issueDate));
	}

	public void search(String drugName) {
		homePageElements.searchBox.sendKeys(drugName);
		PageUtils.waitUntilClickable(webDriver, homePageElements.searchButton);
		homePageElements.searchButton.click();
	}

	public List<WebElement> getSearchResults() {
		return homePageElements.searchResults;
	}

	public WebElement getDrugView() {
		return homePageElements.drugView;
	}
	
	public void clickOnFirstSearchResult() {
		WebElement firstSearchResult = homePageElements.searchResults.get(0);
		PageUtils.waitUntilClickable(webDriver, firstSearchResult);
		firstSearchResult.click();
	}
	
}
