package net.talaatharb.healthcatalog.dto.xml;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;

@Data
public class HealthDepartment { 
	private String code;
	private String name;
	@JsonAlias("FOCG")
	private Integer focg;
	private Integer type;
	private Double optimalDuration;
	private Date validFrom;
	private Date validTo;
	private Integer medicalType;
	private Double tariff;
	private Integer withOptimalDuration;
}
