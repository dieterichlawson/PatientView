package net.frontlinesms.plugins.patientview.data.domain.flag;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;

@Entity
@Table(name="medic_flags")
public class PatientFlag {

	/** Unique id for this entity.  This is for hibernate usage. */
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true,nullable=false,updatable=false)
	private long fid;
	
	@OneToOne(targetEntity=Patient.class)
	private Patient patient;
	
	@ManyToOne(cascade={},fetch=FetchType.EAGER,targetEntity=FlagDefinition.class)
	private FlagDefinition flag;
	
	private boolean acknowledged;

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setFlag(FlagDefinition flag) {
		this.flag = flag;
	}

	public FlagDefinition getFlag() {
		return flag;
	}

	public void setAcknowledged(boolean acknowledged) {
		this.acknowledged = acknowledged;
	}

	public boolean isAcknowledged() {
		return acknowledged;
	}

	public PatientFlag(Patient patient, FlagDefinition flag, boolean acknowledged) {
		super();
		this.patient = patient;
		this.flag = flag;
		this.acknowledged = acknowledged;
	}

	public long getFid() {
		return fid;
	}
	
}
