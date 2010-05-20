package net.frontlinesms.plugins.patientview.data.domain.graph;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import net.frontlinesms.plugins.patientview.data.domain.framework.DataType;

@Entity
@DiscriminatorValue(value="num")
public class NumericGraphSettings extends GraphSettings{

	
	public static enum NumericGraphType{
		CUMULATIVE,
		NONCUMULATIVE;
	}
	
	@Enumerated(EnumType.STRING)
	private NumericGraphType numericGraphType;

	@Override
	public String getTitle() {
		return field.getLabel() + " - " + getNumericGraphType().toString();
	}

	@Override
	public Set<DataType> getDataTypes() {
		Set<DataType> results = new HashSet<DataType>();
		results.add(DataType.NUMERIC_TEXT_FIELD);
		return results;
	}

	public void setNumericGraphType(NumericGraphType numericGraphType) {
		this.numericGraphType = numericGraphType;
	}

	public NumericGraphType getNumericGraphType() {
		return numericGraphType;
	}
	
}
