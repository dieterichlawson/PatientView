package net.frontlinesms.plugins.medic.search.drilldownsearch.breadcrumbs;

import java.util.ArrayList;

public abstract class BreadCrumb {
	
	protected ArrayList<EntityType> restrictedEntities;
	protected EntityType type;
	
	public abstract String getName();
	
	public String getEntityTypeName(){
		return type.getName();
	}
	
	public ArrayList<EntityType> getRestrictedEntityTypes(){
		return restrictedEntities;
	}
	
	public EntityType getEntityType(){
		return type;
	}
	
	public abstract String getQueryForEntityType(EntityType e);
	
	public abstract String getResponseQueryForEntityType(EntityType e);
	
}
