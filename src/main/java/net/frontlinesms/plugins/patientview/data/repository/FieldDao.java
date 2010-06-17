package net.frontlinesms.plugins.patientview.data.repository;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.framework.Field;

public interface FieldDao {

	public Field getFieldById(long id);
	
	public List<Field> getAllFields();
	
	public List<Field> findFieldsByLabel(String label);
	
	public List<Field> getFieldsByLabel(String label);
}
