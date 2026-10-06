package net.talaatharb.healthcatalog.dto;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.talaatharb.healthcatalog.model.CatalogItemType;

@Data
@EqualsAndHashCode(callSuper = false)
public class CatalogItemDto extends BaseDto {

	private static final long serialVersionUID = -2286645437151457720L;

	private CatalogItemType type;
	private String label;
	private String code;
	private String name;
	private Map<String, Object> details = new LinkedHashMap<>();
}
