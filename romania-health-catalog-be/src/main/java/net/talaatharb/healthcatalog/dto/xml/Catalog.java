package net.talaatharb.healthcatalog.dto.xml;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement(name = "Catalogues")
@XmlAccessorType(XmlAccessType.FIELD)
public class Catalog {
	@JsonAlias("Countries")
	private Countries countries;
	@JsonAlias("CnasAgreements")
	private CnasAgreements cnasAgreements;
	@JsonAlias("EuMembers")
	private EuMembers euMembers;
	@JsonAlias("Districts")
	private Districts districts;
	@JsonAlias("CityTypes")
	private CityTypes cityTypes;
	@JsonAlias("Cities")
	private Cities cities;
	@JsonAlias("Street_Types")
	private StreetTypes streetTypes;
	@JsonAlias("Streets")
	private Streets streets;
	@JsonAlias("InsuranceHouseTypes")
	private InsuranceHouseTypes insuranceHouseTypes;
	@JsonAlias("InsuranceHouses")
	private InsuranceHouses insuranceHouses;
	@JsonAlias("PharmaceuticalForms")
	private PharmaceuticalForms pharmaceuticalForms;
	@JsonAlias("Concentrations")
	private Concentrations concentrations;
	@JsonAlias("Specialities")
	private Specialities specialities;
	@JsonAlias("Errors")
	private Errors errors;
	@JsonAlias("BusinessRules")
	private BusinessRules businessRules;
	@JsonAlias("PersonStates")
	private PersonStates personStates;
	@JsonAlias("PersonCategories")
	private PersonCategories personCategories;
	@JsonAlias("PrescriptionTypes")
	private PrescriptionTypes prescriptionTypes;
	@JsonAlias("StockPaperTypes")
	private StockPaperTypes stockPaperTypes;
	@JsonAlias("Physicians")
	private Physicians physicians;
	@JsonAlias("PhysicianSpecialities")
	private PhysicianSpecialities physicianSpecialities;
	@JsonAlias("NHPS")
	private NHPS nhps;
	@JsonAlias("DiseaseCategories")
	private DiseaseCategories diseaseCategories;
	@JsonAlias("ICD10S")
	private ICD10S icd10s;
	@JsonAlias("Cim10s")
	private Cim10s cim10s;
	@JsonAlias("ActiveSubstances")
	private ActiveSubstances activeSubstances;
	@JsonAlias("ActSubstICD10s")
	private Object actSubstICD10s;
	@JsonAlias("CopaymentListTypes")
	private CopaymentListTypes copaymentListTypes;
	@JsonAlias("CopaymentListTypePersState")
	private CopaymentListTypePersState copaymentListTypePersState;
	@JsonAlias("ATCS")
	private ATCS atcs;
	@JsonAlias("Goods")
	private Goods goods;
	@JsonAlias("MedicalTests")
	private Object medicalTests;
	@JsonAlias("MedicalDevices")
	private Object medicalDevices;
	@JsonAlias("PackageModes")
	private PackageModes packageModes;
	@JsonAlias("DocumentsFormEu")
	private DocumentsFormEu documentsFormEu;
	@JsonAlias("DocumentTypes")
	private DocumentTypes documentTypes;
	@JsonAlias("Drugs")
	private Drugs drugs;
	@JsonAlias("CopaymentListDrugs")
	private CopaymentListDrugs copaymentListDrugs;
	@JsonAlias("CopaymentListProtocolTherapeutics")
	private CopaymentListProtocolTherapeutics copaymentListProtocolTherapeutics;
	@JsonAlias("CopaymentListActiveSubsts")
	private CopaymentListActiveSubsts copaymentListActiveSubsts;
	@JsonAlias("HealthDepartmentTypes")
	private HealthDepartmentTypes healthDepartmentTypes;
	@JsonAlias("HealthDepartments")
	private HealthDepartments healthDepartments;
	@JsonAlias("BlackList")
	private BlackList blackList;
	@JsonAlias("MissingPrescriptions")
	private MissingPrescriptions missingPrescriptions;
	@JsonAlias("Holidays")
	private Holidays holidays;
	@JsonAlias("NHPCategories")
	private NHPCategories nhpCategories;
	@JsonAlias("NHP_DRUG_TYPES")
	private NHPDrugTypes nhpDrugTypes;
	@JsonAlias("NHP_DRUGS")
	private NHPDrugs nhpDrugs;
	@JsonAlias("CtrDocumentTypes")
	private CtrDocumentTypes ctrDocumentTypes;
	@JsonAlias("PersonFunctions")
	private PersonFunctions personFunctions;
	@JsonAlias("EmplTypes")
	private EmplTypes employeeTypes;
	@JsonAlias("InvoiceItems")
	private InvoiceItems invoiceItems;
	private Date issueDate;
}
