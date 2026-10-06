package net.talaatharb.healthcatalog.model;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperclass
public class BaseEntity {
	@CreationTimestamp
	private Instant creationDate;

	@UpdateTimestamp
	private Instant updateDate;

	@Id
	@Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
	private UUID id;
}
