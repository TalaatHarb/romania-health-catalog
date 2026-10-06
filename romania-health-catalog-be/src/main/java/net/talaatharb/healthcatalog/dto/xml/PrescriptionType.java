package net.talaatharb.healthcatalog.dto.xml;

import java.util.Date;

import lombok.Data;

@Data
public class PrescriptionType { 
	private String code;
	private String description;
	private Boolean forNarcotics;
	private Date validFrom;
}
