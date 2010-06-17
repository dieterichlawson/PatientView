package net.frontlinesms.plugins.patientview.data.repository;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.flag.FlagDefinition;

public interface FlagDefinitionDao {

	public void saveFlagDefinition(FlagDefinition definition);
	
	public void updateFlagDefinition(FlagDefinition definition);
	
	public void deleteFlagDefinition(FlagDefinition definition);
	
	public List<FlagDefinition> getAllFlagDefinitions();
	
	public List<FlagDefinition> findFlagDefinitionsByName(String name);
	
	public FlagDefinition getFlagDefinitionByID(long id);
	
}
