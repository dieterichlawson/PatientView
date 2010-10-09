package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.List;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormDao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

public class HibernateMedicFormDao extends BaseHibernateDao<MedicForm> implements MedicFormDao{
	
	protected HibernateMedicFormDao() {
		super(MedicForm.class);
	}

	public void saveMedicForm(MedicForm form) {
		super.saveWithoutDuplicateHandling(form);
	}

	public void updateMedicForm(MedicForm form) {
		super.updateWithoutDuplicateHandling(form);
	}
	
	public void deleteMedicForm(MedicForm form) {
		super.delete(form);
	}

	public List<MedicForm> getAllMedicForms() {
		return super.getAll();
	}

	public List<MedicForm> findMedicFormsByName(String nameFragment){
		DetachedCriteria c = DetachedCriteria.forClass(MedicForm.class);
		c.add(Restrictions.like("name", nameFragment,MatchMode.ANYWHERE));
		return super.getList(c);
	}

	public MedicForm getMedicFormForForm(Form form) {
		DetachedCriteria c = DetachedCriteria.forClass(MedicForm.class);
		c.add(Restrictions.eq("vanillaForm", form));
		return super.getUnique(c);
	}
}
