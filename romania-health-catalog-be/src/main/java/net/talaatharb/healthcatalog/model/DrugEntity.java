package net.talaatharb.healthcatalog.model;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class DrugEntity extends BaseEntity {

	private String code;
	private String name;
	private String presentationMode;
	private Integer isNarcotic;
	private Boolean isFractional;
	private Boolean isSpecial;
	private Boolean isBrand;
	private Boolean hasBioEchiv;
	private Integer qtyPerPackage;
	private Double pricePerPackage;
	private Double wholeSalePricePerPackage;
	private String prescriptionMode;
	private Date validFrom;
	private Date validTo;
	private String activeSubstance;
	private String concentration;
	private String pharmaceuticalForm;
	private String company;
	private String country;
	private String atc;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private HealthCatalogVersionEntity version;
}
