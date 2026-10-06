package net.talaatharb.healthcatalog.dto.xml;

import java.util.Date;

import lombok.Data;

@Data
public class CtrDocumentType { 
	private String code;
	private String name;
	private Date validFrom;
}
