package net.frontlinesms.plugins.medic.data.repository.hibernate;

import java.util.Collection;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.medic.data.domain.response.Response;

public class HibernateResponseDao extends BaseHibernateDao<Response>{
	protected HibernateResponseDao() {
		super(Response.class);
	}

	public void deleteResponse(Response response) {
		super.delete(response);
	}

	public Collection<Response> getAllResponses() {
		return super.getAll();
	}

	public void saveResponse(Response response) {
		super.saveWithoutDuplicateHandling(response);
	}

	public void updateResponse(Response response) {
		super.updateWithoutDuplicateHandling(response);
	}
}
