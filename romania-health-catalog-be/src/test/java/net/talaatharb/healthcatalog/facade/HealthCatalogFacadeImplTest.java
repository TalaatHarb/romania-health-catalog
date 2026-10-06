package net.talaatharb.healthcatalog.facade;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import net.talaatharb.healthcatalog.mapper.HealthCatalogVersionMapper;
import net.talaatharb.healthcatalog.service.HealthCatalogVersionService;

@ExtendWith(MockitoExtension.class)
class HealthCatalogFacadeImplTest {
	
	@InjectMocks
	private HealthCatalogFacadeImpl healthCatalogFacade;
	
	@Mock
	private HealthCatalogVersionMapper healthCatalogVersionMapper;
	
	@Mock
	private HealthCatalogVersionService healthCatalogVersionService;

	@Test
	void testGetAllAvailableVersions() {

		healthCatalogFacade.getAllAvailableVersions();
		
		verify(healthCatalogVersionService).getAllAvailableVersions();
	}

}
