package net.talaatharb.healthcatalog.dto.xml;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;

@Data
public class ICD10S { 
	@JsonAlias("ICD10")
	private List<ICD10> icd10List;
}
