package net.frontlinesms.plugins.medic.data.repository;

import java.util.Collection;

import net.frontlinesms.plugins.medic.data.domain.framework.MedicField;
import net.frontlinesms.plugins.medic.data.domain.framework.MedicField.PersonType;
import net.frontlinesms.plugins.medic.data.domain.people.Person;

public interface FieldDao {

	public void saveField(MedicField s);

	public void deleteField(MedicField s);
	
	public void updateField(MedicField s);
	
	public Collection<MedicField> getAllFields();
	
	public Collection<MedicField> getAllDetailViewFieldsForPersonType(PersonType p);
	
	public Collection<MedicField> getDetailViewFieldsForPerson(Person p);
}
