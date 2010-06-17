package net.frontlinesms.plugins.patientview.data.domain.response;

import java.text.DateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.ui.i18n.InternationalisationUtils;


@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@Table(name="medic_responses")
@DiscriminatorValue("response")
@DiscriminatorColumn(name="response_type")
public abstract class Response { 

	/** Unique id for this entity.  This is for hibernate usage. */
	@Id @GeneratedValue
	@Column(unique=true,nullable=false,updatable=false)
	protected long responseId;
	
	@OneToOne(fetch=FetchType.EAGER,cascade={})
	@JoinColumn(name="submitter",nullable=true )
	protected Person submitter;
	
	@OneToOne(fetch=FetchType.EAGER,optional=true, cascade={})
	@JoinColumn(name="subject",nullable=true )
	protected Person subject;
	
	protected long dateSubmitted;

	public Response(){}
	
	public Response(Person submitter, Person subject){
		this.submitter = submitter;
		this.subject = subject;
		dateSubmitted = new Date().getTime();
	}

	public Person getSubmitter() {
		return submitter;
	}

	public void setSubmitter(Person submitter) {
		this.submitter = submitter;
	}
	
	public String getSubmitterName(){
		return submitter.getName();
	}
	
	public String getSubjectName(){
		return subject.getName();
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
