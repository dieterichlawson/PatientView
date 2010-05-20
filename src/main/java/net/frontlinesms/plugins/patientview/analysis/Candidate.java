package net.frontlinesms.plugins.patientview.analysis;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

public class Candidate implements Comparable<Candidate>{

	private Patient patient;
	private float birthdateScore;
	private float nameScore;
	private float idScore;
	private float total = 0F;
	
	public Candidate(Patient patient) {
		super();
		this.patient = patient;
	}
	public Patient getPatient() {
		return patient;
	}
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	
	public float getTotalScore() {
		return birthdateScore + idScore + nameScore;
	}

	public float getAverageScore(){
		return (getTotalScore() / total) * 100F;
	}
	
	public float getBirthdateScore() {
		return birthdateScore;
	}
	public void setBirthdateScore(float birthdateScore) {
		this.birthdateScore = birthdateScore;
		total +=1.0F;
	}
	public float getNameScore() {
		return nameScore;
	}
	public void setNameScore(float nameScore) {
		this.nameScore = nameScore;
		total +=1.0F;
	}
	public float getIdScore() {
		return idScore;
	}
	public void setIdScore(float idScore) {
		this.idScore = idScore;
		total +=1.0F;
	}
	
	public String getName(){
		return patient.getName();
	}
	
	public String getStringID(){
		return patient.getPid() +"";
	}
	
	public String getStringBirthdate(){
		return InternationalisationUtils.getDateFormat().format(patient.getBirthdate());
	}

	public int compareTo(Candidate o) {
		if(this.getTotalScore() - o.getTotalScore() > 0){
			return -1;
		}else if(this.getTotalScore() - o.getTotalScore() < 0){
			return 1;
		}else{
			return 0;
		}
	}
	
	public String getConfidence(){
		float score = (getTotalScore() / total) * 100F;
		return score+"%";
	}
	
	public String getCHWName(){
		return patient.getCHWName();
	}
}
