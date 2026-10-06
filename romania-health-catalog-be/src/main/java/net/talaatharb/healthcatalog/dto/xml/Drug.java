package net.talaatharb.healthcatalog.dto.xml;

import java.util.Date;

import lombok.Data;

@Data
public class Drug { 
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
}
