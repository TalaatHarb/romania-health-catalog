package net.talaatharb.healthcatalog.service;

import net.talaatharb.healthcatalog.dto.DrugDetailsDto;
import net.talaatharb.healthcatalog.model.DrugEntity;

public interface DrugDetailsService {

	/**
	 * Gathers the additional information of a drug from the other items of its catalog version
	 */
	DrugDetailsDto getDetails(DrugEntity drug);
}
