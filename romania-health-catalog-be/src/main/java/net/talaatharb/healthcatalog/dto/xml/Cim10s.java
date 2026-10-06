package net.talaatharb.healthcatalog.dto.xml;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;

@Data
public class Cim10s { 
	@JsonAlias("Cim10")
	private List<Cim10> cim10List;
}
