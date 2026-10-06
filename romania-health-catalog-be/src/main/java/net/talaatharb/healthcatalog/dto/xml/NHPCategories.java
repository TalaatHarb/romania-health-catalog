package net.talaatharb.healthcatalog.dto.xml;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;

@Data
public class NHPCategories {
	@JsonAlias("NHPCategory")
	private List<NHPCategory> nhpCategoryList;
}
