package net.talaatharb.healthcatalog.dto.xml;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;

@Data
public class NHPCategory { 
	private String code;
	private String name;
	private Date validFrom;
	private Date validTo;
	@JsonAlias("NHPCode")
	private String nhpCode;
}
