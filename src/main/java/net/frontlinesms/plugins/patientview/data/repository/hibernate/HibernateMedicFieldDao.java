package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.Collection;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicField;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicField.PersonType;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;

import org.hibernate.Query;

public class HibernateMedicFieldDao extends BaseHibernateDao<MedicField>{

	private static final String dvFieldsForPersonQuery = "select distinct ff from MedicField ff, MedicFieldResponse mfr where ff.isDetailViewField=true and mfr.field = ff and mfr.subject.pid =";
	
	private static final String dvFieldsForPersonTypeQuery = "select mf from MedicField mf where mf.isDetailViewField = true and mf.detailViewPersonType =";
	
	private static final String fieldsByName = "select mf from MedicField mf where mf.label like ";
	
	protected HibernateMedicFieldDao() {
		super(MedicField.class);
	}

	public void deleteMedicField(MedicField field) {
		if(field.isDetailViewField() && !(field instanceof MedicFormField)){
			super.delete(field);
		}
		
	}

	public Collection<MedicField> getAllMedicFields() {
		return super.getAll();
	}

	public void saveMedicField(MedicField field) {
		super.saveWithoutDuplicateHandling(field);
	}

	public void updateMedicField(MedicField field) {
		super.updateWithoutDuplicateHandling(field);
	}
	
	public Collection<MedicField> getDetailViewFieldsForPerson(Person p){
			Query q = super.getSession().createQuery(dvFieldsForPersonQuery + p.getPid());
			return q.list();
	}
	
	public Collection<MedicField> getDetailViewFieldsForPersonType(PersonType p){
		Query q = super.getSession().createQuery(dvFieldsForPersonTypeQuery + "'"+p+"'");
		return q.list();
	}
	
	public Collection<MedicField> getAllPossibleDetailViewFieldsForPerson(Person p){
		Query q = null;
		if(p instanceof Patient){
			q = super.getSession().createQuery(dvFieldsForPersonTypeQuery + "'PATIENT'");
		}else{
			q = super.getSession().createQuery(dvFieldsForPersonTypeQuery + "'CHW'");
		}
		return q.list();
	}
	
	public Collection<MedicField> getFieldsByName(String s){
		String q = fieldsByName + "'%" + s + "%'";
		return super.getSession().createQuery(q).list();
	}
}
