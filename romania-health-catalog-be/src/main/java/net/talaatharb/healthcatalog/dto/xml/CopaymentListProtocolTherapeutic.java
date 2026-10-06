package net.talaatharb.healthcatalog.dto.xml;

import java.util.Date;

import lombok.Data;

@Data
public class CopaymentListProtocolTherapeutic { 
	private String codeProtocolTherap;
	private String descProtocolTherap;
	private String copaymentListType;
	private String activeSubstance;
	private String atc;
	private String needApproval;
	private String contractCv;
	private Date validFrom;
	private String diseaseCategory;
	private String nhpCode;
	private Date validTo;
	private Integer icd10;
}
