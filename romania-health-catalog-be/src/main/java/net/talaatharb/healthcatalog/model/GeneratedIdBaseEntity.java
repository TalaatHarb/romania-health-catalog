package net.talaatharb.healthcatalog.model;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperclass
public class GeneratedIdBaseEntity {
	@CreationTimestamp
	private Instant creationDate;
	
	@UpdateTimestamp
    private Instant updateDate;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@UuidGenerator(style = UuidGenerator.Style.TIME)
	@Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
	private UUID id;
}
