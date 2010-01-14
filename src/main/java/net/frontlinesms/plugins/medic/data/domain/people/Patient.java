package net.frontlinesms.plugins.medic.data.domain.people;

import java.util.Date;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue("patient")
public class Patient extends Person {
	
	/**
	 * The Community Health Worker of the Patient
	 */
	@ManyToOne(fetch=FetchType.LAZY, cascade={})
	@JoinColumn(name="chw_id", nullable=true)
	private CommunityHealthWorker chw;

	
	/**
	 * Empty constructor for Hibernate
	 */
	Patient(){}
	
	//FIXME:make all the columns in Person.java match up to this comment with how I'm describing the nullable and stuff
	/**
	 * Constructor for Patient
	 * @param chw the CHW of the patient, is nullable
	 * @param name the name of the Patient, non-nullable
	 * @param gender the gender of the Patient. Options are M,F,T
	 * @param birthdate the birthdate of the Patient, is nullable
	 * @param affiliation affiliation, like tribe, family, etc..., is nullable
	 */
	public Patient(CommunityHealthWorker chw, String name, char gender, Date birthdate){
		super(name, gender, birthdate);
		this.chw = chw;
	}
	
	public CommunityHealthWorker getChw() {
		return chw;
	}
	
	public String getCHWName(){
		return chw.getName();
	}
	public void setChw(CommunityHealthWorker chw) {
		this.chw = chw;
	}
	
	@Override
	public String getPersonType() {
		return "Patient";
	}
}
