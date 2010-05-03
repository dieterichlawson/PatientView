package net.frontlinesms.plugins.patientview.data.repository;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.graph.GraphSettings;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;

public interface GraphSettingsDao {
	
	public void saveOrUpdateGraphSettings(GraphSettings graphSettings);
	
	public void deleteGraphSettings(GraphSettings graphSettings);
	
	public List<GraphSettings> getAllGraphSettings();
	
	public List<GraphSettings> getGraphSettingsForPerson(Person p);
}
