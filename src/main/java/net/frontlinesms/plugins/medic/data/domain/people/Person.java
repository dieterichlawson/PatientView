package net.frontlinesms.plugins.medic.data.domain.people;

import java.awt.image.BufferedImage;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.plugins.medic.ui.dialogs.imagechooser.ImageUtils;

import org.hibernate.annotations.IndexColumn;


@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn( name="persontype",discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue("person")
@Table(name="medic_people")
public abstract class Person{
	
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
	//FIXME: limit the possibilities using annotations
	private char gender;
	
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
	protected Person(String name, char gender, Date birthdate){
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
		this.name = name;
	}

	public Date getBirthdate() {
		return new Date(birthdate);
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate.getTime();
	}

	public char getGender() {
		return gender;
	}

	public void setGender(char gender) {
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
	    unscaledImageContent = ImageUtils.getByteArrayForImage(ImageUtils.getLargeImage(image), type);
	    thumbnailImageContent = ImageUtils.getByteArrayForImage(ImageUtils.getThumbnailImage(image), type);
	 }
	
	public abstract String getPersonType();
	
}
