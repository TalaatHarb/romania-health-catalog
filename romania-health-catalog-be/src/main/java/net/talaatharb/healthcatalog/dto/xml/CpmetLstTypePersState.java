package net.talaatharb.healthcatalog.dto.xml;

import java.util.Date;

import lombok.Data;

@Data
public class CpmetLstTypePersState { 
	private String copaymentListType;
	private String personState;
	private Integer percent;
	private Date validFrom;
	private Date validTo;
}
