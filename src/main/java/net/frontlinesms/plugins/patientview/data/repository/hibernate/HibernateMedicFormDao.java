package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.Collection;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormDao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

public class HibernateMedicFormDao extends BaseHibernateDao<MedicForm> implements MedicFormDao{
	
	protected HibernateMedicFormDao() {
		super(MedicForm.class);
	}

	/* (non-Javadoc)
	 * @see net.frontlinesms.plugins.patientview.data.repository.MedicFormDao#deleteMedicForm(net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm)
	 */
	public void deleteMedicForm(MedicForm form) {
		super.delete(form);
	}

	/*
	 * @see net.frontlinesms.plugins.patientview.data.repository.MedicFormDao#getAllMedicForms()
	 */
	public Collection<MedicForm> getAllMedicForms() {
		return super.getAll();
	}

	/*
	 * @see net.frontlinesms.plugins.patientview.data.repository.MedicFormDao#saveMedicForm(net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm)
	 */
	public void saveMedicForm(MedicForm form) {
		super.saveWithoutDuplicateHandling(form);
	}

	/*
	 * @see net.frontlinesms.plugins.patientview.data.repository.MedicFormDao#updateMedicForm(net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm)
	 */
	public void updateMedicForm(MedicForm form) {
		super.updateWithoutDuplicateHandling(form);
	}

	/*
	 * @see net.frontlinesms.plugins.patientview.data.repository.MedicFormDao#getMedicFormsByName(java.lang.String)
	 */
	public Collection<MedicForm> getMedicFormsByName(String s){
		DetachedCriteria c = DetachedCriteria.forClass(MedicForm.class);
		c.add(Restrictions.like("name", "%" + s+ "%"));
		return super.getList(c);
	}

	/*
	 * @see net.frontlinesms.plugins.patientview.data.repository.MedicFormDao#getMedicFormForForm(net.frontlinesms.plugins.forms.data.domain.Form)
	 */
	public MedicForm getMedicFormForForm(Form form) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("form", form));
		try{
			return super.getList(c).get(0);
		}catch(Throwable t){
			return null;
		}
	}
	
	public void reattach(MedicForm mf){
		super.getSession().update(mf);
	}
}
