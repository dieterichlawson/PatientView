package net.frontlinesms.plugins.patientview.data.repository;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;

/**
 * This Dao is pretty bare bones, mainly because you should not
 * be saving fields directly. Fields should be created and then passed into a MedicForm
 * object, which should then be saved. The operation will cascade down to the fields
 * However, we do want to be able to retrieve the fields that should be displayed in the
 * attribute panel.
 * @author Dieterich
 *
 */
public interface MedicFormFieldDao {
	
	/**
	 * Updates the field in question
	 * @param mff
	 */
	public void updateField(MedicFormField mff);
	
	/**
	 * Returns all MedicFormFields
	 * @return
	 */
	public List<MedicFormField> getAllFields();
	
	/**
	 * Returns all form fields that are marked as attribute panel fields.
	 * There is no need to supply a person type or a person because FormFields
	 * can currently only be submitted about a patient. As a result, the
	 * FormFields returned by this method are only about patients
	 * @return
	 */
	public List<MedicFormField> getAttributePanelFields();
	
	/**
	 * Returns the attributes that have actually been set for Person p
	 * @param p
	 * @return
	 */
	public List<MedicFormField> getAnsweredAttributePanelFieldsForPerson(Person p);
	
	/**
	 * Returns fields that have a label that matches a like %s% query
	 * @param s
	 * @return
	 */
	public List<MedicFormField> getFieldsByName(String s);
	
	/**
	 * Returns all the fields on the form supplied. The fields
	 * are not guaranteed to be returned in the order that they are listed on the form, but
	 * you can use the {@link MedicFormField#getPosition()} method to get the
	 * position of the field on it's parent form
	 * @param f the form in question
	 * @return the fields on that form
	 */
	public List<MedicFormField> getFieldsOnForm(MedicForm f);
}
