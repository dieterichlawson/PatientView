package net.frontlinesms.plugins.patientview.data.domain.framework;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 * A field is an abstract class that has a label, datatype, and a unique identifier.
 * It should be extended to create more specific fields
 * @author Dieterich
 */
@Entity
@Table(name="medic_fields")
@DiscriminatorColumn(name="field_type", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="field")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public abstract class Field {

	/** Unique id for this entity.  This is for hibernate usage. */
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true,nullable=false,updatable=false)
	protected long fid;
	
	/**
	 * The label of this field. e.g. HIV status
	 */
	protected String label;
	
	/**
	 * The datatype of this field
	 */
	@Enumerated(EnumType.STRING)
	protected DataType datatype;
	
	public Field(){}
	
	public Field(String label, DataType dataType){
		this.label = label;
		this.datatype = dataType;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the datatype
	 */
	public DataType getDatatype() {
		return datatype;
	}
	
	/**
	 * This method is for teh advanced table controller, which cannot
	 * chain method calls (yet)
	 * @return The string name of this field's data type
	 */
	public String getDataTypeName(){
		return datatype.toString();
	}

	/**
	 * @param datatype the datatype to set
	 */
	public void setDatatype(DataType datatype) {
		this.datatype = datatype;
	}

	/**
	 * @return the fid
	 */
	public long getFid() {
		return fid;
	}
}
