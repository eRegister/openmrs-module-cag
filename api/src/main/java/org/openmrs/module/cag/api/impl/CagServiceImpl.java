/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.cag.api.impl;

import ca.uhn.hl7v2.model.v25.datatype.MA;
import liquibase.servicelocator.LiquibaseService;
import org.openmrs.*;
import org.openmrs.api.APIException;
import org.openmrs.api.OrderContext;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.logic.op.In;
import org.openmrs.module.cag.api.CagService;
import org.openmrs.module.cag.api.db.CagDao;
import org.openmrs.module.cag.cag.*;

import java.lang.ref.SoftReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CagServiceImpl extends BaseOpenmrsService implements CagService {
	
	public static Set<Integer> COPABLE_CONCEPTS = new HashSet<Integer>(Arrays.asList(3843, 2250, 3751, 3752, 4174, 3730,
	    2403, 3753));
	
	private CagDao dao;
	
	public CagDao getDao() {
		return dao;
	}
	
	public void setDao(CagDao dao) {
		this.dao = dao;
	}
	
	@Override
	public Cag getCagById(Integer cagId) {
		return dao.getCagById(cagId);
	}
	
	@Override
	public Cag getCagByUuid(String uuid) {
		Cag cag = dao.getCagByUuid(uuid);
		List<Patient> cagPatientList = getCagPatientList(cag.getId());
		cag.setCagPatientList(cagPatientList);
		return cag;
	}
	
	@Override
	public List<Cag> getCagList() {
		return dao.getCagList();
	}
	
	@Override
	public void saveCag(Cag cag) {
		
		//		cag.setUuid(UUID.randomUUID().toString());
		cag.setCreator(Context.getAuthenticatedUser());
		cag.setVoided(false);
		dao.saveCag(cag);
		System.out.println("CAg ID : " + cag.getId());
		
		List<Patient> patients = cag.getCagPatientList();
		
		for (Patient currentPatient : patients) {
			CagPatient newCagPatient = new CagPatient();
			newCagPatient.setCag(cag);
			newCagPatient.setPatient(currentPatient);
			
			saveCagPatient(newCagPatient);
		}
	}
	
	@Override
	public void voidCag(Cag cag) {
		cag.setVoided(true);
		dao.saveCag(cag);
	}
	
	@Override
	public void onStartup() {
	}
	
	@Override
	public void onShutdown() {
	}
	
	@Override
	public CagPatient getCagPatientById(Integer cagPatientId) {
		return this.dao.getCagPatientById(cagPatientId);
	}
	
	@Override
	public List<Patient> getCagPatientList(Integer cagId) {
		return dao.getCagPatientList(cagId);
	}
	
	@Override
	public Patient saveCagPatient(CagPatient cagPatient) {
		
		CagPatient retrivedCagPatient = getCagPatientByUuid(cagPatient.getPatient().getUuid());
		
		System.out.println("retrivedCagPatient : " + retrivedCagPatient);
		
		if ((retrivedCagPatient != null)) {
			
			throw new IllegalArgumentException("WARNING!! Patient : " + retrivedCagPatient.getPatient().getNames()
			        + "(uuid=" + retrivedCagPatient.getPatient().getUuid() + ") " + " Already a member of another CAG:"
			        + " " + retrivedCagPatient.getCag().getName());
			
		} else {
			
			cagPatient.setUuid(cagPatient.getPatient().getUuid());
			cagPatient.setStatus(true);
			
			dao.saveCagPatient(cagPatient);
			
			return cagPatient.getPatient();
		}
		
	}
	
	@Override
	public void deletePatientFromCag(String uuid) {
		Integer patientId = Context.getPatientService().getPatientByUuid(uuid).getPatientId();
		dao.deletePatientFromCag(patientId);
		
	}
	
	@Override
	public void deleteCag(String uuid) {
		dao.deleteCag(uuid);
	}
	
	@Override
	public Cag updateCag(Cag cag) {
		
		cag.setDateChanged(new Date());
		cag.setChangedBy(Context.getAuthenticatedUser());
		
		Cag updatedCag = dao.updateCag(cag);
		updatedCag.setCagPatientList(getCagPatientList(updatedCag.getId()));
		
		return updatedCag;
	}
	
	@Override
	public CagPatient getCagPatientByUuid(String uuid) {
		return dao.getCagPatientByUuid(uuid);
	}
	
	@Override
	public CagPatient getActiveCagVisitByAttender(String uuid) {
		Patient attender = Context.getPatientService().getPatientByUuid(uuid);
		CagPatient cagPatient = new CagPatient();
		cagPatient.setActiveCagVisits(dao.getAttenderActiveCagVisitList(attender));
		
		return cagPatient;
	}
	
	@Override
	public CagVisit openCagVisit(CagVisit cagVisit) {
		
		cagVisit.setCreator(Context.getAuthenticatedUser());
		CagVisit savedCagVisit = dao.saveCagVisit(cagVisit);
		
		Map<String, String> absentees = cagVisit.getAbsentees();
		if (!absentees.isEmpty())
			for (Map.Entry<String, String> absenteeEntry : absentees.entrySet()) {
				Absentee absentee = new Absentee();
				Patient absentPatient = Context.getPatientService().getPatientByUuid(absenteeEntry.getKey());
				absentee.setPatientId(absentPatient.getPatientId());
				absentee.setCagVisitId(savedCagVisit.getId());
				absentee.setReason(absenteeEntry.getValue());
				
				absentees.put(
				    absenteeEntry.getKey(),
				    absentPatient.getPatientIdentifier(3) + " - " + absentPatient.getGivenName() + " "
				            + absentPatient.getFamilyName() + " : " + absenteeEntry.getValue());
				
				dao.saveAbsentCagPatient(absentee);
				
			}
		
		Set<Visit> visits = cagVisit.getVisits();
		CagEncounter cagEncounter = new CagEncounter();
		cagEncounter.setCag(cagVisit.getCag());
		cagEncounter.setCagVisit(savedCagVisit);
		cagEncounter.setAttender(cagVisit.getAttender());
		cagEncounter.setCagEncounterDateTime(cagVisit.getDateStarted());
		
		for (Visit currentVisit : visits) {
			cagEncounter.getEncounters().add(currentVisit.getNonVoidedEncounters().get(0));
		}
		
		saveCagEncounter(cagEncounter);
		
		return getCagVisitByUuid(savedCagVisit.getUuid());
	}
	
	private Encounter getEncounter(Patient currentPatient, Visit savedVisit, Encounter attenderEncounter) {
		
		Encounter currentEncounter = new Encounter();
		currentEncounter.setEncounterType(attenderEncounter.getEncounterType());
		currentEncounter.setVisit(savedVisit);
		currentEncounter.setLocation(attenderEncounter.getLocation());
		currentEncounter.setForm(attenderEncounter.getForm());
		currentEncounter.setPatient(currentPatient);
		currentEncounter.setEncounterDatetime(attenderEncounter.getEncounterDatetime());
		currentEncounter.setCreator(attenderEncounter.getCreator());
		return currentEncounter;
	}
	
	@Override
	public CagVisit getCagVisitByUuid(String uuid) {
		
		CagVisit retrievedCagVisit = dao.getCagVisitByUuid(uuid);
		
		Map<String, String> absentees = getAbsentees(retrievedCagVisit);
		retrievedCagVisit.setAbsentees(absentees);
		
		return retrievedCagVisit;
	}
	
	@Override
	public List<CagVisit> getCagVisitList() {
		return dao.getCagVisitList();
	}
	
	public Map<String, String> getAbsentees(CagVisit cagVisit) {
		Map<String, String> absentees = new HashMap<String, String>();
		
		List<Absentee> absenteeList = dao.getAbsenteeList(cagVisit.getId());
		
		if (!absenteeList.isEmpty()) {
			
			for (Absentee absentee : absenteeList) {
				
				Patient absentPatient = Context.getPatientService().getPatient(absentee.getPatientId());
				
				absentees.put(
				    absentPatient.getUuid(),
				    absentPatient.getPatientIdentifier(3) + " - " + absentPatient.getGivenName() + " "
				            + absentPatient.getFamilyName() + " : " + absentee.getReason());
			}
		}
		
		return absentees;
	}
	
	@Override
	public CagVisit closeCagVisit(String uuid, String dateStopped) {
		
		Date stopDate = new Date();
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			stopDate = simpleDateFormat.parse(dateStopped);
		}
		catch (Exception e) {
			System.out.println("Cought ParseException Exception!!!");
			throw new ParseException("Date ParseException encountered!", 1);
		}
		finally {
			
			System.out.println("dateStopped=================== " + stopDate);
			
			CagVisit cagVisit = dao.closeCagVisit(uuid, dateStopped);
			Map<String, String> absentees = getAbsentees(cagVisit);
			Set<String> absenteeUuidSet = absentees.keySet();
			Cag cag = cagVisit.getCag();
			Date visitStartDate = cagVisit.getDateStarted();
			
			closePatientVisits(absenteeUuidSet, cag.getId(), visitStartDate, dateStopped);
			
			return cagVisit;
		}
	}
	
	public void closePatientVisits(Set<String> absenteeUuidSet, Integer cagId, Date visitDate, String dateStopped) {
		
		List<Patient> cagPatientList = getCagPatientList(cagId);
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String VisitTime = simpleDateFormat.format(visitDate);
		
		for (Patient presentPatient : cagPatientList) {
			if (!absenteeUuidSet.contains(presentPatient.getUuid())) {
				dao.closeCagPatientVisit(presentPatient, VisitTime, dateStopped);
			}
		}
		
	}
	
	@Override
	public void deleteCagVisit(String uuid) {
		CagVisit cagVisit = getCagVisitByUuid(uuid);
		
		dao.deleteCagVisit(cagVisit.getId());
	}
	
	@Override
	public CagEncounter getCagEncounterByUuid(String uuid) {
		
		//		System.out.println("About to fetch CAG Encounter : " + uuid);
		CagEncounter cagEncounter = dao.getCagEncounterByUuid(uuid);
		//		System.out.println("Fetched CAG Encounter : " + cagEncounter.getCag());
		
		//		String displayed = dao.getCagById(cagEncounter.getCagId()).getName() + " @ "
		//		        + Context.getLocationService().getLocation(cagEncounter.getLocationId()) + " - From "
		//		        + cagEncounter.getDateCreated() + " - " + cagEncounter.getNextEncounterDate();
		
		return cagEncounter;
	}
	
	@Override
	public CagEncounter saveCagEncounter(CagEncounter cagEncounter) {
		
		//		Integer cagId = Context.getService(CagService.class).getCagByUuid(cagEncounter.getCag().getUuid()).getId();
		//		CagVisit cagVisit = getCagVisitByUuid(cagEncounter.getCagVisit().getUuid());
		
		//		cagEncounter.setCagId(cagId);
		//		cagEncounter.setCagVisitId(cagVisit.getId());
		cagEncounter.setCreator(Context.getAuthenticatedUser());
		
		System.out.println("About to save Cag Encounter");
		
		dao.saveCagEncounter(cagEncounter);
		
		Set<Encounter> encounters = cagEncounter.getEncounters();
		
		for (Encounter currentEncounter : encounters) {
			dao.saveCagPatientEncounter(currentEncounter);
			
			int count = 1;
			Set<Obs> topLevelObs = currentEncounter.getObsAtTopLevel(false);
			for (Obs currentObs : topLevelObs) {
				System.out.println(count);
				System.out.println("Current obs : " + currentObs);
				currentObs.setEncounter(currentEncounter);
				Context.getObsService().saveObs(currentObs, "Saving Cag member Observation");
				count += 1;
			}
			if (!currentEncounter.getOrders().isEmpty()) {
				Set<Order> orders = currentEncounter.getOrders();
				Object[] ordersArray = orders.toArray();
				
				for (int index = 0; index < ordersArray.length; index += 2) {
					Order currentOrder = (Order) ordersArray[index];
					currentOrder.setInstructions("As directed");
					Context.getOrderService().saveOrder(currentOrder, new OrderContext());
				}
			}
			
		}
		
		System.out.println("When trying to get encounter by uuid : " + cagEncounter.getUuid());
		CagEncounter cagEncounter1 = getCagEncounterByUuid(cagEncounter.getUuid());
		System.out.println("\nFinally!!!!!!!!!1\n" + cagEncounter1);
		
		return cagEncounter1;
	}
	
	@Override
	public void deleteCagEncounter(String uuid) {
		CagEncounter cagEncounter = getCagEncounterByUuid(uuid);
		cagEncounter.setVoided(true);
		
		System.out.println("retrieved cagEncounter : " + cagEncounter.getUuid() + "\nVoided : " + cagEncounter.getVoided());
		
		Set<Encounter> encounters = cagEncounter.getEncounters();
		for (Encounter currentEncounter : encounters) {
			currentEncounter.setVoided(true);
			System.out.println(" encounter : " + currentEncounter + "\nVoided : " + currentEncounter.getVoided());
			
			System.out.println("\n");
			Set<Obs> obs = currentEncounter.getObs();
			for (Obs currentObs : obs) {
				currentObs.setVoided(true);
				System.out.println(" obs : " + currentObs + "\nVoided : " + currentObs.getVoided());
			}
			
			System.out.println("\n\n========================");
			
		}
		
		dao.deleteCagEncounter(cagEncounter);
	}
	
	@Override
	public CagEncounter updateCagEncounter(String cagEncounterUuid, String locationUuid, Date encounterDateTime,
	        Date nextEncounterDateTime) {
		CagEncounter retrievedCagEncounter = dao.getCagEncounterByUuid(cagEncounterUuid);
		System.out.println("========nextEncounterDateTime ====\n" + retrievedCagEncounter);
		
		Location location = Context.getLocationService().getLocation(locationUuid);
		System.out.println("location: " + locationUuid);
		
		dao.updateCagEncounter(cagEncounterUuid, location, encounterDateTime, nextEncounterDateTime);
		
		return null;
	}
	
	public String formatDateTime(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = simpleDateFormat.format(date);
		
		return dateString;
	}
	
	public Double computeQty(DrugOrder order) {
		Double dose = order.getDose();
		Double frequency = order.getFrequency().getFrequencyPerDay();
		Integer duration = order.getDuration();
		
		Integer conceptId = order.getDurationUnits().getConceptId();
		Integer durationUnits = (conceptId == 76) ? 1 : (conceptId == 93) ? 7 : 30;
		
		Double qty = dose * frequency * duration * durationUnits;
		
		System.out.println("\nQuantity : " + qty + "\n");
		
		return qty;
	}
	
	//	public List<Visit> getPresentPatientVisits(CagVisit cagVisit) {
	//		List<Visit> presentPatientVisits = new ArrayList<Visit>();
	//
	//		List<Patient> cagPatientList = getCagPatientList(cagVisit.getCagId());
	//		Set<String> absenteeUuidSet = getAbsentees(cagVisit).keySet();
	//
	//		String VisitTime = formatDateTime(cagVisit.getDateStarted());
	//
	//		for (Patient presentPatient : cagPatientList) {
	//			if (!absenteeUuidSet.contains(presentPatient.getUuid())) {
	//				Visit visit = dao.getVisit(presentPatient, VisitTime);
	//				presentPatientVisits.add(visit);
	//			}
	//		}
	//		return presentPatientVisits;
	//	}
}
