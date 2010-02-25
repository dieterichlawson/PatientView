package net.frontlinesms.plugins.patientview.data.repository;

import java.util.Collection;

import net.frontlinesms.plugins.patientview.data.domain.framework.MedicField;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicField.PersonType;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;

public interface MedicFieldDao {

	public void saveMedicField(MedicField s);

	public void deleteMedicField(MedicField s);
	
	public void updateMedicField(MedicField s);
	
	public Collection<MedicField> getAllMedicFields();
	
	public Collection<MedicField> getDetailViewFieldsForPersonType(PersonType p);
	
	public Collection<MedicField> getDetailViewFieldsForPerson(Person p);
}
