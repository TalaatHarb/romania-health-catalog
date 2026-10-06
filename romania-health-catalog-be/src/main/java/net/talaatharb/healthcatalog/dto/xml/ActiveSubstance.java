package net.talaatharb.healthcatalog.dto.xml;

import java.util.Date;

import lombok.Data;

@Data 
public class ActiveSubstance { 
	private String code;
	private Date validFrom;
}
