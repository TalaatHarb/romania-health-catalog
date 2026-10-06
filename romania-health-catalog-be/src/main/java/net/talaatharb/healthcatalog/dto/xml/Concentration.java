package net.talaatharb.healthcatalog.dto.xml;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;

@Data
public class Concentration { 
	@JsonAlias("concentration")
	private String concentrationDescription;
	private Date validFrom;
}
