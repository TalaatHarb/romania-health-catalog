package hooks;

import io.cucumber.java.Before;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HomePageHooks {
	
	@Before(value = "@Home", order = 1)
	public void homePageScenariosSetup() {
		log.info("Home Page scenario");
	}
}
