package net.talaatharb.healthcatalog.dto.xml;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;

@Data
public class Street { 
	private String code;
	private String name;
	@JsonAlias("city_code")
	private Integer cityCode;
	private String streetType;
}
