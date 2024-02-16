package org.openmrs.module.cag.cag;

import org.springframework.stereotype.Component;

import javax.persistence.*;

@Component
@Entity(name = "missed_drug_pickup")
public class Absentee {
	
	@Id
	@GeneratedValue
	@Column(name = "missed_drug_pickup_id")
	private Integer id;
	
	@Column(name = "cag_visit_id")
	private Integer cagVisitId;
	
	@Column(name = "patient_id")
	private Integer patientId;
	
	@Column(name = "reason_missed")
	private String reason;
	
	@Transient
	private String uuid;
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getCagVisitId() {
		return cagVisitId;
	}
	
	public void setCagVisitId(Integer cagVisitId) {
		this.cagVisitId = cagVisitId;
	}
	
	public Integer getPatientId() {
		return patientId;
	}
	
	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}
	
	public String getReason() {
		return reason;
	}
	
	public void setReason(String reason) {
		this.reason = reason;
	}
	
	public String getUuid() {
		return uuid;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	@Override
	public String toString() {
		return "Absentee{" + "id=" + id + ", cagVisitId=" + cagVisitId + ", patientId=" + patientId + ", reasonMissed='"
		        + reason + '\'' + ", uuid='" + uuid + '\'' + '}';
	}
}
