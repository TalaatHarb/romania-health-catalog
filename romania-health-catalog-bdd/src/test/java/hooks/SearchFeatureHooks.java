package hooks;

import io.cucumber.java.Before;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SearchFeatureHooks {

	@Before(value = "@Search", order = 0)
	public void searchScenariosSetup() {
		log.info("Search scenario");
	}
}
