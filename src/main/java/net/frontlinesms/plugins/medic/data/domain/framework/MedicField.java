package net.frontlinesms.plugins.medic.data.domain.framework;

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


@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn( name="fieldtype",discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue("field")
@Table(name="medic_fields")
public class MedicField {

	/** Unique id for this entity.  This is for hibernate usage. */
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true,nullable=false,updatable=false)
	private long fid;
	
	private String label;
	
	@Enumerated(EnumType.STRING)
	private DataType datatype;
	
	private boolean isDetailViewField;
	
	@Enumerated(EnumType.STRING)
	private PersonType detailViewPersonType;
	
	public enum PersonType{ PATIENT(),CHW(), BOTH();}
	
	public MedicField(){}


	public MedicField(String label, DataType datatype){
		this.label = label;
		this.datatype = datatype;
	}
	
	public long getFid() {
		return fid;
	}


	public String getLabel() {
		return label;
	}


	public void setLabel(String label) {
		this.label = label;
	}


	public DataType getDatatype() {
		return datatype;
	}


	public void setDatatype(DataType datatype) {
		this.datatype = datatype;
	}


	public boolean isDetailViewField() {
		return isDetailViewField;
	}


	public void setDetailViewField(boolean isDetailViewField) {
		this.isDetailViewField = isDetailViewField;
	}


	public PersonType getDetailViewPersonType() {
		return detailViewPersonType;
	}


	public void setDetailViewPersonType(PersonType detailViewPersonType) {
		if(this.detailViewPersonType != null && detailViewPersonType !=this.detailViewPersonType){
			this.detailViewPersonType = PersonType.BOTH;
		}
		this.detailViewPersonType = detailViewPersonType;
	}
	
	
}
