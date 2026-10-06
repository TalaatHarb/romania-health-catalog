package net.talaatharb.healthcatalog.service;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import net.talaatharb.healthcatalog.repository.HealthCatalogVersionRepository;

@ExtendWith(MockitoExtension.class)
class HealthCatalogVersionServiceImplTest {
	
	@InjectMocks
	private HealthCatalogVersionServiceImpl healthCatalogVersionService;
	
	@Mock
	private HealthCatalogVersionRepository healthCatalogVersionRepository;

	@Test
	void testGetAllAvailableVersions() {
		healthCatalogVersionService.getAllAvailableVersions();
		
		verify(healthCatalogVersionRepository).findAllByOrderByIssueDateDesc();
	}

}
