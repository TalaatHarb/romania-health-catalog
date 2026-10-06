package net.talaatharb.healthcatalog.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseDto implements Serializable{
	private static final long serialVersionUID = 3518685708798006854L;

	private Instant creationDate;

	private UUID id;

	private Instant updateDate;
}
