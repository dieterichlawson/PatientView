package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.Collection;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormDao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

public class HibernateMedicFormDao extends BaseHibernateDao<MedicForm> implements MedicFormDao{
	
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

	public Collection<MedicForm> getMedicFormsForString(String s){
		String q = formsForStringQ + "'%" + s + "%'";
		return super.getSession().createQuery(q).list();
	}

	/**
	 * @see net.frontlinesms.plugins.patientview.data.repository.MedicFormDao#getMedicFormForForm(net.frontlinesms.plugins.forms.data.domain.Form)
	 */
	public MedicForm getMedicFormForForm(Form form) {
		DetachedCriteria c = super.getCriterion().forClass(MedicForm.class);
		c.add(Restrictions.eq("form", form));
		try{
			return super.getList(c).get(0);
		}catch(Throwable t){
			return null;
		}
	}
}
