package net.talaatharb.healthcatalog.dto;

import java.time.Instant;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class HealthCatalogVersionDto extends BaseDto{

	private static final long serialVersionUID = -7046715447239477415L;
	
	private Instant issueDate;

}
