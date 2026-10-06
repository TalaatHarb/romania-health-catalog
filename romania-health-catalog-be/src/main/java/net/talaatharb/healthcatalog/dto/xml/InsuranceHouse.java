package net.talaatharb.healthcatalog.dto.xml;
import java.util.Date;

import lombok.Data;

@Data
public class InsuranceHouse { 
	private String code;
	private String name;
	private String type;
	private Date validFrom;
}
