package net.talaatharb.healthcatalog.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import lombok.Data;

/**
 * Additional, optional information about a drug, loaded separately from the drug itself to enrich its view.
 */
@Data
public class DrugDetailsDto implements Serializable {
	private static final long serialVersionUID = 4021788127458112005L;

	private UUID drugId;
	private Atc atc;
	private Restrictions restrictions = new Restrictions();
	private Pricing pricing = new Pricing();
	private Set<Insurance> insurance = new LinkedHashSet<>();
	private List<Map<String, Object>> protocols = new ArrayList<>();

	@Data
	public static class Atc implements Serializable {
		private static final long serialVersionUID = 1L;
		private String code;
		private String description;
	}

	@Data
	public static class Restrictions implements Serializable {
		private static final long serialVersionUID = 1L;
		private boolean narcotic;
		private boolean special;
		private String prescriptionMode;
		private boolean needsApproval;
	}

	@Data
	public static class Pricing implements Serializable {
		private static final long serialVersionUID = 1L;
		private Double pricePerPackage;
		private Double wholeSalePricePerPackage;
		private Double maxPrice;
		private Double referencePrice;
	}

	/**
	 * A copayment list the drug (or its active substance) belongs to
	 */
	@Data
	public static class Insurance implements Serializable {
		private static final long serialVersionUID = 1L;
		private String listCode;
		private String listDescription;
		private Integer percent;
		private String source;
		private Map<String, Object> details = new LinkedHashMap<>();
	}
}
