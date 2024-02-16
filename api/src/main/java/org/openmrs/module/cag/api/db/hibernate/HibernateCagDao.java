package org.openmrs.module.cag.api.db.hibernate;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.cag.api.db.CagDao;
import org.openmrs.module.cag.cag.*;

import java.io.Serializable;
import java.util.*;

public class HibernateCagDao implements CagDao {
	
	private DbSessionFactory sessionFactory;
	
	public DbSession getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	public void setSessionFactory(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	public Cag getCagById(Integer cagId) {
		Transaction tx = getSession().beginTransaction();
		
		Cag cag = (Cag) getSession().get(Cag.class, cagId);
		
		if (!tx.wasCommitted())
			tx.commit();
		
		return cag;
	}
	
	@Override
	public List<Cag> getCagList() {
		Transaction tx = getSession().beginTransaction();
		
		Query query = getSession().createQuery("from cag c where c.voided= :voided");
		query.setInteger("voided", 0);
		List<Cag> cagList = query.list();
		
		if (!tx.wasCommitted())
			tx.commit();
		
		return cagList;
	}
	
	@Override
	public void saveCag(Cag cag) {
		Transaction tx = getSession().beginTransaction();
		getSession().save(cag);
		if (!tx.wasCommitted())
			tx.commit();
	}
	
	@Override
	public Cag getCagByUuid(String uuid) {
		Transaction tx = getSession().beginTransaction();
		
		Query query = getSession().createQuery("from cag c where c.uuid=:uuid and c.voided=:voided");
		query.setInteger("voided", 0);
		query.setString("uuid", uuid);
		Cag cag = (Cag) query.uniqueResult();
		
		if (!tx.wasCommitted())
			tx.commit();
		return cag;
	}
	
	@Override
	public Cag updateCag(Cag cag) {
		Transaction tx = getSession().beginTransaction();
		
		Query query = getSession()
		        .createQuery(
		            "update cag c set c.name=:name, c.description=:description, c.village=:village, c.constituency=:constituency, c.district=:district, c.dateChanged=:date_changed, c.changedBy=:changed_by where c.uuid=:uuid and c.voided=:voided");
		query.setString("name", cag.getName());
		query.setString("description", cag.getDescription());
		query.setString("village", cag.getVillage());
		query.setString("constituency", cag.getConstituency());
		query.setString("district", cag.getDistrict());
		query.setDate("date_changed", cag.getDateChanged());
		query.setParameter("changed_by", cag.getChangedBy());
		query.setInteger("voided", 0);
		query.setString("uuid", cag.getUuid());
		query.executeUpdate();
		
		if (!tx.wasCommitted())
			tx.commit();
		
		return getCagByUuid(cag.getUuid());
	}
	
	@Override
	public void deleteCag(String uuid) {
		
		clearCag(getCagByUuid(uuid).getId());
		
		Transaction tx = getSession().beginTransaction();
		
		Query query = getSession().createQuery("update cag c set c.voided=:voided where c.uuid=:uuid");
		query.setInteger("voided", 1);
		query.setString("uuid", uuid);
		query.executeUpdate();
		
		if (!tx.wasCommitted())
			tx.commit();
	}
	
	@Override
	public void clearCag(Integer cagId) {
		Transaction tx = getSession().beginTransaction();
		
		Query query = getSession().createQuery("update cag_patient set status=:status where cag_id=:cag_id");
		query.setInteger("status", 0);
		query.setInteger("cag_id", cagId);
		query.executeUpdate();
		
		if (!tx.wasCommitted())
			tx.commit();
	}
	
	@Override
	public CagPatient getCagPatientById(Integer cagPatientId) {
		Transaction tx = getSession().beginTransaction();
		
		Query query = getSession().createQuery("from cag_patient cp where cp.cagPatientId=:id and cp.status=:status");
		query.setBoolean("status", true);
		query.setInteger("id", cagPatientId);
		CagPatient cagPatient = (CagPatient) query.uniqueResult();
		
		if (!tx.wasCommitted())
			tx.commit();
		return cagPatient;
	}
	
	@Override
	public List<Patient> getCagPatientList(Integer cagId) {
		
		Cag cag = new Cag(cagId);
		Transaction tx = getSession().beginTransaction();
		
		Query query = getSession().createQuery(
		    "select p from cag_patient cp join cp.patient p where cp.cag=:cag and cp.status=:status");
		query.setBoolean("status", true);
		query.setParameter("cag", cag);
		List<Patient> patients = query.list();
		
		if (!tx.wasCommitted())
			tx.commit();
		
		return patients;
		
		//		List<Integer> idList = getPatientIdList(cagId);
		//		List<Patient> emptyList = Collections.<Patient> emptyList();
		//		if (!idList.isEmpty()) {
		//			PatientService patientService = Context.getPatientService();
		//			List<Patient> cagPatientList = new ArrayList<Patient>();
		//
		//			for (Integer patatientId : idList) {
		//				cagPatientList.add(patientService.getPatient(patatientId));
		//			}
		//
		//			return cagPatientList;
		//		}
		//
		//		return emptyList;
	}
	
	@Override
	public List<Integer> getPatientIdList(Integer cagId) {
		
		Transaction tx = getSession().beginTransaction();
		Query query = getSession().createQuery(
		    "select p.patient_id from cag_patient p where p.status=:status and p.cag_id=:cagId");
		query.setBoolean("status", true);
		query.setInteger("cagId", cagId);
		List<Integer> idList = query.list();
		if (!tx.wasCommitted())
			tx.commit();
		
		return idList;
	}
	
	@Override
	public void saveCagPatient(CagPatient cag_patient) {
		Transaction tx = getSession().beginTransaction();
		
		getSession().save(cag_patient);
		
		if (!tx.wasCommitted())
			tx.commit();
	}
	
	@Override
	public void deletePatientFromCag(Integer patientId) {
		
		Patient patient = new Patient(patientId);
		Transaction tx = getSession().beginTransaction();
		
		Query query = getSession().createQuery(
		    "update cag_patient cp set cp.status=:inactive where cp.patient=:patient and cp.status=:isActive");
		query.setInteger("inactive", 0);
		query.setInteger("isActive", 1);
		query.setParameter("patient", patient);
		query.executeUpdate();
		
		if (!tx.wasCommitted())
			tx.commit();
	}
	
	@Override
	public CagPatient getCagPatientByUuid(String uuid) {
		
		Transaction tx = getSession().beginTransaction();
		
		Query query = getSession().createQuery("from cag_patient cp where cp.uuid=:uuid and cp.status=:status");
		query.setInteger("status", 1);
		query.setString("uuid", uuid);
		CagPatient cagPatient = (CagPatient) query.uniqueResult();
		
		if (!tx.wasCommitted())
			tx.commit();
		
		return cagPatient;
	}
	
	@Override
	public List<CagPatient> getActiveCagVisitByAttender(Patient attender) {
		System.out.println("attender : " + attender);
		Transaction tx = getSession().beginTransaction();
		
		Query query = getSession().createQuery(
		    "from cag_visit cv where cv.attender=:attender and cv.dateStopped is NULL and cv.voided=0");
		query.setParameter("attender", attender);
		List<CagPatient> activeCagVisit = (List<CagPatient>) query.list();
		
		if (!tx.wasCommitted())
			tx.commit();
		
		return activeCagVisit;
	}
	
	@Override
	public CagVisit saveCagVisit(CagVisit cagVisit) {
		Transaction tx = getSession().beginTransaction();
		
		getSession().save(cagVisit);
		
		if (!tx.wasCommitted())
			tx.commit();
		
		return getCagVisitByUuid(cagVisit.getUuid());
	}
	
	@Override
	public CagVisit getCagVisitByUuid(String uuid) {
		Transaction tx = getSession().beginTransaction();
		
		Query query = getSession().createQuery("from cag_visit cv where cv.uuid=:uuid and cv.voided=:voided");
		query.setInteger("voided", 0);
		query.setString("uuid", uuid);
		CagVisit cagVisit = (CagVisit) query.uniqueResult();
		
		if (!tx.wasCommitted())
			tx.commit();
		
		return cagVisit;
	}
	
	public List<CagVisit> getCagVisitList() {
		Transaction tx = getSession().beginTransaction();
		
		Query query = getSession().createQuery("from cag_visit cv where cv.voided=:voided");
		query.setInteger("voided", 0);
		List<CagVisit> cagVisits = query.list();
		
		if (!tx.wasCommitted())
			tx.commit();
		
		return cagVisits;
	}
	
	@Override
	public List<CagVisit> getAttenderActiveCagVisitList(Patient attender) {
		Transaction tx = getSession().beginTransaction();
		
		Query query = getSession().createQuery(
		    "from cag_visit cv where cv.attender=:attender and cv.voided=:voided and cv.dateStopped is null");
		query.setInteger("voided", 0);
		query.setParameter("attender", attender);
		List<CagVisit> cagVisits = (List<CagVisit>) query.list();
		
		if (!tx.wasCommitted())
			tx.commit();
		
		return cagVisits;
	}
	
	public CagVisit getCagVisitById(Integer id) {
		Transaction tx = getSession().beginTransaction();
		
		CagVisit cagVisit = (CagVisit) getSession().get(CagVisit.class, id);
		
		if (!tx.wasCommitted())
			tx.commit();
		
		return cagVisit;
	}
	
	@Override
	public void deleteCagVisit(Integer cagVisitId) {
		Transaction tx = getSession().beginTransaction();
		
		Query query = getSession().createQuery(
		    "update cag_visit cv set cv.voided=:inactive where cv.id=:visitId and cv.voided=:active");
		query.setInteger("active", 0);
		query.setInteger("inactive", 1);
		query.setInteger("visitId", cagVisitId);
		query.executeUpdate();
		
		if (!tx.wasCommitted())
			tx.commit();
	}
	
	@Override
	public List<Visit> getCagVisits(Integer cagId) {
		return null;
	}
	
	@Override
	public void saveAbsentCagPatient(Absentee absentee) {
		Transaction tx = getSession().beginTransaction();
		
		getSession().save(absentee);
		
		if (!tx.wasCommitted())
			tx.commit();
	}
	
	@Override
	public List<Absentee> getAbsenteeList(Integer cagVisitId) {
		Transaction tx = getSession().beginTransaction();
		
		Query query = getSession().createQuery("from missed_drug_pickup  where cagVisitId=:cagVisitId");
		query.setInteger("cagVisitId", cagVisitId);
		List<Absentee> absenteeList = query.list();
		
		if (!tx.wasCommitted())
			tx.commit();
		
		return absenteeList;
	}
	
	@Override
	public void closeCagPatientVisit(Patient patient, String startDate, String dateStopped) {
		Transaction tx = getSession().beginTransaction();
		String uuid = "";
		
		Query query = getSession()
		        .createQuery(
		            "update Visit v set v.stopDatetime=:dateStopped where v.patient = :patient and v.startDatetime = :startDate and v.voided=:active");
		query.setString("dateStopped", dateStopped);
		query.setString("startDate", startDate);
		query.setInteger("active", 0);
		query.setParameter("patient", patient);
		query.executeUpdate();
		
		if (!tx.wasCommitted())
			tx.commit();
		
	}
	
	@Override
	public CagVisit closeCagVisit(String cagVisitUuid, String dateStopped) {
		Transaction tx = getSession().beginTransaction();
		
		Query query = getSession().createQuery(
		    "update cag_visit cv set cv.dateStopped=:dateStopped where cv.uuid=:uuid and cv.voided=:active");
		query.setString("dateStopped", dateStopped);
		query.setInteger("active", 0);
		query.setString("uuid", cagVisitUuid);
		query.executeUpdate();
		
		if (!tx.wasCommitted())
			tx.commit();
		
		return getCagVisitByUuid(cagVisitUuid);
	}
	
	@Override
	public Visit getVisit(Patient patient, String dateStarted) {
		
		Transaction tx = getSession().beginTransaction();
		
		Query query = getSession()
		        .createQuery(
		            "from Visit v where v.patient=:patient and v.voided=:voided and v.startDatetime=:dateStarted and v.stopDatetime IS NULL");
		query.setBoolean("voided", false);
		query.setString("dateStarted", dateStarted);
		//		query.setString("dateStopped", null);
		query.setParameter("patient", patient);
		Visit visit = (Visit) query.uniqueResult();
		
		if (!tx.wasCommitted())
			tx.commit();
		
		return visit;
	}
	
	@Override
	public CagEncounter getCagEncounterByUuid(String uuid) {
		Transaction tx = getSession().beginTransaction();
		
		Query query = getSession().createQuery("from cag_encounter ce where ce.uuid=:uuid and ce.voided=:voided");
		query.setInteger("voided", 0);
		query.setString("uuid", uuid);
		CagEncounter cagEncounter = (CagEncounter) query.uniqueResult();
		if (cagEncounter == null)
			System.out.println("Did not find the cagEncouter!!!");
		
		if (!tx.wasCommitted())
			tx.commit();
		
		return cagEncounter;
	}
	
	@Override
	public void saveCagEncounter(CagEncounter cagEncounter) {
		
		Transaction tx = getSession().beginTransaction();
		
		getSession().save(cagEncounter);
		
		if (!tx.wasCommitted())
			tx.commit();
	}
	
	@Override
	public void saveCagPatientEncounter(Encounter encounter) {
		Transaction tx = getSession().beginTransaction();
		
		getSession().save(encounter);
		
		if (!tx.wasCommitted())
			tx.commit();
	}
	
	@Override
	public void updateCagEncounter(String cagEncounterUuid, Location location, Date encounterDateTime,
	        Date nextEncounterDateTime) {
		Transaction tx = getSession().beginTransaction();
		
		Query query = getSession().createQuery(
		    "update cag_encounter ce set ce.location=:location,"
		            + " ce.cagEncounterDateTime=:encounterDateTime, ce.nextEncounterDate=:nextEncounterDateTime"
		            + " where ce.uuid=:cagEncounterUuid");
		query.setString("cagEncounterUuid", cagEncounterUuid);
		query.setParameter("encounterDateTime", encounterDateTime);
		query.setParameter("nextEncounterDateTime", nextEncounterDateTime);
		query.setParameter("location", location);
		query.executeUpdate();
		
		//		Query query2 = getSession().createQuery(
		//				"update Encounter e set e.location=:location, e.encounterDatetime=:encounterDateTime"
		//						+ " where ce.uuid=:cagEncounterUuid");
		//		query2.setString("cagEncounterUuid", cagEncounterUuid);
		//		query2.setParameter("encounterDateTime", encounterDateTime);
		//		query2.setParameter("nextEncounterDateTime", nextEncounterDateTime);
		//		query2.setParameter("location", location);
		//		query2.executeUpdate();
		
		if (!tx.wasCommitted())
			tx.commit();
	}
	
	@Override
	public void deleteCagEncounter(CagEncounter cagEncounter) {
		System.out.println("\n=========Executing DAO deleteCagEncounter=========\n");
		Transaction tx = getSession().beginTransaction();
		
		getSession().update(cagEncounter);
		
		if (!tx.wasCommitted())
			tx.commit();
		
	}
	
	public List<CagPatient> getAllCagPatients() {
		Transaction tx = getSession().beginTransaction();
		Query query = getSession().createQuery("From cag_patient");
		List<CagPatient> cagPatientList = query.list();
		if (!tx.wasCommitted())
			tx.commit();
		return cagPatientList;
	}
}
