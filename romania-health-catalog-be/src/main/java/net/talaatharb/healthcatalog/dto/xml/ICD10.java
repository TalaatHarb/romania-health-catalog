package net.talaatharb.healthcatalog.dto.xml;

import java.util.Date;

import lombok.Data;

@Data
public class ICD10 { 
	private String code;
	private String diseaseCategory;
	private String name;
	private Date validFrom;
	private Date validTo;
}
