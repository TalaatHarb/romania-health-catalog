package net.talaatharb.healthcatalog.api;

import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "test")
@Tag("integration")
public class AbstractAPIIT {
	@Autowired
	protected MockMvc mvc;

	@Autowired
	protected ObjectMapper objectMapper;
	
}
