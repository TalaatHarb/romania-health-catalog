package net.talaatharb.healthcatalog.dto.xml;

import java.util.Date;

import lombok.Data;

@Data
public class PersonFunction { 
	private String code;
	private String description;
	private Date validFrom;
}
