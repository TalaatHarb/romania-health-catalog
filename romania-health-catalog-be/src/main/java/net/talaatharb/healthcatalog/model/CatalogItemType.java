package net.talaatharb.healthcatalog.model;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import lombok.Getter;
import net.talaatharb.healthcatalog.dto.xml.ATCS;
import net.talaatharb.healthcatalog.dto.xml.ActiveSubstances;
import net.talaatharb.healthcatalog.dto.xml.BlackList;
import net.talaatharb.healthcatalog.dto.xml.BusinessRules;
import net.talaatharb.healthcatalog.dto.xml.Catalog;
import net.talaatharb.healthcatalog.dto.xml.Cim10s;
import net.talaatharb.healthcatalog.dto.xml.Cities;
import net.talaatharb.healthcatalog.dto.xml.CityTypes;
import net.talaatharb.healthcatalog.dto.xml.CnasAgreements;
import net.talaatharb.healthcatalog.dto.xml.Concentrations;
import net.talaatharb.healthcatalog.dto.xml.CopaymentListActiveSubsts;
import net.talaatharb.healthcatalog.dto.xml.CopaymentListDrugs;
import net.talaatharb.healthcatalog.dto.xml.CopaymentListProtocolTherapeutics;
import net.talaatharb.healthcatalog.dto.xml.CopaymentListTypePersState;
import net.talaatharb.healthcatalog.dto.xml.CopaymentListTypes;
import net.talaatharb.healthcatalog.dto.xml.Countries;
import net.talaatharb.healthcatalog.dto.xml.CtrDocumentTypes;
import net.talaatharb.healthcatalog.dto.xml.DiseaseCategories;
import net.talaatharb.healthcatalog.dto.xml.Districts;
import net.talaatharb.healthcatalog.dto.xml.DocumentTypes;
import net.talaatharb.healthcatalog.dto.xml.DocumentsFormEu;
import net.talaatharb.healthcatalog.dto.xml.EmplTypes;
import net.talaatharb.healthcatalog.dto.xml.Errors;
import net.talaatharb.healthcatalog.dto.xml.EuMembers;
import net.talaatharb.healthcatalog.dto.xml.Goods;
import net.talaatharb.healthcatalog.dto.xml.HealthDepartmentTypes;
import net.talaatharb.healthcatalog.dto.xml.HealthDepartments;
import net.talaatharb.healthcatalog.dto.xml.Holidays;
import net.talaatharb.healthcatalog.dto.xml.ICD10S;
import net.talaatharb.healthcatalog.dto.xml.InsuranceHouseTypes;
import net.talaatharb.healthcatalog.dto.xml.InsuranceHouses;
import net.talaatharb.healthcatalog.dto.xml.InvoiceItems;
import net.talaatharb.healthcatalog.dto.xml.MissingPrescriptions;
import net.talaatharb.healthcatalog.dto.xml.NHPCategories;
import net.talaatharb.healthcatalog.dto.xml.NHPDrugTypes;
import net.talaatharb.healthcatalog.dto.xml.NHPDrugs;
import net.talaatharb.healthcatalog.dto.xml.NHPS;
import net.talaatharb.healthcatalog.dto.xml.PackageModes;
import net.talaatharb.healthcatalog.dto.xml.PersonCategories;
import net.talaatharb.healthcatalog.dto.xml.PersonFunctions;
import net.talaatharb.healthcatalog.dto.xml.PersonStates;
import net.talaatharb.healthcatalog.dto.xml.PharmaceuticalForms;
import net.talaatharb.healthcatalog.dto.xml.PhysicianSpecialities;
import net.talaatharb.healthcatalog.dto.xml.Physicians;
import net.talaatharb.healthcatalog.dto.xml.PrescriptionTypes;
import net.talaatharb.healthcatalog.dto.xml.Specialities;
import net.talaatharb.healthcatalog.dto.xml.StockPaperTypes;
import net.talaatharb.healthcatalog.dto.xml.StreetTypes;
import net.talaatharb.healthcatalog.dto.xml.Streets;

/**
 * Registry of every object type available in the catalog XML file.
 * <p>
 * Each type knows its human readable label, which XML attributes act as its
 * code and name (used for searching and listing) and how to extract its items
 * from a parsed {@link Catalog}. Drugs are stored in a dedicated table, so
 * {@link #DRUG} has no generic extractor.
 */
@Getter
public enum CatalogItemType {

	DRUG("Drugs", "code", "name", null),
	COUNTRY("Countries", "code", "name", from(Catalog::getCountries, Countries::getCountryList)),
	DISTRICT("Districts", "code", "name", from(Catalog::getDistricts, Districts::getDistrictList)),
	CITY_TYPE("City types", "code", "name", from(Catalog::getCityTypes, CityTypes::getCityTypeList)),
	CITY("Cities", "code", "name", from(Catalog::getCities, Cities::getCityList)),
	STREET_TYPE("Street types", "code", "name", from(Catalog::getStreetTypes, StreetTypes::getStreetTypeList)),
	STREET("Streets", "code", "name", from(Catalog::getStreets, Streets::getStreetList)),
	PHYSICIAN("Physicians", "stencil", "name", from(Catalog::getPhysicians, Physicians::getPhysicianList)),
	PHYSICIAN_SPECIALITY("Physician specialities", "stencil", "specialityCode",
			from(Catalog::getPhysicianSpecialities, PhysicianSpecialities::getPhysicianSpecialityList)),
	SPECIALITY("Specialities", "code", "name", from(Catalog::getSpecialities, Specialities::getSpecialityList)),
	INSURANCE_HOUSE_TYPE("Insurance house types", "code", "description",
			from(Catalog::getInsuranceHouseTypes, InsuranceHouseTypes::getInsuranceHouseTypeList)),
	INSURANCE_HOUSE("Insurance houses", "code", "name",
			from(Catalog::getInsuranceHouses, InsuranceHouses::getInsuranceHouseList)),
	HEALTH_DEPARTMENT_TYPE("Health department types", "code", "description",
			from(Catalog::getHealthDepartmentTypes, HealthDepartmentTypes::getHealthDepartmentTypeList)),
	HEALTH_DEPARTMENT("Health departments", "code", "name",
			from(Catalog::getHealthDepartments, HealthDepartments::getHealthDepartmentList)),
	ACTIVE_SUBSTANCE("Active substances", "code", "code",
			from(Catalog::getActiveSubstances, ActiveSubstances::getActiveSubstanceList)),
	ATC("ATC codes", "code", "description", from(Catalog::getAtcs, ATCS::getAtcList)),
	PHARMACEUTICAL_FORM("Pharmaceutical forms", "code", "code",
			from(Catalog::getPharmaceuticalForms, PharmaceuticalForms::getPharmaceuticalFormList)),
	CONCENTRATION("Concentrations", "concentrationDescription", "concentrationDescription",
			from(Catalog::getConcentrations, Concentrations::getConcentrationList)),
	PACKAGE_MODE("Package modes", "code", "code", from(Catalog::getPackageModes, PackageModes::getPackageModeList)),
	GOOD("Goods", "code", "name", from(Catalog::getGoods, Goods::getGoodsList)),
	ICD10("ICD-10 diagnostics", "code", "name", from(Catalog::getIcd10s, ICD10S::getIcd10List)),
	CIM10("CIM-10 diagnostics", "code", "name", from(Catalog::getCim10s, Cim10s::getCim10List)),
	DISEASE_CATEGORY("Disease categories", "code", "description",
			from(Catalog::getDiseaseCategories, DiseaseCategories::getDiseaseCategoryList)),
	NHP("National health programs", "code", "description", from(Catalog::getNhps, NHPS::getNhpList)),
	NHP_CATEGORY("NHP categories", "code", "name",
			from(Catalog::getNhpCategories, NHPCategories::getNhpCategoryList)),
	NHP_DRUG_TYPE("NHP drug types", "code", "description",
			from(Catalog::getNhpDrugTypes, NHPDrugTypes::getNhpDrugTypeList)),
	NHP_DRUG("NHP drugs", "code", "code", from(Catalog::getNhpDrugs, NHPDrugs::getNhpDrugList)),
	COPAYMENT_LIST_TYPE("Copayment list types", "code", "description",
			from(Catalog::getCopaymentListTypes, CopaymentListTypes::getCopaymentListType)),
	COPAYMENT_LIST_TYPE_PERSON_STATE("Copayment list type person states", "copaymentListType", "personState",
			from(Catalog::getCopaymentListTypePersState, CopaymentListTypePersState::getCopaymentListTypePersonState)),
	COPAYMENT_LIST_DRUG("Copayment list drugs", "copaymentListType", "drug",
			from(Catalog::getCopaymentListDrugs, CopaymentListDrugs::getCopaymentListDrug)),
	COPAYMENT_LIST_ACTIVE_SUBSTANCE("Copayment list active substances", "copaymentListType", "activeSubstance",
			from(Catalog::getCopaymentListActiveSubsts, CopaymentListActiveSubsts::getCopaymentListActiveSubst)),
	COPAYMENT_LIST_PROTOCOL_THERAPEUTIC("Therapeutic protocols", "codeProtocolTherap", "descProtocolTherap",
			from(Catalog::getCopaymentListProtocolTherapeutics,
					CopaymentListProtocolTherapeutics::getCopaymentListProtocolTherapeutic)),
	PRESCRIPTION_TYPE("Prescription types", "code", "description",
			from(Catalog::getPrescriptionTypes, PrescriptionTypes::getPrescriptionTypeList)),
	MISSING_PRESCRIPTION("Missing prescriptions", "series", "type",
			from(Catalog::getMissingPrescriptions, MissingPrescriptions::getMissingPrescriptionList)),
	STOCK_PAPER_TYPE("Stock paper types", "code", "description",
			from(Catalog::getStockPaperTypes, StockPaperTypes::getStockPaperTypeList)),
	DOCUMENT_TYPE("Document types", "code", "description",
			from(Catalog::getDocumentTypes, DocumentTypes::getDocumentTypeList)),
	DOCUMENT_FORM_EU("EU form documents", "formEuCode", "formEuDesc",
			from(Catalog::getDocumentsFormEu, DocumentsFormEu::getDocumentFormEuList)),
	CTR_DOCUMENT_TYPE("Contract document types", "code", "name",
			from(Catalog::getCtrDocumentTypes, CtrDocumentTypes::getCtrDocumentTypeList)),
	PERSON_STATE("Person states", "code", "description",
			from(Catalog::getPersonStates, PersonStates::getPersonStateList)),
	PERSON_CATEGORY("Person categories", "code", "description",
			from(Catalog::getPersonCategories, PersonCategories::getPersonCategoryList)),
	PERSON_FUNCTION("Person functions", "code", "description",
			from(Catalog::getPersonFunctions, PersonFunctions::getPersonFunctionList)),
	EMPLOYEE_TYPE("Employee types", "code", "name", from(Catalog::getEmployeeTypes, EmplTypes::getEmployeeTypeList)),
	CNAS_AGREEMENT("CNAS agreements", "countryCode", "countryCode",
			from(Catalog::getCnasAgreements, CnasAgreements::getCnasAgreementList)),
	EU_MEMBER("EU members", "countryCode", "countryCode", from(Catalog::getEuMembers, EuMembers::getEuMemberList)),
	HOLIDAY("Holidays", "holidayDate", "description", from(Catalog::getHolidays, Holidays::getHolidayList)),
	BUSINESS_RULE("Business rules", "code", "description",
			from(Catalog::getBusinessRules, BusinessRules::getBusinessRuleList)),
	ERROR("Errors", "code", "text", from(Catalog::getErrors, Errors::getErrorList)),
	INVOICE_ITEM("Invoice items", "code", "description",
			from(Catalog::getInvoiceItems, InvoiceItems::getInvoiceItemList)),
	BLACK_LIST("Black list", "personPID", "personPID", from(Catalog::getBlackList, BlackList::getBlackListRows));

	private final String label;
	private final String codeProperty;
	private final String nameProperty;
	private final Function<Catalog, List<?>> extractor;

	CatalogItemType(String label, String codeProperty, String nameProperty, Function<Catalog, List<?>> extractor) {
		this.label = label;
		this.codeProperty = codeProperty;
		this.nameProperty = nameProperty;
		this.extractor = extractor;
	}

	/**
	 * @return whether this type is persisted in the generic catalog items storage
	 */
	public boolean isGeneric() {
		return extractor != null;
	}

	/**
	 * Extract the raw XML objects of this type from the given catalog
	 * 
	 * @param catalog the parsed catalog
	 * @return the list of XML objects, never null
	 */
	public List<?> extractFrom(Catalog catalog) {
		if (extractor == null || catalog == null) {
			return Collections.emptyList();
		}
		return Optional.ofNullable(extractor.apply(catalog)).orElse(Collections.emptyList());
	}

	private static <W> Function<Catalog, List<?>> from(Function<Catalog, W> wrapper,
			Function<W, ? extends List<?>> list) {
		return catalog -> Optional.ofNullable(wrapper.apply(catalog)).map(list).orElse(null);
	}
}
