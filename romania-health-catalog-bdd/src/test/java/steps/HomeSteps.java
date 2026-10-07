package steps;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pages.HomePageActions;
import utils.PageUtils;

@RequiredArgsConstructor
@Slf4j
public class HomeSteps {

	private final HomePageActions homePage;

	private String uploadError;

	@When("Looking into the list of available versions")
	public void lookingIntoTheListOfAvailableVersions() {
		homePage.clickOnVersionsMenu();
	}

	@Then("I find the list of available versions")
	public void iFindTheListOfAvailableVersions() {
		var versionsList = homePage.getVersions();
		assertNotNull(versionsList, "Invalid version list");
	}

	@When("I import new catalog with issue date {string}")
	public void importingNewVersion(String issueDate) {
		homePage.importFile(PageUtils.mapVersionToFileName(issueDate));
	}

	@When("I try to import catalog with issue date {string} using upload secret {string}")
	public void tryingToImportWithSecret(String issueDate, String uploadSecret) {
		uploadError = homePage.importFileWithSecret(PageUtils.mapVersionToFileName(issueDate), uploadSecret);
	}

	@Then("I see the upload error {string}")
	public void iSeeTheUploadError(String expectedError) {
		assertNotNull(uploadError, "No upload error shown");
		assertTrue(uploadError.contains(expectedError), "Unexpected upload error: " + uploadError);
	}

	@Then("I confirm {string} gets added to the top of the list of available catalogs")
	public void iFindTheNewVersionInTheListOfVersions(String issueDate) {
		homePage.clickOnVersionsMenu();
		assertTrue(homePage.hasVersion(issueDate), "Version " + issueDate + " isn't available.");
	}

	@When("I search for {string}")
	public void iSearchFor(String drugName) {
		homePage.search(drugName);
	}

	@Then("I confirm at least {int} result")
	public void iConfirmAtLeastResult(Integer minCount) {
		assertTrue(homePage.getSearchResults().size() >= minCount, "Not enough search results");
	}
	
	@Then("I can open drug search result")
	public void iCanOpenDrugSearchResult() {
		homePage.clickOnFirstSearchResult();
		assertTrue(homePage.getDrugView().isDisplayed(), "Drug isn't in view");
	}

	@Given("catalog version {string} is available")
	public void catalogVersionIsAvailable(String issueDate) {
		homePage.ensureVersionAvailable(issueDate, PageUtils.mapVersionToFileName(issueDate));
	}

	@Then("I can choose to search in {string}")
	public void iCanChooseToSearchIn(String typeLabel) {
		var types = homePage.getSearchTypes();
		assertTrue(types.stream().anyMatch(t -> t.equals(typeLabel) || t.startsWith(typeLabel + " (")),
				"Can't search in " + typeLabel + ", available: " + types);
	}

	@When("I search for {string} in {string}")
	public void iSearchForIn(String searchTerm, String typeLabel) {
		homePage.searchIn(searchTerm, typeLabel);
	}

	@Then("I can open search result showing {string}")
	public void iCanOpenSearchResultShowing(String expectedText) {
		homePage.clickOnFirstSearchResult();
		var itemView = homePage.getItemView();
		assertTrue(itemView.getText().toLowerCase(Locale.ROOT).contains(expectedText.toLowerCase(Locale.ROOT)),
				"Item details don't show " + expectedText);
	}

	
}
