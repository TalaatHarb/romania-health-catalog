package net.talaatharb.healthcatalog.dto.xml;

import java.util.Date;

import lombok.Data;

@Data
public class CopaymentListDrug { 
	private String copaymentListType;
	private String drug;
	private String nhpCode;
	private Double maxPrice;
	private Double maxPriceUT;
	private Double copaymentValue;
	private Double copaymentValue90;
	private Double wholeSalePrice;
	private Double referencePrice;
	private Boolean specialLaw;
	private String needApproval;
	private String contractCv;
	private Integer overValue;
	private Integer needSpecialty;
	private String classifInsulin;
	private String hgDci;
	private String hgAtc;
	private String hgIcd10;
	private Integer openCircuit;
	private Date validFrom;
	private Date validTo;
	private String diseaseCode;
}
