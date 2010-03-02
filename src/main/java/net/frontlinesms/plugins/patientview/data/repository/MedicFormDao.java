package net.frontlinesms.plugins.patientview.data.repository;

import java.util.Collection;

import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;

public interface MedicFormDao {

	public void saveMedicForm(MedicForm s);

	public void deleteMedicForm(MedicForm s);
	
	public void updateMedicForm(MedicForm s);
	
	public Collection<MedicForm> getAllMedicForms();
	
	/**
	 * Performs a like query with %'s around the s's
	 * select * from MedicForm where name like '%s%'
	 * @param s
	 * @return All Medic Forms with a name like s
	 */
	public Collection<MedicForm> getMedicFormsByName(String s);
	
	/**
	 * Returns the MedicForm that corresponds to the supplied FrontlineSMS form
	 * @param form the FrontlineSMS form
	 * @return the corresponding Medic Form
	 */
	public MedicForm getMedicFormForForm(Form form);
	
	public void reattach(MedicForm mf);
}
