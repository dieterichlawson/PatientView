package net.frontlinesms.plugins.patientview.data.domain.graph;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import net.frontlinesms.plugins.patientview.data.domain.framework.DataType;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="graph_settings_type", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="gen")
@Table(name="medic_graph_settings")
public abstract class GraphSettings {

	/** Unique id for this entity.  This is for hibernate usage. */
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true,nullable=false,updatable=false)
	private long gid;
	
	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public MedicFormField getField() {
		return field;
	}

	public void setField(MedicFormField field) {
		this.field = field;
	}

	public DateRange getDateRange() {
		return  dateRange;
	}

	public void setDateRange(DateRange dateRange) {
		this.dateRange = dateRange;
	}

	@ManyToOne(cascade={},fetch=FetchType.EAGER,optional=true,targetEntity=Person.class)
	protected Person person;
	
	@ManyToOne(cascade={},fetch=FetchType.EAGER,optional=true,targetEntity=MedicFormField.class)
	protected MedicFormField field;
	
	public static enum DateRange{
		DAY,
		WEEK,
		MONTH,
		YEAR,
		LIFETIME;
	}
	
	@Enumerated(EnumType.STRING)
	protected DateRange dateRange;
	
	public abstract String getTitle();
	
	public abstract Set<DataType> getDataTypes();

	public long getGid() {
		return gid;
	}
}
