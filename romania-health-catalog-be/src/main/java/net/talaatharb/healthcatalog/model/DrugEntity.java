package net.talaatharb.healthcatalog.model;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Drug search uses contains-matching (LIKE '%term%') which no B-tree index can seek into. The search index narrows
 * the scan to the active drugs of one version and holds name and code, so LIKE is evaluated on the compact index
 * entries instead of loading every full row of the version.
 */
@Entity
@Table(name = "drug_entity", indexes = {
		@Index(name = "idx_drug_search", columnList = "version_id,valid_to,name,code") })
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
