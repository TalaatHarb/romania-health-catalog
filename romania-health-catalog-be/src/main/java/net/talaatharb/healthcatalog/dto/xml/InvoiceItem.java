package net.talaatharb.healthcatalog.dto.xml;

import java.util.Date;

import lombok.Data;

@Data
public class InvoiceItem { 
	private String code;
	private String description;
	private String providerCategory;
	private String contractType;
	private Date validFrom;
	private Date validTo;
}
