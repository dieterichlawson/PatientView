package net.frontlinesms.plugins.medic.data.repository;

import java.util.Collection;

import net.frontlinesms.plugins.medic.data.domain.framework.MedicForm;

public interface FormDao {

	public void saveForm(MedicForm s);

	public void deleteForm(MedicForm s);
	
	public void updateForm(MedicForm s);
	
	public Collection<MedicForm> getAllForms();
	
	public Collection<MedicForm> getFormsForString(String s);
}
