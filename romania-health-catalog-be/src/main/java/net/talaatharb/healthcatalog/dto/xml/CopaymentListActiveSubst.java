package net.talaatharb.healthcatalog.dto.xml;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;

@Data
public class CopaymentListActiveSubst { 
	private String copaymentListType;
	private String activeSubstance;
	@JsonAlias("aTC")
	private String atc;
	private String diseasecategory;
	private String needApproval;
	private Integer openCircuit;
	private String contractCv;
	private Date validFrom;
	private Date validTo;
	private String nhpCode;
	private Integer icd10;
}
