package org.openmrs.module.cag.cag;

import org.springframework.stereotype.Repository;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Repository
@Entity(name = "cag_visit_visit")
public class CagVisitVisit {
	
	@Id
	@GeneratedValue
	private int id;
	
	private int cagVisitId;
	
	private int visitId;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getCagVisitId() {
		return cagVisitId;
	}
	
	public void setCagVisitId(int cagVisitId) {
		this.cagVisitId = cagVisitId;
	}
	
	public int getVisitId() {
		return visitId;
	}
	
	public void setVisitId(int visitId) {
		this.visitId = visitId;
	}
}
