package net.frontlinesms.plugins.patientview.data.domain.graph;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import net.frontlinesms.plugins.patientview.data.domain.framework.DataType;

@Entity
@DiscriminatorValue(value="bool")
public class BooleanGraphSettings extends GraphSettings{
	
	public static enum BooleanGraphType{
		BAR,
		BAND;
	}
	
	@Enumerated(EnumType.STRING)
	protected BooleanGraphType booleanGraphType;

	@Override
	public String getTitle() {
		return field.getLabel() + " - " + booleanGraphType.toString();
	}
	
	@Override
	public Set<DataType> getDataTypes() {
		Set<DataType> results = new HashSet<DataType>();
		results.add(DataType.POSITIVENEGATIVE);
		results.add(DataType.YESNO);
		results.add(DataType.TRUEFALSE);
		return results;
	}
	
}
