package steps;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
		assertNotNull(homePage.getDrugView(), "Drug isn't in view");
	}

	
}
