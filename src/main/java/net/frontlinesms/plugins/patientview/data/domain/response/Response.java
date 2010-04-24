package net.frontlinesms.plugins.patientview.data.domain.response;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.utils.DateUtils;


@Entity
@DiscriminatorColumn(name="response_type",discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="response")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@Table(name="medic_responses")
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
	
	@ManyToMany(cascade={},fetch=FetchType.LAZY)
	@JoinTable(name="tag_map",
			joinColumns= @JoinColumn(name="response"),
			inverseJoinColumns=@JoinColumn(name="tag"))
	protected List<Tag> tags;

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
		DateFormat df = DateUtils.getDateFormatter();
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
