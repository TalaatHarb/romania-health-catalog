package net.talaatharb.healthcatalog.dto.xml;

import lombok.Data;

@Data
public class Cim10 { 
	private String code;
	private String name;
	private Integer entityLevel;
	private String parentCode;
}
