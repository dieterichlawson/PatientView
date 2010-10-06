package net.frontlinesms.plugins.patientview.domain;

import java.util.Date;

import net.frontlinesms.plugins.patientview.data.domain.people.Person;

public interface Removable {

	public boolean isDeleted();
	
	public boolean isActive();
	
	public String getReason();
	
	public Person getRemovedBy();
	
	public Date getRemovedDate();
	
	public void setRemoved(boolean isDeleted, boolean isActive, Person removedBy, String reason);	
}
