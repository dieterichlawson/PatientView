package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.Collection;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;

public class HibernateMedicFormFieldDao extends BaseHibernateDao<MedicFormField>{

	private static final String fieldsByName = "select mf from MedicFormField mf where mf.label like ";
	
	protected HibernateMedicFormFieldDao() {
		super(MedicFormField.class);
	}

	public void deleteMedicFormField(MedicFormField field) {
		super.delete(field);
	}

	public Collection<MedicFormField> getAllMedicFormFields() {
		return super.getAll();
	}

	public void saveMedicFormField(MedicFormField field) {
		super.saveWithoutDuplicateHandling(field);
	}

	public void updateMedicFormField(MedicFormField field) {
		super.updateWithoutDuplicateHandling(field);
	}
	
	public Collection<MedicFormField> getFieldsByName(String s){
		String q = fieldsByName + "'%" + s + "%'";
		return super.getSession().createQuery(q).list();
	}
}
