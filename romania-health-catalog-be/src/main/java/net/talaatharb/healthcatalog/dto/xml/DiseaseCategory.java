package net.talaatharb.healthcatalog.dto.xml;

import java.util.Date;

import lombok.Data;

@Data
public class DiseaseCategory { 
	private String code;
	private String description;
	private Boolean isChronicDisease;
	private Boolean isAuctioned;
	private Date validFrom;
}
