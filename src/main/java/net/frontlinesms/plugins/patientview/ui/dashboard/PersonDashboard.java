package net.frontlinesms.plugins.patientview.ui.dashboard;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import java.util.ArrayList;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.people.Person.Gender;
import net.frontlinesms.plugins.patientview.data.domain.people.User.Role;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormFieldDao;
import net.frontlinesms.plugins.patientview.data.repository.PersonAttributeDao;
import net.frontlinesms.plugins.patientview.security.UserSessionManager;
import net.frontlinesms.plugins.patientview.ui.dashboard.tabs.TabController;
import net.frontlinesms.plugins.patientview.ui.personpanel.PersonAttributePanel;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

import thinlet.Thinlet;

import org.springframework.context.ApplicationContext;

import thinlet.Thinlet;
public abstract class PersonDashboard<P extends Person> extends Dashboard {

	protected P person;
	protected PersonAttributePanel attributePanel;
	
	protected List<TabController> tabs;
	
	protected boolean inEditingMode;
	
	private static final String GO_BACK_BUTTON = "patientrecord.buttons.go.back";
	
	private static final String EDIT_ATTRIBUTES = "detailview.buttons.edit.attributes";
	private static final String SAVE = "detailview.buttons.save";
	private static final String CANCEL = "detailview.buttons.cancel";
	private static final String EDIT_ATTRIBUTE_ICON = "/icons/patient_edit_";
	private static final String SAVE_ICON = "/icons/tick.png";
	private static final String CANCEL_ICON = "/icons/cross.png";
	
	public PersonDashboard(UiGeneratorController uiController, ApplicationContext appCon, P p) {
		super(uiController, appCon);
		this.person = p;
		tabs = new ArrayList<TabController>();
		initView();
	}
	
	protected abstract void init();

	public void initView(){
		init();
		//add all the tabs
		for(TabController tab: tabs){
			uiController.add(tabbedPanel,tab.getTab());
		}
		//add the attribute panel
		attributePanel = new PersonAttributePanel(uiController,appCon,person);
		uiController.add(leftPanel,attributePanel.getMainPanel());
		uiController.add(leftPanel,getBottomButtons());
	}
	
	protected Object getBottomButtons(){
		Object buttonPanel = uiController.create("panel");
		uiController.setName(buttonPanel, "buttonPanel");
		uiController.setColumns(buttonPanel, 3);
		Object leftButton = uiController.createButton(!inEditingMode?getI18NString(GO_BACK_BUTTON):getI18NString(SAVE));
		Object rightButton = uiController.createButton(!inEditingMode?getI18NString(EDIT_ATTRIBUTES):getI18NString(CANCEL));
		if(inEditingMode){
			uiController.setAction(leftButton, "saveButtonClicked", null, this);
			uiController.setAction(rightButton, "cancelButtonClicked", null, this);
			uiController.setIcon(leftButton, SAVE_ICON);
			uiController.setIcon(rightButton, CANCEL_ICON);
			
		}else{
			uiController.setAction(leftButton, "goBack()", null, this);
			uiController.setIcon(leftButton, "/icons/arrow_turn_left_large.png");
			uiController.setAction(rightButton, "editButtonClicked()", null, this);
			uiController.setIcon(rightButton, EDIT_ATTRIBUTE_ICON + (person.getGender() == Gender.MALE?"male.png":"female.png"));
			if(((PersonAttributeDao) appCon.getBean("PersonAttributeDao")).getAllAttributesForPerson(person).size() == 0 && (person.getClass().equals(CommunityHealthWorker.class) ||(person.getClass().equals(Patient.class) && ((MedicFormFieldDao) appCon.getBean("MedicFormFieldDao")).getAttributePanelFields().size() == 0 ))){
				uiController.setEnabled(rightButton,false);
			}
		}
		uiController.setHAlign(leftButton, Thinlet.LEFT);
		uiController.setVAlign(leftButton, Thinlet.BOTTOM);
		if(UserSessionManager.getUserSessionManager().getCurrentUserRole() == Role.READWRITE||
		   UserSessionManager.getUserSessionManager().getCurrentUserRole() == Role.ADMIN){
			uiController.add(buttonPanel,leftButton);
		}
		Object spacerLabel = uiController.createLabel("");
		uiController.setWeight(spacerLabel, 1, 0);
		uiController.add(buttonPanel,spacerLabel);
		uiController.setHAlign(rightButton, Thinlet.RIGHT);
		uiController.setVAlign(rightButton, Thinlet.BOTTOM);
		uiController.add(buttonPanel, rightButton);
		uiController.setWeight(buttonPanel, 1, 1);
		uiController.setVAlign(buttonPanel, Thinlet.BOTTOM);
		return buttonPanel;
	}
	
	public void goBack(){
		collapseDashboard();
	}
	
	public void editButtonClicked(){
		inEditingMode=true;
		attributePanel.switchToEditingPanel();
		uiController.remove(uiController.find(leftPanel,"buttonPanel"));
		uiController.add(leftPanel,getBottomButtons());
	}
	
	public void saveButtonClicked(){
		if(attributePanel.stopEditingWithSave()){
			inEditingMode=false;
			uiController.remove(uiController.find(leftPanel,"buttonPanel"));
			uiController.add(leftPanel,getBottomButtons());
		}
	}
	
	public void cancelButtonClicked(){
		inEditingMode=false;
		attributePanel.stopEditingWithoutSave();
		uiController.remove(uiController.find(leftPanel,"buttonPanel"));
		uiController.add(leftPanel,getBottomButtons());
	}
}
