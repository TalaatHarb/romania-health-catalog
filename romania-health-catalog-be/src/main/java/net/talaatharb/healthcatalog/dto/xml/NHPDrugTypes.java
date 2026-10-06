package net.talaatharb.healthcatalog.dto.xml;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;

@Data
public class NHPDrugTypes { 
	@JsonAlias("NHP_DRUG_TYPE")
	private List<NHPDrugType> nhpDrugTypeList;
}
