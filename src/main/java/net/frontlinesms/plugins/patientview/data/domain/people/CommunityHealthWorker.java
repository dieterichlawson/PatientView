package net.frontlinesms.plugins.patientview.data.domain.people;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.domain.Contact;

/**
 * A Community Health Worker (CHW) is a health worker that 
 * is responsible for the basic care of the patients in their purview. They are
 * the primary data source about patients and will fill out forms on their mobile
 * devices.
 *
 */
@Entity
@DiscriminatorValue(value="chw")
public class CommunityHealthWorker extends Person {

	@OneToOne(targetEntity = Contact.class, fetch = FetchType.EAGER,cascade=CascadeType.ALL)
	@JoinColumn(name = "contact_id")
	private Contact contactInfo;
	
	@OneToMany(cascade=CascadeType.PERSIST,mappedBy="chw",fetch=FetchType.LAZY)
	private List<Patient> patients;
	
	public CommunityHealthWorker() {}

	public CommunityHealthWorker(String name,String phoneNumber, Gender gender, Date birthdate) {
		super(name, gender, birthdate);
		contactInfo = new Contact(name,phoneNumber,null,null,null,true);
	}
	
	public Contact getContactInfo(){
		return contactInfo;
	}
	
	public String getPhoneNumber(){
		return contactInfo.getPhoneNumber();
	}
	
	/**
	 * Sets the phone number of the CHW, creating a Contact
	 * if the CHW does not already have one created.
	 * 
	 * @param phoneNumber
	 * @throws NumberFormatException
	 * @throws DuplicateKeyException
	 */
	public void setPhoneNumber(String phoneNumber) throws NumberFormatException, DuplicateKeyException{
		if (contactInfo == null){
			contactInfo = new Contact(this.getName(),phoneNumber,null,null,null,true);
		}else{
				contactInfo.setPhoneNumber(phoneNumber);
		}
	}

	public void setPatients(List<Patient> patients) {
		this.patients = patients;
	}

	public List<Patient> getPatients() {
		return patients;
	}

}
