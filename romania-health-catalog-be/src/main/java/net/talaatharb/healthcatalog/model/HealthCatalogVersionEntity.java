package net.talaatharb.healthcatalog.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class HealthCatalogVersionEntity extends BaseEntity {

	private Instant issueDate;
	
	@OneToMany(mappedBy = "version", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	private List<DrugEntity> drugs = new ArrayList<>();
}
