package pages;

import java.nio.file.Paths;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import hooks.GlobalHooks;
import pages.locators.HomePageElementsLocator;
import utils.PageUtils;

public class HomePageActions {

	private static final Object IMPORT_LOCK = new Object();

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
		synchronized (IMPORT_LOCK) {
			doImportFile(filePath);
		}
	}

	private void doImportFile(String filePath) {
		var absolutePath = Paths.get(filePath).toAbsolutePath().toString();
		homePageElements.importButton.click();
		
		homePageElements.fileUploadInput.sendKeys(absolutePath);
		PageUtils.waitUntilClickable(webDriver, homePageElements.uploadButton);
		homePageElements.uploadButton.click();
        PageUtils.waitUntilUploadFinishes(webDriver, homePageElements.loadingIndicator);
		
        PageUtils.waitUntilClickable(webDriver, homePageElements.closeModalButton);
		homePageElements.closeModalButton.click();
		PageUtils.waitUntilElementVanish(webDriver, homePageElements.modal);
	}

	/**
	 * Tries to import a catalog with the given upload secret and returns the upload error shown in the dialog
	 */
	public String importFileWithSecret(String filePath, String uploadSecret) {
		var absolutePath = Paths.get(filePath).toAbsolutePath().toString();
		homePageElements.importButton.click();
		PageUtils.waitUntilVisible(webDriver, homePageElements.uploadSecretInput);

		homePageElements.fileUploadInput.sendKeys(absolutePath);
		homePageElements.uploadSecretInput.clear();
		homePageElements.uploadSecretInput.sendKeys(uploadSecret);
		PageUtils.waitUntilClickable(webDriver, homePageElements.uploadButton);
		homePageElements.uploadButton.click();

		PageUtils.waitUntilVisible(webDriver, homePageElements.uploadError);
		String error = homePageElements.uploadError.getText();

		homePageElements.closeModalButton.click();
		PageUtils.waitUntilElementVanish(webDriver, homePageElements.modal);
		return error;
	}

	/**
	 * Imports the catalog of the given issue date unless it was already imported
	 */
	public void ensureVersionAvailable(String issueDate, String filePath) {
		// scenarios run in parallel, so imports are serialized and the page is reloaded to see versions imported meanwhile
		synchronized (IMPORT_LOCK) {
			webDriver.navigate().refresh();
			clickOnVersionsMenu();
			boolean available = hasVersion(issueDate);
			clickOnVersionsMenu();
			if (!available) {
				doImportFile(filePath);
			}
		}
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
		PageUtils.waitUntilVisible(webDriver, homePageElements.drugView);
		return homePageElements.drugView;
	}

	public WebElement getItemView() {
		PageUtils.waitUntilVisible(webDriver, homePageElements.itemView);
		return homePageElements.itemView;
	}

	/**
	 * @return labels of the object types that can be searched (e.g. "Cities (18)")
	 */
	public List<String> getSearchTypes() {
		// item types are loaded asynchronously, only "Drugs" is present until they arrive
		return PageUtils.waitUntil(webDriver, driver -> {
			var types = homePageElements.searchTypeOptions.stream().map(WebElement::getText).toList();
			return types.size() > 1 ? types : null;
		});
	}

	/**
	 * Chooses what to search in, the option is matched by its label (counts are ignored)
	 */
	public void selectSearchType(String typeLabel) {
		// item types (and their counts) are loaded asynchronously once a version is selected
		WebElement option = PageUtils.waitUntil(webDriver, driver -> homePageElements.searchTypeOptions.stream()
				.filter(o -> o.getText().equals(typeLabel) || o.getText().startsWith(typeLabel + " ("))
				.filter(WebElement::isEnabled)
				.findFirst()
				.orElse(null));
		new Select(homePageElements.searchType).selectByVisibleText(option.getText());
	}

	public void searchIn(String searchTerm, String typeLabel) {
		selectSearchType(typeLabel);
		search(searchTerm);
	}
	
	public void clickOnFirstSearchResult() {
		WebElement firstSearchResult = homePageElements.searchResults.get(0);
		PageUtils.waitUntilClickable(webDriver, firstSearchResult);
		firstSearchResult.click();
	}
	
}
