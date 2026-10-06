package net.talaatharb.healthcatalog.dto.xml;

import java.util.Date;

import lombok.Data;

@Data
public class CopaymentListType { 
	private String code;
	private String description;
	private Integer percent;
	private Integer drugMaxNo;
	private Date validFrom;
	private Date validTo;
	private Integer maxValue;
}
