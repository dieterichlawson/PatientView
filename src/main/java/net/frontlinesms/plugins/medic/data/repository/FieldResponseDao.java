package net.frontlinesms.plugins.medic.data.repository;

import java.util.Collection;

import net.frontlinesms.plugins.medic.data.domain.framework.MedicField;
import net.frontlinesms.plugins.medic.data.domain.people.Person;
import net.frontlinesms.plugins.medic.data.domain.response.MedicFieldResponse;

public interface FieldResponseDao {

	public void saveFieldResponse(MedicFieldResponse s);

	public void deleteFieldResponse(MedicFieldResponse s);
	
	public void updateFieldResponse(MedicFieldResponse s);
	
	public Collection<MedicFieldResponse> getAllFieldResponses();
	
	public Collection<MedicFieldResponse> getDetailViewFieldResponsesForPerson(Person p);
	
	public MedicFieldResponse getResponseForFieldAndPerson(MedicField f, Person p);
	
}
