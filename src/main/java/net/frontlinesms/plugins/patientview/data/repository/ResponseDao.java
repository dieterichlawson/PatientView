package net.frontlinesms.plugins.patientview.data.repository;

import java.util.Collection;

import net.frontlinesms.plugins.patientview.data.domain.response.Response;

public interface ResponseDao {
	
	public void saveResponse(Response s);

	public void deleteResponse(Response s);
	
	public void updateResponse(Response s);
	
	public Collection<Response> getAllResponses();
}
