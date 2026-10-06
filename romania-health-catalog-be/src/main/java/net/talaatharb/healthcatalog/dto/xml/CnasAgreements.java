package net.talaatharb.healthcatalog.dto.xml;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;

@Data
public class CnasAgreements { 
	@JsonAlias("CnasAgreement")
	private List<CnasAgreement> cnasAgreementList;
}
