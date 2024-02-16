package org.openmrs.module.cag.api;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.cag.cag.Cag;
import org.openmrs.module.cag.cag.CagEncounter;
import org.openmrs.module.cag.cag.CagPatient;
import org.openmrs.module.cag.cag.CagVisit;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.CDATASection;

public interface CagService extends OpenmrsService {
	
	//	Methods acting on Cag
	//	@Transactional(readOnly = true)
	Cag getCagById(Integer cagId);
	
	//	@Transactional(readOnly = true)
	Cag getCagByUuid(String uuid);
	
	//	@Transactional(readOnly = true)
	List<Cag> getCagList();
	
	//	@Transactional
	void saveCag(Cag cag);
	
	//	@Transactional
	void voidCag(Cag cag);
	
	//	Handles CagPatient Transactions
	//	@Transactional(readOnly = true)
	CagPatient getCagPatientById(Integer cagPatientId); //find patient method
	
	//	@Transactional(readOnly = true)
	List<Patient> getCagPatientList(Integer id); //read cag patients as list
	
	//	@Transactional
	Patient saveCagPatient(CagPatient cagPatient); //save cag patient
	
	//	@Transactional
	void deletePatientFromCag(String uuid); //set status for cag member
	
	//	@Transactional
	void deleteCag(String uuid);
	
	Cag updateCag(Cag cag);
	
	CagPatient getCagPatientByUuid(String uuid);
	
	CagPatient getActiveCagVisitByAttender(String uuid);
	
	//	CagVisit saveCagVisit(CagVisit cagVisit);
	
	CagVisit openCagVisit(CagVisit cagVisit);
	
	CagVisit getCagVisitByUuid(String uuid);
	
	List<CagVisit> getCagVisitList();
	
	void deleteCagVisit(String uuid);
	
	public CagVisit closeCagVisit(String uuid, String dateStopped);
	
	CagEncounter getCagEncounterByUuid(String uuid);
	
	CagEncounter saveCagEncounter(CagEncounter cagEncounter);
	
	CagEncounter updateCagEncounter(String uuid, String locationUuid, Date encounterDateTime, Date nextEncounterDateTime);
	
	void deleteCagEncounter(String uuid);
	
}
