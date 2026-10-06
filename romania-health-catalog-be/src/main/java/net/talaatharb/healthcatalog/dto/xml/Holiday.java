package net.talaatharb.healthcatalog.dto.xml;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;

@Data
public class Holiday { 
	@JsonAlias("holiday")
	private Date holidayDate;
	private String description;
}
