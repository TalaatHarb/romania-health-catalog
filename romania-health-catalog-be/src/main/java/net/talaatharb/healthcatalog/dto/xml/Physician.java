package net.talaatharb.healthcatalog.dto.xml;

import java.util.Date;

import lombok.Data;

@Data
public class Physician { 
	private String name;
	private String stencil;
	private Date validFrom;
	private Date validTo;
}
