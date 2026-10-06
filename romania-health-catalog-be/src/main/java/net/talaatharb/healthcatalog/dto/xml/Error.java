package net.talaatharb.healthcatalog.dto.xml;

import java.util.Date;

import lombok.Data;

@Data
public class Error { 
	private String code;
	private String text;
	private Date validFrom;
	private Date validTo;
}
