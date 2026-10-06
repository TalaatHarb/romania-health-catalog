package net.talaatharb.healthcatalog.dto.xml;

import lombok.Data;

@Data
public class City { 
	private String code;
	private String name;
	private String district;
	private Integer cityType;
	private String parentCity;
}
