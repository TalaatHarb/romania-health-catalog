package net.talaatharb.healthcatalog.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;
import net.talaatharb.healthcatalog.dto.CatalogImportDto;
import net.talaatharb.healthcatalog.dto.HealthCatalogVersionDto;
import net.talaatharb.healthcatalog.dto.xml.Catalog;
import net.talaatharb.healthcatalog.facade.HealthCatalogFacade;
import net.talaatharb.healthcatalog.model.CatalogImportEntity;
import net.talaatharb.healthcatalog.model.CatalogImportEntity.Status;
import net.talaatharb.healthcatalog.repository.CatalogImportRepository;
import net.talaatharb.healthcatalog.utils.FileUtils;

@Service
@Slf4j
public class CatalogImportService {

	private final CatalogImportRepository repository;
	private final HealthCatalogFacade facade;
	private final CatalogUrlImporter urlImporter;
	private final TaskExecutor executor;
	private final AtomicBoolean busy = new AtomicBoolean();

	public CatalogImportService(CatalogImportRepository repository, HealthCatalogFacade facade,
			CatalogUrlImporter urlImporter, @Qualifier("catalogImportExecutor") TaskExecutor executor) {
		this.repository = repository;
		this.facade = facade;
		this.urlImporter = urlImporter;
		this.executor = executor;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void recoverInterruptedImports() {
		// The backend runs as a single instance (Recreate deployment strategy).
		for (var job : repository.findByStatus(Status.RUNNING)) {
			job.setStatus(Status.FAILED);
			job.setError("Import interrupted by a server restart. Check available versions before retrying.");
			repository.save(job);
			log.warn("Marked interrupted catalog import {} as failed", job.getId());
		}
	}

	public CatalogImportDto startFile(MultipartFile file) throws IOException {
		if (file.isEmpty()) throw new IllegalArgumentException("Uploaded file is invalid");
		reserveWorker();
		Path staged = null;
		try {
			staged = Files.createTempFile("catalog-import-", ".tmp");
			file.transferTo(staged);
			Path input = staged;
			return submit(() -> {
				try (var stream = Files.newInputStream(input)) {
					return FileUtils.readCatalog(stream);
				}
			}, input);
		} catch (IOException | RuntimeException ex) {
			deleteStagedFile(staged);
			busy.set(false);
			throw ex;
		}
	}

	public CatalogImportDto startUrl(String url) {
		reserveWorker();
		try {
			return submit(() -> urlImporter.download(url), null);
		} catch (RuntimeException ex) {
			busy.set(false);
			throw ex;
		}
	}

	public CatalogImportDto getStatus(UUID id) {
		return toDto(repository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Catalog import not found")));
	}

	private void reserveWorker() {
		if (!busy.compareAndSet(false, true)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "A catalog import is already running. Try again later.");
		}
	}

	private CatalogImportDto submit(CatalogSource source, Path staged) {
		var job = new CatalogImportEntity();
		job.setId(UUID.randomUUID());
		job.setStatus(Status.RUNNING);
		var savedJob = repository.save(job);
		var accepted = toDto(savedJob);
		try {
			executor.execute(() -> run(savedJob, source, staged));
		} catch (RuntimeException ex) {
			savedJob.setStatus(Status.FAILED);
			savedJob.setError("The server could not start the import. Please retry.");
			repository.save(savedJob);
			log.error("Unable to schedule catalog import {}", savedJob.getId(), ex);
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, savedJob.getError(), ex);
		}
		return accepted;
	}

	private void run(CatalogImportEntity job, CatalogSource source, Path staged) {
		try {
			// saveVersion returns only after its transaction has committed.
			var version = facade.saveVersion(source.read());
			job.setVersionId(version.getId());
			job.setIssueDate(version.getIssueDate());
			job.setStatus(Status.SUCCEEDED);
			repository.save(job);
			log.info("Catalog import {} completed as version {}", job.getId(), version.getId());
		} catch (Exception ex) {
			log.error("Catalog import {} failed", job.getId(), ex);
			job.setStatus(Status.FAILED);
			job.setError(ex instanceof IllegalArgumentException || ex instanceof CatalogUrlImporter.DownloadException
					? ex.getMessage() : "Unable to read or save the catalog. See server logs for details.");
			if (job.getError() != null && job.getError().length() > 1000) {
				job.setError(job.getError().substring(0, 1000));
			}
			repository.save(job);
		} finally {
			deleteStagedFile(staged);
			busy.set(false);
		}
	}

	private void deleteStagedFile(Path staged) {
		if (staged == null) return;
		try {
			Files.deleteIfExists(staged);
		} catch (IOException ex) {
			log.error("Unable to delete staged catalog {}", staged, ex);
		}
	}

	private CatalogImportDto toDto(CatalogImportEntity job) {
		HealthCatalogVersionDto version = null;
		if (job.getStatus() == Status.SUCCEEDED) {
			version = new HealthCatalogVersionDto();
			version.setId(job.getVersionId());
			version.setIssueDate(job.getIssueDate());
		}
		return new CatalogImportDto(job.getId(), job.getStatus(), version, job.getError());
	}

	@FunctionalInterface
	private interface CatalogSource {
		Catalog read() throws IOException;
	}
}
