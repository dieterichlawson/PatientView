package net.frontlinesms.plugins.medic.data.repository.hibernate;

import java.util.Collection;
import java.util.List;

import org.hibernate.Query;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.medic.data.domain.people.Person;
import net.frontlinesms.plugins.medic.data.domain.response.MedicFormResponse;

public class HibernateMedicFormResponseDao extends BaseHibernateDao<MedicFormResponse>{

	private String responseForSubjectQuery = "select mformr from MedicFormResponse mformr where mformr.subject.pid =";

	protected HibernateMedicFormResponseDao() {
		super(MedicFormResponse.class);
	}

	public void deleteMedicFormResponse(MedicFormResponse response) {
		super.delete(response);
	}

	public Collection<MedicFormResponse> getAllFormResponses() {
		return super.getAll();
	}

	public void saveMedicFormResponse(MedicFormResponse response) {
		super.saveWithoutDuplicateHandling(response);
	}

	public void updateMedicFormResponse(MedicFormResponse response) {
		super.updateWithoutDuplicateHandling(response);
	}
	
	public List<MedicFormResponse> getFormResponsesForSubject(Person p){
		Query q = super.getSession().createQuery(responseForSubjectQuery + p.getPid());
		return q.list();
	}
}
