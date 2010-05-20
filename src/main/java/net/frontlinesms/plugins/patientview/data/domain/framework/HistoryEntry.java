package net.frontlinesms.plugins.patientview.data.domain.framework;

import java.text.DateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.utils.DateUtils;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

@Entity
@Table(name="medic_history")
public class HistoryEntry {

	/** Unique id for this entity.  This is for hibernate usage. */
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true,nullable=false,updatable=false)
	private long rid;
	
	@OneToOne(fetch=FetchType.LAZY,cascade={})
	@JoinColumn(name="actor",nullable=true )
	private Person actor;
	
	@OneToOne(fetch=FetchType.LAZY,optional=true, cascade={})
	@JoinColumn(name="subject",nullable=true )
	private Person subject;
	
	private long dateSubmitted;
	
	private String message;
	
	public HistoryEntry(){}
	
	public HistoryEntry(Person actor, Person subject, String message){
		this.actor = actor;
		this.subject = subject;
		this.message = message;
		this.dateSubmitted = new Date().getTime();
	}
	public long getRid() {
		return rid;
	}

	public Person getActor() {
		return actor;
	}

	public void setActor(Person actor) {
		this.actor = actor;
	}
	
	public String getActorName(){
		if(getActor()!=null){
			return actor.getName();
		}else{
			return "SYSTEM";
		}
	}
	
	public String getSubjectName(){
		if(getSubject()!=null){
			return subject.getName();
		}else{
			return "SYSTEM";
		}
	}
	
	public String getMessage(){
		return message;
	}

	public Person getSubject() {
		return subject;
	}

	public void setSubject(Person subject) {
		this.subject = subject;
	}
	
	public String getStringDateSubmitted(){
		DateFormat df = InternationalisationUtils.getDateFormat();
		return df.format(getDateSubmitted());
	}

	public Date getDateSubmitted() {
		return new Date(dateSubmitted);
	}

	public void setDateSubmitted(long dateSubmitted) {
		this.dateSubmitted = dateSubmitted;
	}
	
	public void setDateSubmitted(Date dateSubmitted) {
		this.dateSubmitted = dateSubmitted.getTime();
	}

}
