package net.talaatharb.healthcatalog.dto.xml;

import java.util.Date;

import lombok.Data;

@Data
public class EuMember { 
	private String countryCode;
	private Date validFrom;
}
