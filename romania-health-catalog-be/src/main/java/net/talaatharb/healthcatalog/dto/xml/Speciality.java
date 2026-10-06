package net.talaatharb.healthcatalog.dto.xml;

import java.util.Date;

import lombok.Data;

@Data
public class Speciality { 
	private String code;
	private String name;
	private Date validFrom;
}
