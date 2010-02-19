package net.frontlinesms.plugins.patientview.search;

import net.frontlinesms.plugins.patientview.search.simplesearch.SimpleSearchDataType;
import net.frontlinesms.plugins.patientview.search.simplesearch.SimpleSearchEntity;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

public class FieldDescriptor {

	private SimpleSearchEntity parentEntity;
	private String displayName;
	private String databaseName;
	private SimpleSearchDataType dataType;
	
	public FieldDescriptor(SimpleSearchEntity parentEntity, String displayName, String databaseName, SimpleSearchDataType dataType) {
		super();
		this.parentEntity = parentEntity;
		this.displayName = displayName;
		this.databaseName = databaseName;
		this.dataType = dataType;
	}
	public SimpleSearchEntity getParentEntity() {
		return parentEntity;
	}
	public void setParentEntity(SimpleSearchEntity parentEntity) {
		this.parentEntity = parentEntity;
	}
	public String getDisplayName() {
		return InternationalisationUtils.getI18NString(displayName);
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getDatabaseName() {
		return databaseName;
	}
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	public SimpleSearchDataType getDataType() {
		return dataType;
	}
	public void setDataType(SimpleSearchDataType dataType) {
		this.dataType = dataType;
	}
	
}
