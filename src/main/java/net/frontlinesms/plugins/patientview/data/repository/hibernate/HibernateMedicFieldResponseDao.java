package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicField;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFieldResponse;

import org.hibernate.Query;

public class HibernateMedicFieldResponseDao extends BaseHibernateDao<MedicFieldResponse>{

	private static final String dvFieldsForPersonQuery = "select distinct ff from MedicField ff, MedicFieldResponse mfr where ff.isDetailViewField=true and mfr.field = ff and mfr.subject.pid =";
	
	private static final String mostRecentResponseForFieldQuery = "select mf from MedicFieldResponse mf where mf.field.fid = :fid and mf.subject.pid = :pid	order by mf.dateSubmitted desc";
//	private static final String mostRecentResponseForFieldQueryC = "select mf from MedicFieldResponse mf where mf.field.fid = :fid and mf.submitter.pid = :pid	order by mf.dateSubmitted desc";
	private static final String responsesForField = "select mf from MedicFieldResponse mf where mf.field.fid = ";
	protected HibernateMedicFieldResponseDao() {
		super(MedicFieldResponse.class);
	}

	public void deleteMedicFieldResponse(MedicFieldResponse response) {
		super.delete(response);
	}

	public Collection<MedicFieldResponse> getAllFieldResponses() {
		return super.getAll();
	}

	public void saveMedicFieldResponse(MedicFieldResponse response) {
		super.saveWithoutDuplicateHandling(response);
	}

	public void updateMedicFieldResponse(MedicFieldResponse response) {
		super.updateWithoutDuplicateHandling(response);
	}
	
	public ArrayList<MedicFieldResponse> getDetailViewFieldResponsesForPerson(Person p){
		List<MedicField> fields = super.getSession().createQuery(dvFieldsForPersonQuery + p.getPid()).list();
		ArrayList<MedicFieldResponse> responses = new ArrayList<MedicFieldResponse>();
		for(MedicField ff: fields){
			Query q = null;
				 q = super.getSession().createQuery(mostRecentResponseForFieldQuery);
			
			q.setParameter("fid", ff.getFid());
			q.setParameter("pid", p.getPid());
			q.setMaxResults(1);
			try{
				responses.add((MedicFieldResponse) q.list().get(0));
			}catch(Throwable t){}
		}
		return responses;
	}
	
	
	public MedicFieldResponse getDetailViewFieldResponseForFieldPerson(MedicField f, Person p){
		Query q = null;
		q = super.getSession().createQuery(mostRecentResponseForFieldQuery);
		q.setParameter("fid", f.getFid());
		q.setParameter("pid", p.getPid());
		q.setMaxResults(1);
		try{
			return (MedicFieldResponse) q.list().get(0);
		}catch(Throwable t){
			return null;
		}
	}
	
	public boolean deleteResponsesForField(MedicField f){
		if(!f.isDetailViewField() || f instanceof MedicFormField){
			return false;
		}
		else{
			Query q = super.getSession().createQuery(responsesForField + f.getFid());
			List<MedicFieldResponse> mfrs = q.list();
			for(MedicFieldResponse mfr: mfrs){
				delete(mfr);
			}
			return true;
		}
	}
	
	public Collection<MedicFieldResponse> getResponsesForField(MedicField f){
		Query q = super.getSession().createQuery(responsesForField + f.getFid() + " order by dateSubmitted asc");
		return q.list();
	}
}
