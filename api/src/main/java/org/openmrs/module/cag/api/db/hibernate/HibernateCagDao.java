package org.openmrs.module.cag.api.db.hibernate;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Transaction;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.cag.api.db.CagDao;
import org.openmrs.module.cag.cag.*;

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
	public List<Cag> getAllCags() {
		Transaction tx = getSession().beginTransaction();
		Query query = getSession().createQuery("from cag c where c.voided= :voided");
		query.setInteger("voided", 0);
		
		List<Cag> cagList = (List<Cag>) query.list();
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
	public Cag getCagByPatientUuid(String uuid) {
		Transaction tx = getSession().beginTransaction();
		Query query = getSession().createQuery("from cag_patient cp where cp.uuid=:uuid and cp.status=1");
		query.setString("uuid", uuid);
		CagPatient cagPatient = (CagPatient) query.uniqueResult();
		
		if (!tx.wasCommitted())
			tx.commit();
		
		return (cagPatient != null) ? cagPatient.getCag() : null;
	}
	
	@Override
	public Cag updateCag(Cag cag) {
		Transaction tx = getSession().beginTransaction();
		Query query = getSession().createQuery(
		    "update cag c set c.name=:name, c.description=:description, c.village=:village,"
		            + " c.constituency=:constituency, c.district=:district, c.dateChanged=:date_changed,"
		            + " c.changedBy=:changed_by where c.uuid=:uuid and c.voided=false");
		query.setString("name", cag.getName());
		query.setString("description", cag.getDescription());
		query.setString("village", cag.getVillage());
		query.setString("constituency", cag.getConstituency());
		query.setString("district", cag.getDistrict());
		query.setDate("date_changed", cag.getDateChanged());
		query.setParameter("changed_by", cag.getChangedBy());
		query.setString("uuid", cag.getUuid());
		query.executeUpdate();
		
		if (!tx.wasCommitted())
			tx.commit();
		return getCagByUuid(cag.getUuid());
	}
	
	@Override
	public void deleteCag(String cagUuid) {
		Transaction tx = getSession().beginTransaction();
		SQLQuery query = getSession()
		        .createSQLQuery(
		            "update cag c join cag_patient cp on c.cag_id=cp.cag_id set c.voided=1, cp.status=0 "
		                    + " where c.uuid=:cagUuid");
		query.setString("cagUuid", cagUuid);
		query.executeUpdate();
		
		if (!tx.wasCommitted())
			tx.commit();
	}
	
	@Override
	public CagPatient getCagPatientById(Integer cagPatientId) {
		Transaction tx = getSession().beginTransaction();
		Query query = getSession().createQuery("from cag_patient cp where cp.cagPatientId=:id and cp.status=true");
		query.setInteger("id", cagPatientId);
		CagPatient cagPatient = (CagPatient) query.uniqueResult();
		
		if (!tx.wasCommitted())
			tx.commit();
		return cagPatient;
	}
	
	@Override
	public List<Patient> getAllCagPatients(Integer cagId) {
		Cag cag = new Cag(cagId);
		Transaction tx = getSession().beginTransaction();
		Query query = getSession().createQuery(
		    "select p from cag_patient cp join cp.patient p where cp.cag=:cag and cp.status=true");
		query.setParameter("cag", cag);
		List<Patient> patients = (List<Patient>) query.list();
		
		if (!tx.wasCommitted())
			tx.commit();
		return patients;
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
		    "update cag_patient cp set cp.status=false where cp.patient=:patient and cp.status=true");
		query.setParameter("patient", patient);
		query.executeUpdate();
		
		if (!tx.wasCommitted())
			tx.commit();
	}
	
	@Override
	public CagPatient getCagPatientByUuid(String cagPatientUuid) {
		Transaction tx = getSession().beginTransaction();
		Query query = getSession().createQuery("from cag_patient cp where cp.uuid=:uuid and cp.status=true");
		query.setString("uuid", cagPatientUuid);
		CagPatient cagPatient = (CagPatient) query.uniqueResult();
		
		if (!tx.wasCommitted())
			tx.commit();
		return cagPatient;
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
	public CagVisit getCagVisitByUuid(String cagVisitUuid) {
		Transaction tx = getSession().beginTransaction();
		Query query = getSession().createQuery("from cag_visit cv where cv.uuid=:uuid and cv.voided=false");
		query.setString("uuid", cagVisitUuid);
		CagVisit cagVisit = (CagVisit) query.uniqueResult();
		
		if (!tx.wasCommitted())
			tx.commit();
		return cagVisit;
	}
	
	public List<CagVisit> getAllCagVisits() {
		Transaction tx = getSession().beginTransaction();
		Query query = getSession().createQuery("from cag_visit cv where cv.voided=false");
		List<CagVisit> cagVisits = (List<CagVisit>) query.list();
		
		if (!tx.wasCommitted())
			tx.commit();
		return cagVisits;
	}
	
	@Override
	public List<CagVisit> searchCagVisits(Patient attender, Boolean isActive) {
		Transaction tx = getSession().beginTransaction();
		Query query = getSession().createQuery(
		    "from cag_visit cv where cv.attender=:attender and cv.isActive=:isActive and cv.voided=false");
		query.setParameter("attender", attender);
		query.setBoolean("isActive", isActive);
		List<CagVisit> cagVisits = (List<CagVisit>) query.list();
		
		if (!tx.wasCommitted())
			tx.commit();
		return cagVisits;
	}
	
	@Override
	public void deleteCagVisit(String cagVisitUuid) {
		Transaction tx = getSession().beginTransaction();
		SQLQuery cagEncounterVoider = getSession().createSQLQuery(
		    "update obs o join encounter e on "
		            + "o.encounter_id=e.encounter_id join visit v on v.visit_id=e.visit_id join cag_visit_visit cvv on "
		            + "cvv.visit_id=v.visit_id join cag_visit cv on cv.cag_visit_id=cvv.cag_visit_id join cag_encounter ce "
		            + "on ce.cag_visit_id=cv.cag_visit_id set o.voided=1, e.voided=1, v.voided=1, cv.voided=1, ce.voided=1 "
		            + "where cv.uuid=:cagVisitUuid");
		cagEncounterVoider.setString("cagVisitUuid", cagVisitUuid);
		cagEncounterVoider.executeUpdate();
		
		if (!tx.wasCommitted())
			tx.commit();
	}
	
	@Override
	public void saveAbsentCagPatient(Absentee absentee) {
		Transaction tx = getSession().beginTransaction();
		getSession().save(absentee);
		if (!tx.wasCommitted())
			tx.commit();
	}
	
	@Override
	public List<Absentee> getAllAbsentees(Integer cagVisitId) {
		Transaction tx = getSession().beginTransaction();
		Query query = getSession().createQuery("from missed_drug_pickup  where cagVisitId=:cagVisitId");
		query.setInteger("cagVisitId", cagVisitId);
		List<Absentee> absenteeList = (List<Absentee>) query.list();
		
		if (!tx.wasCommitted())
			tx.commit();
		return absenteeList;
	}
	
	@Override
	public void closeCagPatientVisit(Patient patient, String startDate, String dateStopped) {
		Transaction tx = getSession().beginTransaction();
		Query query = getSession()
		        .createQuery(
		            "update Visit v set v.stopDatetime=:dateStopped where v.patient = :patient and v.startDatetime = :startDate and v.voided=false");
		query.setString("dateStopped", dateStopped);
		query.setString("startDate", startDate);
		query.setParameter("patient", patient);
		query.executeUpdate();
		
		if (!tx.wasCommitted())
			tx.commit();
		
	}
	
	@Override
	public CagVisit closeCagVisit(String cagVisitUuid, String dateStopped) {
		Transaction tx = getSession().beginTransaction();
		Query query = getSession().createQuery(
		    "update cag_visit cv set cv.dateStopped=:dateStopped, isActive=false where cv.uuid=:uuid and cv.voided=false");
		query.setString("dateStopped", dateStopped);
		query.setString("uuid", cagVisitUuid);
		query.executeUpdate();
		if (!tx.wasCommitted())
			tx.commit();
		
		return getCagVisitByUuid(cagVisitUuid);
	}
	
	@Override
	public List<CagEncounter> getAllCagEncounters() {
		Transaction tx = getSession().beginTransaction();
		Query query = getSession().createQuery("from cag_encounter ce where ce.voided=false");
		List<CagEncounter> cagEncounters = (List<CagEncounter>) query.list();
		if (!tx.wasCommitted())
			tx.commit();
		
		return cagEncounters;
	}
	
	@Override
	public List<CagEncounter> searchCagEncounters(Cag cag) {
		Transaction tx = getSession().beginTransaction();
		Query query = getSession().createQuery("from cag_encounter ce where ce.cag=:cag and ce.voided=0");
		query.setParameter("cag", cag);
		List<CagEncounter> cagEncounters = (List<CagEncounter>) query.list();
		
		if (!tx.wasCommitted())
			tx.commit();
		return cagEncounters;
	}
	
	@Override
	public CagEncounter getCagEncounterByUuid(String uuid) {
		Transaction tx = getSession().beginTransaction();
		Query query = getSession().createQuery("from cag_encounter ce where ce.uuid=:uuid and ce.voided=:voided");
		query.setInteger("voided", 0);
		query.setString("uuid", uuid);
		CagEncounter cagEncounter = (CagEncounter) query.uniqueResult();
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
	public void updateCagEncounter(String cagEncounterUuid, Integer locationId, Date nextEncounterDateTime) {
		Transaction tx = getSession().beginTransaction();
		SQLQuery cagEncounterUpdater = getSession()
		        .createSQLQuery(
		            "update obs o join encounter e on "
		                    + "o.encounter_id=e.encounter_id join cag_encounter_encounter cee on "
		                    + "cee.encounter_id=e.encounter_id join cag_encounter ce on ce.cag_encounter_id=cee.cag_encounter_id "
		                    + "set o.location_id=:locationId, e.location_id=:locationId, ce.location_id=:locationId, ce.next_encounter_date=:nextEncounterDateTime "
		                    + "where ce.uuid=:cagEncounterUuid");
		cagEncounterUpdater.setString("cagEncounterUuid", cagEncounterUuid);
		cagEncounterUpdater.setParameter("nextEncounterDateTime", nextEncounterDateTime);
		cagEncounterUpdater.setParameter("locationId", locationId);
		cagEncounterUpdater.executeUpdate();
		
		if (!tx.wasCommitted())
			tx.commit();
	}
	
	@Override
	public void deleteCagEncounter(String cagEncounterUuid) {
		Transaction tx = getSession().beginTransaction();
		SQLQuery cagEncounterVoider = getSession().createSQLQuery(
		    "update obs o join encounter e on " + "o.encounter_id=e.encounter_id join cag_encounter_encounter cee on "
		            + "cee.encounter_id=e.encounter_id join cag_encounter ce on ce.cag_encounter_id=cee.cag_encounter_id "
		            + "set o.voided=1, e.voided=1, ce.voided=1 where ce.uuid=:cagEncounterUuid");
		cagEncounterVoider.setString("cagEncounterUuid", cagEncounterUuid);
		cagEncounterVoider.executeUpdate();
		
		if (!tx.wasCommitted())
			tx.commit();
	}
	
	@Override
	public Location getLocation(String locationUuid) {
		Transaction tx = getSession().beginTransaction();
		Query query = getSession().createQuery("from Location l where l.uuid=:locationUuid");
		query.setString("locationUuid", locationUuid);
		Location location = (Location) query.uniqueResult();
		
		if (!tx.wasCommitted())
			tx.commit();
		return location;
	}
}
