package net.frontlinesms.plugins.patientview.data.repository;

import java.util.Collection;

import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;

public interface FormDao {

	public void saveForm(MedicForm s);

	public void deleteForm(MedicForm s);
	
	public void updateForm(MedicForm s);
	
	public Collection<MedicForm> getAllForms();
	
	public Collection<MedicForm> getFormsForString(String s);
}
