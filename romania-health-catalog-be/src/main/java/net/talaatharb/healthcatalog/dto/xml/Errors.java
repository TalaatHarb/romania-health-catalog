package net.talaatharb.healthcatalog.dto.xml;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;

@Data public class Errors { 
	@JsonAlias("Error")
	private List<Error> errorList;
}
