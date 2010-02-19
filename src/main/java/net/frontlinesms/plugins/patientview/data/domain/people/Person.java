package net.frontlinesms.plugins.patientview.data.domain.people;

import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.List;

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
import javax.persistence.Lob;
import javax.persistence.Table;

import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.history.HistoryManager;
import net.frontlinesms.plugins.patientview.ui.dialogs.imagechooser.ImageUtils;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.hibernate.annotations.IndexColumn;


@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn( name="persontype",discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue("person")
@Table(name="medic_people")
public abstract class Person{
	
	
	public static enum Gender{ MALE("medic.common.male"),FEMALE("medic.common.female"),TRANSGENDER("medic.common.transgender"); 	

		private Gender(String name){
			this.name = name;
		}
	
		private String name;
	
		public String toString(){
			return InternationalisationUtils.getI18NString(name);
		}

		public static Gender getGenderForName(String name){
			for(Gender g : Gender.values()){
				if(name.equalsIgnoreCase(g.toString())){
					return g;
				}
			}
			return null;
		}
	}
	
	/** Unique id for this entity.  This is for hibernate usage. */
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true,nullable=false,updatable=false)
	private long pid;
	
	/**
	 * Name of this person
	 */
	@IndexColumn(name="name_index")
	private String name;
	
	/**
	 * Birthdate of this person
	 */
	private long birthdate;
	
	/**
	 * Gender of this person. Right now, possibilities are m,f,t.
	 * Should figure out a better way to do this
	 */
	@Enumerated(EnumType.ORDINAL)
	private Gender gender;
	
	@Lob
	private byte[] unscaledImageContent;

	@Lob
	private byte[] thumbnailImageContent;
	
	/**
	 * skeleton constructor for hibernate
	 */
	public Person(){}
	
	/**
	 * Protected constructor for person, used only by subclasses
	 * @param name Name of the person
	 * @param gender Gender of the Person (options are m,f,t)
	 * @param birthdate birthdate of the person
	 */
	protected Person(String name, Gender gender, Date birthdate){
		this.name = name;
		this.gender = gender;
		this.birthdate = birthdate.getTime();
	}
	
	public long getPid() {
		return pid;
	}
	
	public String getStringID(){
		return ""+ pid;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		HistoryManager.logNameChange(this, name);
		this.name = name;
	}

	public Date getBirthdate() {
		return new Date(birthdate);
	}

	public void setBirthdate(Date birthdate) {
		HistoryManager.logBirthdateChange(this,birthdate.toLocaleString());
		this.birthdate = birthdate.getTime();
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		HistoryManager.logGenderChange(this, gender);
		this.gender = gender;
	}
	
	public int getAge() {
		if(getBirthdate() == null){
			return 0;
		}
	    Date today = new Date();
	    Date d= getBirthdate();
	    int years = today.getYear() - d.getYear() - 1;
	    if(today.getMonth() >= d.getMonth() && today.getDay() >= d.getDay()){
	    	years++;
	    }
	    return years;
	 }
	
	public String getStringAge(){
		return String.valueOf(getAge());
	}
	
	public String getStringGender(){
		return getGender().toString();
	}
	
	
	public boolean hasImage(){
		return unscaledImageContent !=null;
	}
	
	 public BufferedImage getImage() {
	     return ImageUtils.getImageFromByteArray(unscaledImageContent);
	 }
	 
	 public BufferedImage getResizedImage(){
		 return ImageUtils.getImageFromByteArray(thumbnailImageContent);
	 }

	 public void setImage(BufferedImage image, String type) {
		HistoryManager.logImageChange(this);
	    unscaledImageContent = ImageUtils.getByteArrayForImage(ImageUtils.getLargeImage(image), type);
	    thumbnailImageContent = ImageUtils.getByteArrayForImage(ImageUtils.getThumbnailImage(image), type);
	 }
	
	public abstract String getPersonType();
	
}
