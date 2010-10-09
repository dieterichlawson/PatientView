package net.frontlinesms.plugins.patientview.data.repository;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;

/**
 * MedicFormFieldDao does not have a save or delete method because you should
 * not be saving fields directly. Fields should be created and then passed into
 * a MedicForm object, which should then be saved. This operation will cascade
 * down to the fields However, we do want to be able to retrieve the fields that
 * should be displayed in the attribute panel.
 */
public interface MedicFormFieldDao {

	/**
	 * Updates a MedicFormField
	 * 
	 * @param mff
	 */
	public void updateField(MedicFormField field);

	/**
	 * Returns all MedicFormFields
	 * 
	 * @return
	 */
	public List<MedicFormField> getAllFields();

	/**
	 * Returns all form fields that are marked as attribute panel fields.
	 * 
	 * @return
	 */
	public List<MedicFormField> getAttributePanelFields();

	/**
	 * Returns all attribute panel form fields that have been responded to for Person p
	 * 
	 * @param p
	 * @return
	 */
	public List<MedicFormField> getAnsweredAttributePanelFieldsForPerson(Person p);

	/**
	 * Returns fields that have 'labelFragment' anywhere in their label.
	 * 
	 * @param labelFragment
	 * @return
	 */
	public List<MedicFormField> findFieldsByLabel(String labelFragment);

	/**
	 * Returns fields that have 'labelFragment' anywhere in their label. The number
	 * of results can be limited by supplying the int 'limit'. If limit is less than 0,
	 * all results are returned
	 * 
	 * @param labelFragment
	 *            the string to match
	 * @param limit
	 *            the limit
	 * @return
	 */
	public List<MedicFormField> findFieldsByLabel(String labelFragment, int limit);

	/**
	 * Returns all the fields on the form supplied. The fields are not
	 * guaranteed to be returned in the order that they are listed on the form,
	 * but you can use the {@link MedicFormField#getPosition()} method to get
	 * the position of the field on its parent form.
	 * 
	 * @param form the form in question
	 * @return the fields on that form
	 */
	public List<MedicFormField> getFieldsOnForm(MedicForm form);
}
