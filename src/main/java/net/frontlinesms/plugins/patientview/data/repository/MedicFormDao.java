package net.frontlinesms.plugins.patientview.data.repository;

import java.util.Collection;

import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;

public interface MedicFormDao {

	public void saveMedicForm(MedicForm s);

	public void deleteMedicForm(MedicForm s);
	
	public void updateMedicForm(MedicForm s);
	
	public Collection<MedicForm> getAllMedicForms();
	
	public Collection<MedicForm> getMedicFormsForString(String s);
	
	public MedicForm getMedicFormForForm(Form form);
}
