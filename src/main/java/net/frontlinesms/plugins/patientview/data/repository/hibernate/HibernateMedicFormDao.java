package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.Collection;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;

public class HibernateMedicFormDao extends BaseHibernateDao<MedicForm>{
	
	private static final String formsForStringQ = "select mf from MedicForm mf where mf.name like ";
	protected HibernateMedicFormDao() {
		super(MedicForm.class);
	}

	public void deleteMedicForm(MedicForm form) {
		super.delete(form);
	}

	public Collection<MedicForm> getAllMedicForms() {
		return super.getAll();
	}

	public void saveMedicForm(MedicForm form) {
		super.saveWithoutDuplicateHandling(form);
	}

	public void updateMedicForm(MedicForm form) {
		super.updateWithoutDuplicateHandling(form);
	}

	public Collection<MedicForm> getFormsForString(String s){
		String q = formsForStringQ + "'%" + s + "%'";
		return super.getSession().createQuery(q).list();
	}
}
