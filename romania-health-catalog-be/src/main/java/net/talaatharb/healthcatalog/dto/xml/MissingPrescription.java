package net.talaatharb.healthcatalog.dto.xml;

import java.util.Date;

import lombok.Data;

@Data
public class MissingPrescription { 
	private String series;
	private Integer firstNo;
	private Integer lastNo;
	private Integer noOfPrescriptions;
	private String type;
	private Date validFrom;
}
