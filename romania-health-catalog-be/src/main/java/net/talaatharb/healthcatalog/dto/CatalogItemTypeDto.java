package net.talaatharb.healthcatalog.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.talaatharb.healthcatalog.model.CatalogItemType;

/**
 * A searchable object type of a catalog version along with how many items of
 * that type are available
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CatalogItemTypeDto implements Serializable {

	private static final long serialVersionUID = 4675030874458153547L;

	private CatalogItemType type;
	private String label;
	private long count;
}
