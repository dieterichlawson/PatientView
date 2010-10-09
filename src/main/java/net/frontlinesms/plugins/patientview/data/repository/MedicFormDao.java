package net.frontlinesms.plugins.patientview.data.repository;

import java.util.List;

import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;

public interface MedicFormDao {

	/**
	 * Saves a MedicForm to the data source
	 * @param s
	 */
	public void saveMedicForm(MedicForm form);

	/**
	 * Deletes a MedicForm from the data source
	 * @param form
	 */
	public void deleteMedicForm(MedicForm form);
	
	/**
	 * Updates a MedicForm in the data source
	 * @param form
	 */
	public void updateMedicForm(MedicForm form);
	
	/**
	 * @return All MedicForms in the system
	 */
	public List<MedicForm> getAllMedicForms();
	
	/**
	 * Finds all MedicForms with nameFragment anywhere
	 * in their name. Performs a like query.
	 * @param nameFragment
	 */
	public List<MedicForm> findMedicFormsByName(String nameFragment);
	
	/**
	 * Returns the MedicForm that corresponds to the supplied FrontlineSMS form
	 * @param form the FrontlineSMS form
	 * @return the corresponding Medic Form
	 */
	public MedicForm getMedicFormForForm(Form form);

}
