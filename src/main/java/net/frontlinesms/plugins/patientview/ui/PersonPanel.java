package net.frontlinesms.plugins.patientview.ui;

import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.people.User;
import net.frontlinesms.plugins.patientview.data.repository.hibernate.HibernateCommunityHealthWorkerDao;
import net.frontlinesms.plugins.patientview.data.repository.hibernate.HibernatePatientDao;
import net.frontlinesms.plugins.patientview.data.repository.hibernate.HibernateUserDao;
import net.frontlinesms.plugins.patientview.history.HistoryManager;
import net.frontlinesms.plugins.patientview.ui.dialogs.imagechooser.ImageChooser;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.BirthdateField;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.CHWComboBox;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.GenderComboBox;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.NameField;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.RoleComboBox;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.ThinletFormField;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.UsernameField;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import static net.frontlinesms.ui.i18n.InternationalisationUtils.*;

import org.springframework.context.ApplicationContext;

import thinlet.FrameLauncher;
import thinlet.Thinlet;

public class PersonPanel<E extends Person> implements ThinletUiEventHandler{

	private UiGeneratorController uiController;
	private ApplicationContext appCon;
	private Object mainPanelContainer;
	private Object mainPanel;
	private E person;
	private HibernateUserDao userDao;
	private HibernateCommunityHealthWorkerDao chwDao;
	private HibernatePatientDao patientDao;
	private boolean inEditingMode;
	
	private static String PERSON_PANEL_XML = "/ui/plugins/patientview/AtAGlance/person_AAG.xml";
	
	private static final String EDIT_PATIENT_DATA_BUTTON = "detailview.buttons.edit.patient.data";
	private static final String EDIT_CHW_DATA_BUTTON = "detailview.buttons.edit.chw.data";
	private static final String EDIT_USER_DATA_BUTTON = "personpanel.labels.edit.user.data";
	private static final String AGE_LABEL = "medic.common.labels.age";
	private static final String PHONE_NUMBER_FIELD = "medic.common.labels.phone.number";
	private static final String CHW_FIELD = "medic.common.chw";
	private static final String PATIENT_AAG ="personpanel.labels.patient.at.a.glance";
	private static final String CHW_AAG ="personpanel.labels.chw.at.a.glance";
	private static final String USER_AAG = "personpanel.labels.user.at.a.glance";
	private static final String USERNAME_LABEL = "medic.common.labels.username";
	private static final String ROLE_LABEL = "medic.common.labels.role";
	private static final String ID_LABEL = "medic.common.labels.id";
	private static final String PICTURE_TITLE_SUFFIX = "detailview.picture.title.suffix";
	
	public PersonPanel(UiGeneratorController uiController, E p, ApplicationContext appCon){
		this.uiController= uiController;
		this.person = p;
		this.userDao = (HibernateUserDao) appCon.getBean("UserDao");
		this.chwDao = (HibernateCommunityHealthWorkerDao) appCon.getBean("CHWDao");
		this.patientDao = (HibernatePatientDao) appCon.getBean("PatientDao");
		this.appCon = appCon;
		this.uiController = uiController;
		this.mainPanelContainer = uiController.create("panel");
		uiController.setInteger(mainPanelContainer, "weightx", 1);
		initPanel();
	}
	
	/**
	 * creates a new panel with non-editable fields for Person p
	 * @param p
	 */
	private void initPanel(){
		uiController.removeAll(mainPanelContainer);
		inEditingMode = false;
		mainPanel = uiController.loadComponentFromFile(PERSON_PANEL_XML,this);
		uiController.setAction(uiController.find(mainPanel,"imagePanel"),"imageClicked()", null, this);
		Object labelPanel = uiController.find(mainPanel,"labelPanel");
		if(person.hasImage()){
			uiController.setIcon(uiController.find(mainPanel, "imagePanel"), person.getResizedImage());
		}
		uiController.setText(uiController.find(labelPanel,"label1"), person.getName());
		if(person instanceof CommunityHealthWorker || person instanceof Patient){
			uiController.setText(uiController.find(labelPanel,"label2"),getI18NString(ID_LABEL) + ": "+ person.getPid());
		}else if(person instanceof User){
			uiController.setText(uiController.find(labelPanel,"label2"),getI18NString(USERNAME_LABEL) + ": "+ ((User) person).getUsername());
		}
		uiController.setText(uiController.find(labelPanel,"label3"), person.getGender().toString());
		uiController.setText(uiController.find(labelPanel,"label4"), getI18NString(AGE_LABEL) + ": " + person.getAge());
		if(person instanceof CommunityHealthWorker){
			uiController.setText(uiController.find(labelPanel,"label5"), getI18NString(PHONE_NUMBER_FIELD)+": " + ((CommunityHealthWorker) person).getContactInfo().getPhoneNumber());
			uiController.setText(mainPanel, getI18NString(CHW_AAG));
		}else if(person instanceof Patient){
			uiController.setText(uiController.find(labelPanel,"label5"), getI18NString(CHW_FIELD) + ": " + ((Patient) person).getChw().getName());
			uiController.setText(mainPanel, getI18NString(PATIENT_AAG));
		}else if(person instanceof User){
			uiController.setText(uiController.find(labelPanel,"label5"), getI18NString(ROLE_LABEL) + ": " + ((User) person).getRoleName());
			uiController.setText(mainPanel, getI18NString(USER_AAG));
		}
		uiController.add(mainPanelContainer,mainPanel);
	}
	
	
	public void imageClicked(){
		if(inEditingMode){
			ImageChooser chooser = new ImageChooser();
			if(chooser.getImage() != null){
				person.setImage(chooser.getImage(), chooser.getExtension());
			}
			if(person instanceof CommunityHealthWorker){
				try{
				chwDao.updateCommunityHealthWorker((CommunityHealthWorker) person);
				HistoryManager.logImageChange(person);
				}catch(Exception e){}
			}
			if(person instanceof Patient){
				patientDao.updatePatient((Patient) person);
				HistoryManager.logImageChange(person);
			}
		}
		else if(person.hasImage()){
			Thinlet thinlet = new Thinlet();
			Object panel = Thinlet.create("panel");
			BufferedImage image = person.getImage();
			int width =650;
			int height = 650;
			width = Math.min(width, image.getWidth());
			height = Math.min(height, image.getHeight());
			thinlet.setIcon(panel, "icon",person.getImage());
			thinlet.add(panel);
			FrameLauncher f = new FrameLauncher(person.getName() +getI18NString(PICTURE_TITLE_SUFFIX),thinlet,width + 10,height + 10,null)
			{ public void windowClosing(WindowEvent e){  dispose(); }};  	
		}
	}
	
	/**
	 * Replaces all labels in the person panel with editable controls for modifying the person's data
	 */
	private void addEditablePersonFields() {
		//set the edit image button
		uiController.setIcon(uiController.find(mainPanel,"imagePanel"), "/icons/patientview/blank_person_edit.png");
		//get the panel with all the labels in it and remove everything
		Object labelPanel = getLabelPanel();
		uiController.removeAll(labelPanel);
		//create and add the thinlet form fields
		//the name field
		NameField name = new NameField(uiController,person.getName());
		uiController.setInteger(name.getThinletPanel(),"colspan",1);
		uiController.add(labelPanel,name.getThinletPanel());
		//if person is a CHW or a patient, do the ID field
		if(person instanceof CommunityHealthWorker || person instanceof Patient){
			Object idLabel = uiController.createLabel(getI18NString(ID_LABEL) + ": " + person.getPid());
			uiController.add(labelPanel,idLabel);
			//otherwise, do the username field
		}else if (person instanceof User){
			UsernameField usernameField = new UsernameField(uiController,appCon,true,((User) person).getUsername());
			uiController.add(labelPanel,usernameField.getThinletPanel());
		}
		//the gender field
		GenderComboBox gender = new GenderComboBox(uiController,person.getGender());
		uiController.add(labelPanel,gender.getThinletPanel());
		//the birthdate field
		BirthdateField bday = new BirthdateField(uiController,person.getBirthdate());
		uiController.add(labelPanel,bday.getThinletPanel());
		//if the person is a CHW, add a phone number field
		if(person instanceof CommunityHealthWorker){
			Object phoneNumberLabel = uiController.createLabel(getI18NString(PHONE_NUMBER_FIELD)+": "+ ((CommunityHealthWorker) person).getContactInfo().getPhoneNumber());
			uiController.add(labelPanel,phoneNumberLabel);
			uiController.setText(mainPanel, getI18NString(EDIT_CHW_DATA_BUTTON));
			//if the person is a patient, add a chw field
		}else if(person instanceof Patient){
			CHWComboBox chwCombo = new CHWComboBox(uiController,appCon,((Patient) person).getChw());
			uiController.add(labelPanel,chwCombo.getThinletPanel());
			uiController.setText(mainPanel, getI18NString(EDIT_PATIENT_DATA_BUTTON));
			//if the person is a user, add a role field
		}else if(person instanceof User){
			RoleComboBox roleCombo = new RoleComboBox(uiController,((User) person).getRole());
			uiController.add(labelPanel,roleCombo.getThinletPanel());
			uiController.setText(mainPanel, getI18NString(EDIT_USER_DATA_BUTTON));
		}
		inEditingMode = true;
	}
	
	/**
	 * @return a Thinlet panel with 2 buttons inside of it for saving and cancelling
	 */
	private Object getSaveCancelButtons(){
		Object saveCancelPanel = uiController.create("panel");
		uiController.setInteger(saveCancelPanel, "columns", 2);
		uiController.setInteger(saveCancelPanel, "gap", 10);
		uiController.setInteger(saveCancelPanel, "right", 10);
		uiController.setChoice(saveCancelPanel, "halign", "fill");
		uiController.setInteger(saveCancelPanel, "weightx", 1);
		Object saveButton = uiController.createButton("Save");
		uiController.setAction(saveButton, "stopEditingWithSave()", null, this);
		uiController.setChoice(saveButton, "halign", "left");
		Object cancelButton = uiController.createButton("Cancel");
		uiController.setAction(cancelButton, "stopEditingWithoutSave()", null, this);
		uiController.setChoice(cancelButton, "halign", "right");
		uiController.add(saveCancelPanel, saveButton);
		uiController.add(saveCancelPanel, cancelButton);
		return saveCancelPanel;
	}
	
	private void validateAndSaveFieldResponses(){
		//first, we check and save the person fields
		for(Object o: getFieldsInLabelPanel()){
			ThinletFormField f = (ThinletFormField) uiController.getAttachedObject(o);
			if(f instanceof NameField){
				if(f.isValid() && ((NameField) f).hasChanged()){
					person.setName(f.getResponse());
				}
			}else if(f instanceof BirthdateField){
				if(f.isValid() && ((BirthdateField) f).hasChanged()){
					DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
					Date date = null;
					try {
						date = df.parse(f.getResponse());
						person.setBirthdate(date);
					} catch (ParseException e) {
						System.out.println("Error parsing date");
					}	
				}
			}else if(f instanceof GenderComboBox){
				if(f.isValid() && ((GenderComboBox) f).hasChanged()){
					person.setGender(((GenderComboBox) f).getRawResponse());						
				}
			}else if(f instanceof CHWComboBox){
				if(f.isValid() && ((CHWComboBox) f).hasChanged()){
					((Patient) person).setChw(((CHWComboBox) f).getRawResponse());
				}
			}else if(f instanceof RoleComboBox){
				if(f.isValid() && ((RoleComboBox) f).hasChanged()){
					((User) person).setRole(((RoleComboBox) f).getRawResponse());
				}
			}else if(f instanceof UsernameField){
				if(f.isValid() && ((UsernameField) f).hasChanged()){
					((User) person).setUsername(((UsernameField) f).getRawResponse());
				}
			}
		}
		
		//now, save the fields
		if(person instanceof CommunityHealthWorker){
			try{
				chwDao.updateCommunityHealthWorker((CommunityHealthWorker) person);
			}catch(Exception e){
				System.out.println("Error updating CHW");
				e.printStackTrace();
			}
		}else if(person instanceof Patient){
			patientDao.updatePatient((Patient) person);
		}else if(person instanceof User){
			userDao.updateUser((User) person);
		}
	}
	
	public Object getLabelPanel(){
		return uiController.find(mainPanel, "labelPanel");
	}
	
	public Object [] getFieldsInLabelPanel(){
		if(inEditingMode){
			return uiController.getItems(getLabelPanel());
		}else{
			return null;
		}
	}
	
	/**
	 * Switches the mode to editing mode, where the user can edit the person's core info
	 */
	public void switchToEditingPanel(){
		addEditablePersonFields();
		uiController.add(getLabelPanel(),getSaveCancelButtons());
	}
	
	public void stopEditingWithSave(){
		validateAndSaveFieldResponses();
		initPanel();
	}
	
	public void stopEditingWithoutSave(){
		initPanel();
	}
	
	public Object getMainPanel(){
		return mainPanelContainer;
	}
	
	public void stuff(){
	}
	
}
