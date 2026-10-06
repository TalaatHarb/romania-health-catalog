package net.talaatharb.healthcatalog.model;

import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Generic storage for every catalog object other than drugs (cities, streets,
 * physicians, ...). The searchable code/name are stored in dedicated columns
 * and the full set of XML attributes is kept as JSON details.
 */
@Entity
@Table(name = "catalog_item_entity", indexes = {
		@Index(name = "idx_catalog_item_version_type", columnList = "version_id,type") })
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class CatalogItemEntity extends GeneratedIdBaseEntity {

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 64)
	private CatalogItemType type;

	@Column(length = 1000)
	private String code;

	@Column(length = 4000)
	private String name;

	@Convert(converter = JsonMapConverter.class)
	@Column(length = 16000)
	private Map<String, Object> details = new LinkedHashMap<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@EqualsAndHashCode.Exclude
	private HealthCatalogVersionEntity version;
}
