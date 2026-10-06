package net.talaatharb.healthcatalog.api;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import net.talaatharb.healthcatalog.facade.HealthCatalogFacade;

@ExtendWith(MockitoExtension.class)
class HealthCatalogControllerTest {
	
	@InjectMocks
	private HealthCatalogController healthCatalogApi;
	
	@Mock
	private HealthCatalogFacade healthCatalogFacade;

	@Test
	void testGetAllAvailableVersions() {
		healthCatalogApi.getAllAvailableVersions();
		
		verify(healthCatalogFacade).getAllAvailableVersions();
	}

}
