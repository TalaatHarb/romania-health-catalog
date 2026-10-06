package steps;

import io.cucumber.java.en.Given;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pages.CommonActions;
import utils.PageUtils;

@RequiredArgsConstructor
@Slf4j
public class CommonSteps {

	private final CommonActions commonActions;

	@Given("I am on Catalog {string} page")
	public void iAmOnPage(String pageName) {
		commonActions.goToURL(PageUtils.mapPageNameToURL(pageName));
		log.info("I am on Catalog {} page", pageName);
	}

}
