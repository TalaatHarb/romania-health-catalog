package net.talaatharb.healthcatalog.dto.xml;

import java.util.Date;

import lombok.Data;

@Data
public class BlackListRow { 
	private Double personPID;
	private Date validFrom;
	private Date validTo;
}
