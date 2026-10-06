package net.talaatharb.healthcatalog.dto.xml;

import java.util.Date;

import lombok.Data;

@Data
public class CnasAgreement { 
	private String countryCode;
	private Date validFrom;
}
