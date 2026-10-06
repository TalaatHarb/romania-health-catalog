package net.talaatharb.healthcatalog.dto.xml;

import java.util.Date;

import lombok.Data;

@Data
public class BusinessRule { 
	private String code;
	private String description;
	private Boolean isApplied;
	private Boolean prescriptionLevel;
	private Boolean isError;
	private Date validFrom;
	private Date validTo;
}
