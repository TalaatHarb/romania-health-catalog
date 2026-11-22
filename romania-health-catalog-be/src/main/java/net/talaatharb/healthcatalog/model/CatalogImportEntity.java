package net.talaatharb.healthcatalog.model;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class CatalogImportEntity extends BaseEntity {

	public enum Status { RUNNING, SUCCEEDED, FAILED }

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status;
	private UUID versionId;
	private Instant issueDate;
	@Column(length = 1000)
	private String error;
}
