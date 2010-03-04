package net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.personalformfields;

import net.frontlinesms.plugins.patientview.data.domain.people.Person;

/**
 * An interface for fields that are about a person specifically.
 * @author Dieterich
 *
 */
public interface PersonalFormField {

	/**
	 * Should change the person object that is passed in to reflect the current state
	 * of the form field
	 * @param p
	 */
	public void setFieldForPerson(Person p);
	
	/**
	 * @return whether or not the field currently contains valid input
	 */
	public boolean isValid();
	
	/**
	 * @return whether or not the field's content has changed
	 * since the field was created
	 */
	public boolean hasChanged();
	
	public String getLabel();
}
