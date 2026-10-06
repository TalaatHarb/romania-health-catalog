package net.talaatharb.healthcatalog.dto.xml;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;

@Data
public class PackageModes { 
	@JsonAlias("PackageMode")
	private List<PackageMode> packageModeList;
}
