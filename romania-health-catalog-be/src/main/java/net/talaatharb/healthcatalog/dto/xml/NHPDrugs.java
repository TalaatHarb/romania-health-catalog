package net.talaatharb.healthcatalog.dto.xml;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;

@Data
public class NHPDrugs { 
	@JsonAlias("NHP_DRUG")
	private List<NHPDrug> nhpDrugList;
}
