package net.talaatharb.healthcatalog.dto.xml;

import java.util.Date;

import lombok.Data;

@Data
public class NHP { 
	private String code;
	private String description;
	private Date validFrom;
	private Date validTo;
	private Integer hasAmbulatoryBudget;
	private Integer hasHospitalBudget;
	private Integer hasDrugsBudget;
	private Integer hasGoodsBudget;
	private String programCode;
}
