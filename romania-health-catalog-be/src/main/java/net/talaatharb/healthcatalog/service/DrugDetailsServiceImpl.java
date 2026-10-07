package net.talaatharb.healthcatalog.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.talaatharb.healthcatalog.dto.DrugDetailsDto;
import net.talaatharb.healthcatalog.model.CatalogItemEntity;
import net.talaatharb.healthcatalog.model.CatalogItemType;
import net.talaatharb.healthcatalog.model.DrugEntity;

@RequiredArgsConstructor
@Service
@Slf4j
public class DrugDetailsServiceImpl implements DrugDetailsService {

	private static final String NEED_APPROVAL = "needApproval";

	private final CatalogItemService catalogItemService;

	@Override
	public DrugDetailsDto getDetails(DrugEntity drug) {
		UUID versionId = drug.getVersion().getId();
		var details = new DrugDetailsDto();
		details.setDrugId(drug.getId());

		var restrictions = details.getRestrictions();
		restrictions.setNarcotic(drug.getIsNarcotic() != null && drug.getIsNarcotic() > 0);
		restrictions.setSpecial(Boolean.TRUE.equals(drug.getIsSpecial()));
		restrictions.setPrescriptionMode(drug.getPrescriptionMode());

		var pricing = details.getPricing();
		pricing.setPricePerPackage(drug.getPricePerPackage());
		pricing.setWholeSalePricePerPackage(drug.getWholeSalePricePerPackage());

		if (drug.getAtc() != null && !drug.getAtc().isBlank()) {
			var atc = new DrugDetailsDto.Atc();
			atc.setCode(drug.getAtc());
			catalogItemService.findByCode(versionId, CatalogItemType.ATC, drug.getAtc()).stream().findFirst()
					.ifPresent(item -> atc.setDescription(item.getName()));
			details.setAtc(atc);
		}

		boolean needsApproval = false;
		for (CatalogItemEntity item : catalogItemService.findByName(versionId, CatalogItemType.COPAYMENT_LIST_DRUG,
				drug.getCode())) {
			needsApproval |= addInsurance(details, versionId, item, "DRUG");
			var itemDetails = item.getDetails();
			if (pricing.getMaxPrice() == null) {
				pricing.setMaxPrice(asDouble(itemDetails.get("maxPrice")));
			}
			if (pricing.getReferencePrice() == null) {
				pricing.setReferencePrice(asDouble(itemDetails.get("referencePrice")));
			}
		}
		for (CatalogItemEntity item : catalogItemService.findByName(versionId,
				CatalogItemType.COPAYMENT_LIST_ACTIVE_SUBSTANCE, drug.getActiveSubstance())) {
			needsApproval |= addInsurance(details, versionId, item, "ACTIVE_SUBSTANCE");
		}
		restrictions.setNeedsApproval(needsApproval);

		for (CatalogItemEntity protocol : catalogItemService.findAllOfType(versionId,
				CatalogItemType.COPAYMENT_LIST_PROTOCOL_THERAPEUTIC)) {
			Map<String, Object> protocolDetails = protocol.getDetails();
			boolean byAtc = drug.getAtc() != null && drug.getAtc().equals(protocolDetails.get("atc"));
			boolean bySubstance = drug.getActiveSubstance() != null
					&& drug.getActiveSubstance().equals(protocolDetails.get("activeSubstance"));
			if (byAtc || bySubstance) {
				details.getProtocols().add(protocolDetails);
			}
		}
		log.debug("Drug {} has {} insurance lists and {} protocols", drug.getId(), details.getInsurance().size(),
				details.getProtocols().size());
		return details;
	}

	private boolean addInsurance(DrugDetailsDto details, UUID versionId, CatalogItemEntity item, String source) {
		var insurance = new DrugDetailsDto.Insurance();
		insurance.setSource(source);
		insurance.setListCode(item.getCode());
		insurance.setDetails(item.getDetails());
		List<CatalogItemEntity> types = new ArrayList<>(
				catalogItemService.findByCode(versionId, CatalogItemType.COPAYMENT_LIST_TYPE, item.getCode()));
		types.stream().filter(Objects::nonNull).findFirst().ifPresent(type -> {
			insurance.setListDescription(type.getName());
			Object percent = type.getDetails().get("percent");
			if (percent instanceof Number number) {
				insurance.setPercent(number.intValue());
			}
		});
		details.getInsurance().add(insurance);
		return "1".equals(String.valueOf(item.getDetails().get(NEED_APPROVAL)))
				|| "true".equalsIgnoreCase(String.valueOf(item.getDetails().get(NEED_APPROVAL)))
				|| "Y".equalsIgnoreCase(String.valueOf(item.getDetails().get(NEED_APPROVAL)));
	}

	private static Double asDouble(Object value) {
		return value instanceof Number number ? number.doubleValue() : null;
	}
}
