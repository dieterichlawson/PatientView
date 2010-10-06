package net.frontlinesms.plugins.patientview.domain;

import java.util.Date;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

import net.frontlinesms.plugins.patientview.data.domain.people.Person;


@MappedSuperclass
public abstract class RemovableObject implements Removable {
	
	private boolean deleted = false;
	
	private boolean active = true;
	
	private String reason;
	
	private long date;
	
	@OneToOne(fetch=FetchType.LAZY,optional=true, cascade={})
	@JoinColumn(name="removedBy",nullable=true )
	private Person removedBy;
	
	public Date getRemovedDate() {
		return new Date(date);
	}

	public Person getRemovedBy() {
		return removedBy;
	}

	public String getReason() {
		return reason;
	}

	public boolean isActive() {
		return active;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setRemoved(boolean isDeleted, boolean isRemoved, Person removedBy, String reason) {
		date = new Date().getTime();
		this.deleted = isDeleted;
		this.active = isRemoved;
		this.removedBy = removedBy;
		this.reason = reason;
	}

}
