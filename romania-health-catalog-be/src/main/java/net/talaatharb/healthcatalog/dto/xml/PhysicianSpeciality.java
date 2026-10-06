package net.talaatharb.healthcatalog.dto.xml;

import java.util.Date;

import lombok.Data;

@Data
public class PhysicianSpeciality { 
	private String stencil;
	private String contractNo;
	private String insuranceHouse;
	private String contractType;
	private String physicianType;
	private String specialityCode;
	private Date validFrom;
	private Date validTo;
}
