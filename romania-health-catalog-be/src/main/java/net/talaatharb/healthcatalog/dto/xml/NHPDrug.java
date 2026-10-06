package net.talaatharb.healthcatalog.dto.xml;

import java.util.Date;

import lombok.Data;

@Data
public class NHPDrug { 
	private String code;
	private String type;
	private Date validFrom;
	private Date validTo;
	private String nhp;
	private Double price;
	private Double vat;
}
