package net.frontlinesms.plugins.patientview.data.repository;

import java.util.Collection;

import net.frontlinesms.plugins.patientview.data.domain.response.Response;

public interface ResponseDao {
	
	//TODO Right now we just offer basic CRUD operations, with the intense stuff being done in the query generating classes. Add more data access options as needed

	public void saveResponse(Response s);

	public void deleteResponse(Response s);
	
	public void updateResponse(Response s);
	
	public Collection<Response> getAllResponses();
}
