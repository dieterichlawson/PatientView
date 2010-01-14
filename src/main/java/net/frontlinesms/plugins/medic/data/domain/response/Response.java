package net.frontlinesms.plugins.medic.data.domain.response;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import net.frontlinesms.plugins.medic.data.domain.people.Person;


@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn( name="responsetype",discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue("response")
@Table(name="medic_responses")
public class Response {

	/** Unique id for this entity.  This is for hibernate usage. */
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true,nullable=false,updatable=false)
	private long rid;
	
	@OneToOne(fetch=FetchType.LAZY,cascade={})
	@JoinColumn(name="submitter",nullable=true )
	private Person submitter;
	
	@OneToOne(fetch=FetchType.LAZY,optional=true, cascade={})
	@JoinColumn(name="subject",nullable=true )
	private Person subject;
	
	private long dateSubmitted;
	
	@ManyToMany(cascade={},fetch=FetchType.LAZY)
	@JoinTable(name="tag_map",
			joinColumns= @JoinColumn(name="response"),
			inverseJoinColumns=@JoinColumn(name="tag"))
	private List<Tag> tags;

	public Response(){}
	
	public Response(Person submitter, Person subject){
		this.submitter = submitter;
		this.subject = subject;
		dateSubmitted = new Date().getTime();
	}
	
	public long getRid() {
		return rid;
	}

	public Person getSubmitter() {
		return submitter;
	}

	public void setSubmitter(Person submitter) {
		this.submitter = submitter;
	}

	public Person getSubject() {
		return subject;
	}

	public void setSubject(Person subject) {
		this.subject = subject;
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
