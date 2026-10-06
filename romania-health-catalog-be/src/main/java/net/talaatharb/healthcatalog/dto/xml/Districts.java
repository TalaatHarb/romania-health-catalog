package net.talaatharb.healthcatalog.dto.xml;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;

@Data
public class Districts { 
	@JsonAlias("District")
	private List<District> districtList;
}
