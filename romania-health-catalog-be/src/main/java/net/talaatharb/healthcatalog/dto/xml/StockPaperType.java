package net.talaatharb.healthcatalog.dto.xml;

import java.util.Date;

import lombok.Data;

@Data
public class StockPaperType { 
	private String code;
	private String description;
	private Integer mathSign;
	private Date validFrom;
}
