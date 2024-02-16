package org.openmrs.module.cag.cag;

import org.openmrs.*;
import org.openmrs.annotation.AllowDirectAccess;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.util.*;

@Repository
@Entity(name = "cag_encounter")
public class CagEncounter extends BaseOpenmrsData {
	
	@Id
	@GeneratedValue
	@Column(name = "cag_encounter_id")
	private Integer id;
	
	//	@Transient
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "cag_id")
	private Cag cag;
	
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "cag_visit_id")
	private CagVisit cagVisit;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "location_id")
	private Location location;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "patient_id")
	private Patient attender;
	
	@Column(name = "cag_encounter_datetime")
	private Date cagEncounterDateTime;
	
	@Column(name = "next_encounter_date")
	private Date nextEncounterDate;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinTable(name = "cag_encounter_encounter", joinColumns = { @JoinColumn(name = "cag_encounter_id") }, inverseJoinColumns = { @JoinColumn(name = "encounter_id") })
	private Set<Encounter> encounters = new HashSet<Encounter>();
	
	@Transient
	public String displayed;
	
	@Override
	public Integer getId() {
		return id;
	}
	
	@Override
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Patient getAttender() {
		return attender;
	}
	
	public void setAttender(Patient attender) {
		this.attender = attender;
	}
	
	public Date getCagEncounterDateTime() {
		return cagEncounterDateTime;
	}
	
	public void setCagEncounterDateTime(Date cagEncounterDateTime) {
		this.cagEncounterDateTime = cagEncounterDateTime;
	}
	
	public Date getNextEncounterDate() {
		return nextEncounterDate;
	}
	
	public void setNextEncounterDate(Date nextEncounterDate) {
		this.nextEncounterDate = nextEncounterDate;
	}
	
	public Set<Encounter> getEncounters() {
		return encounters;
	}
	
	public void setEncounters(Set<Encounter> encounters) {
		this.encounters = encounters;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}
	
	public String getDisplayed() {
		return displayed;
	}
	
	public Cag getCag() {
		return cag;
	}
	
	public void setCag(Cag cag) {
		this.cag = cag;
	}
	
	public CagVisit getCagVisit() {
		return cagVisit;
	}
	
	public void setCagVisit(CagVisit cagVisit) {
		this.cagVisit = cagVisit;
	}
	
	public void setDisplayed(String displayed) {
		this.displayed = displayed;
	}
	
	@Override
	public String toString() {
		return "CagEncounter{" + "id=" + id + ", cag=" + cag + ", cagVisit=" + cagVisit + ", location=" + location.getName()
		        + ", attender=" + attender + ", cagEncounterDateTime=" + cagEncounterDateTime + ", nextEncounterDate="
		        + nextEncounterDate + ", encounters=" + encounters + ", displayed='" + displayed + '\'' + '}';
	}
}
