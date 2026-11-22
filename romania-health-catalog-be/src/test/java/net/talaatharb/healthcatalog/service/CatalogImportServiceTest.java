package net.talaatharb.healthcatalog.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import net.talaatharb.healthcatalog.dto.HealthCatalogVersionDto;
import net.talaatharb.healthcatalog.dto.xml.Catalog;
import net.talaatharb.healthcatalog.facade.HealthCatalogFacade;
import net.talaatharb.healthcatalog.model.CatalogImportEntity;
import net.talaatharb.healthcatalog.model.CatalogImportEntity.Status;
import net.talaatharb.healthcatalog.repository.CatalogImportRepository;

@ExtendWith(MockitoExtension.class)
class CatalogImportServiceTest {

	@Mock private CatalogImportRepository repository;
	@Mock private HealthCatalogFacade facade;
	@Mock private CatalogUrlImporter importer;
	private final List<Runnable> tasks = new ArrayList<>();
	private CatalogImportService service;

	@BeforeEach
	void setup() {
		service = new CatalogImportService(repository, facade, importer, tasks::add);
	}

	private void storeJobs() {
		var jobs = new HashMap<UUID, CatalogImportEntity>();
		when(repository.findById(any())).thenAnswer(call -> Optional.ofNullable(jobs.get(call.getArgument(0))));
		when(repository.save(any())).thenAnswer(call -> {
			CatalogImportEntity job = call.getArgument(0);
			jobs.put(job.getId(), job);
			return job;
		});
	}

	@Test
	void returnsBeforeWorkStartsAndPublishesCommittedVersion() {
		storeJobs();
		var catalog = new Catalog();
		when(importer.download("https://www.casmb.ro/catalog.zip")).thenReturn(catalog);
		var version = new HealthCatalogVersionDto();
		version.setId(UUID.randomUUID());
		version.setIssueDate(Instant.parse("2026-11-01T00:00:00Z"));
		when(facade.saveVersion(catalog)).thenReturn(version);

		var accepted = service.startUrl("https://www.casmb.ro/catalog.zip");
		assertEquals(Status.RUNNING, accepted.status());
		assertNull(accepted.version());
		verifyNoInteractions(importer, facade);
		assertEquals(409, assertThrows(ResponseStatusException.class,
				() -> service.startUrl("another")).getStatusCode().value());
		tasks.removeFirst().run();

		var completed = service.getStatus(accepted.id());
		assertEquals(Status.SUCCEEDED, completed.status());
		assertEquals(version.getId(), completed.version().getId());
		assertEquals(version.getIssueDate(), completed.version().getIssueDate());
		assertEquals(Status.RUNNING, accepted.status());
	}

	@Test
	void exposesFailureAndReleasesWorker() {
		storeJobs();
		when(importer.download("bad")).thenThrow(new IllegalArgumentException("Invalid catalog URL"));
		var accepted = service.startUrl("bad");
		tasks.removeFirst().run();
		var failed = service.getStatus(accepted.id());
		assertEquals(Status.FAILED, failed.status());
		assertEquals("Invalid catalog URL", failed.error());
		assertNull(failed.version());
		assertDoesNotThrow(() -> service.startUrl("retry"));
		verifyNoInteractions(facade);
	}

	@Test
	void stagesFileBeforeRequestEnds() throws Exception {
		storeJobs();
		var file = new MockMultipartFile("file", "catalog.xml", "application/xml",
				"<Catalog issueDate=\"2026-11-01T00:00:00\"/>".getBytes(java.nio.charset.StandardCharsets.UTF_8));
		var version = new HealthCatalogVersionDto();
		version.setId(UUID.randomUUID());
		when(facade.saveVersion(any())).thenReturn(version);
		var accepted = service.startFile(file);
		verifyNoInteractions(facade);
		tasks.removeFirst().run();
		assertEquals(Status.SUCCEEDED, service.getStatus(accepted.id()).status());
	}

	@Test
	void rejectsEmptyFileWithoutReservingWorker() {
		when(repository.save(any())).thenAnswer(call -> call.getArgument(0));
		assertThrows(IllegalArgumentException.class,
				() -> service.startFile(new MockMultipartFile("file", new byte[0])));
		assertDoesNotThrow(() -> service.startUrl("valid"));
	}

	@Test
	void recoversInterruptedJobsAfterRestart() {
		var job = new CatalogImportEntity();
		job.setId(UUID.randomUUID());
		job.setStatus(Status.RUNNING);
		when(repository.findByStatus(Status.RUNNING)).thenReturn(List.of(job));
		service.recoverInterruptedImports();
		assertEquals(Status.FAILED, job.getStatus());
		assertTrue(job.getError().contains("server restart"));
		verify(repository).save(job);
	}

	@Test
	void reportsSchedulingFailureAndAllowsRetry() {
		when(repository.save(any())).thenAnswer(call -> call.getArgument(0));
		TaskExecutor executor = mock(TaskExecutor.class);
		service = new CatalogImportService(repository, facade, importer, executor);
		doThrow(new org.springframework.core.task.TaskRejectedException("shutting down")).when(executor).execute(any());
		assertEquals(503, assertThrows(ResponseStatusException.class, () -> service.startUrl("valid"))
				.getStatusCode().value());
		assertEquals(503, assertThrows(ResponseStatusException.class, () -> service.startUrl("retry"))
				.getStatusCode().value());
	}
}
