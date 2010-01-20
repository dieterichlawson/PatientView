package net.frontlinesms.plugins.medic.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.frontlinesms.plugins.medic.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.medic.data.domain.people.Patient;
import net.frontlinesms.plugins.medic.data.domain.people.Person;
import net.frontlinesms.plugins.medic.data.domain.people.User;
import net.frontlinesms.plugins.medic.data.repository.hibernate.HibernateCommunityHealthWorkerDao;
import net.frontlinesms.plugins.medic.data.repository.hibernate.HibernatePatientDao;
import net.frontlinesms.plugins.medic.data.repository.hibernate.HibernateUserDao;
import net.frontlinesms.plugins.medic.search.QueryGenerator;
import net.frontlinesms.plugins.medic.ui.helpers.thinletformfields.BirthdateField;
import net.frontlinesms.plugins.medic.ui.helpers.thinletformfields.CHWComboBox;
import net.frontlinesms.plugins.medic.ui.helpers.thinletformfields.GenderComboBox;
import net.frontlinesms.plugins.medic.ui.helpers.thinletformfields.NameField;
import net.frontlinesms.plugins.medic.ui.helpers.thinletformfields.PasswordTextField;
import net.frontlinesms.plugins.medic.ui.helpers.thinletformfields.PhoneNumberField;
import net.frontlinesms.plugins.medic.ui.helpers.thinletformfields.RoleComboBox;
import net.frontlinesms.plugins.medic.ui.helpers.thinletformfields.ThinletFormField;
import net.frontlinesms.plugins.medic.ui.helpers.thinletformfields.UsernameField;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class AdminTab implements ThinletUiEventHandler, TableActionDelegate{
	
	private Map<Object, Class> listClassMapping;
	
	/** the Admin tab */
	private Object adminTab;
	/** the list of actions that can be performed*/
	private Object actionList;
	/** the panel containing the list to the left and the juicy stuff to the right
	 * you add the options to this panel to make them appear*/
	private Object splitPanel;
	
	/**the Thinlet XML files used for this tab **/
	private static final String UI_FILE_MAINTAB =  "/ui/plugins/medic/admintab/admin_tab.xml";
	private static final String UI_FILE_SEARCH_ACTION =  "/ui/plugins/medic/admintab/search_action_panel.xml";
	
	private static final String FIELDS_PANEL_CONTAINER = "fieldspanelcontainer";
	private static final String FIELDS_PANEL = "fieldspanel";
	private static final String TASK_LIST = "tasklist";
	private static final String SPLIT_PANEL = "splitpanel";
	private static final String ACTION_PANEL = "actionpanel";
	private static final String RESULTS_TABLE = "resultstable";
	private static final String ADD_BUTTON = "addbutton";
	private static final String REMOVE_BUTTON = "removebutton";
	private static final String EDIT_BUTTON = "editbutton";
	private static final String INFO_LABEL = "infolabel";
	private static final String SEARCH_BOX = "searchbox";
	
	private static final String FIELD_SUFFIX = "field";
	
	Map<Class, Map> classMapping;
	
	/**the daos used by this tab**/
	HibernateCommunityHealthWorkerDao chwDao;
	HibernatePatientDao patientDao;
	HibernateUserDao userDao;
	
	UiGeneratorController uiController;
	ApplicationContext appCon;
	AdvancedTable advancedTable;
	
	
	public AdminTab(UiGeneratorController uiController, ApplicationContext appCon){
		this.uiController = uiController;
		this.appCon = appCon;
		init();
		
	}
	
	public void init(){
		//init the main, static components
		adminTab = uiController.loadComponentFromFile(UI_FILE_MAINTAB, this);
		actionList = uiController.find(adminTab, TASK_LIST);
		splitPanel = uiController.find(adminTab, SPLIT_PANEL);
		//init the different choices for the action list
		Object managePatientsChoice = uiController.createListItem("Manage Patients", getPatientPanel());
		Object manageCHWsChoice = uiController.createListItem("Manage CHWs", getCHWPanel());
		Object manageUsersChoice = uiController.createListItem("Manage Users", getUserPanel());
		//add the choices to the list
		uiController.add(actionList, managePatientsChoice);
		uiController.add(actionList, manageCHWsChoice);
		uiController.add(actionList, manageUsersChoice);
		//init the table controller, and properly set it up to handle the results
		advancedTable = new AdvancedTable(this,uiController,true);
		advancedTable.putHeader(CommunityHealthWorker.class, new String[]{"Name","Age","Phone Number"}, new String[]{"getName", "getStringAge","getPhoneNumber"});
		advancedTable.putHeader(Patient.class, new String[]{"Name","Age","CHW"}, new String[]{"getName", "getStringAge", "getCHWName"});
		advancedTable.putHeader(User.class, new String[]{"Name","Age","Role"}, new String[]{"getName", "getStringAge","getRoleName"});
		//init the Daos
		chwDao = (HibernateCommunityHealthWorkerDao) appCon.getBean("CHWDao");
		patientDao = (HibernatePatientDao) appCon.getBean("PatientDao");
		userDao = (HibernateUserDao) appCon.getBean("UserDao");
		//set the mappings for the list choices
		listClassMapping = new HashMap<Object,Class>();
		listClassMapping.put(managePatientsChoice, Patient.class);
		listClassMapping.put(manageCHWsChoice, CommunityHealthWorker.class);
		listClassMapping.put(manageUsersChoice, User.class);		
		setSelection(0);
	}
	
	private Object getCoreFields(){
		Object fieldPanel = uiController.create("panel");
		uiController.setName(fieldPanel,FIELDS_PANEL);
		Object label = uiController.createLabel("Info");
		uiController.setName(label,INFO_LABEL);
		NameField nameField = new NameField(uiController,"");
		BirthdateField bdayField = new BirthdateField(uiController,null);
		GenderComboBox genderBox = new GenderComboBox(uiController,null);
		uiController.add(fieldPanel,label);
		uiController.add(fieldPanel,nameField.getThinletPanel());
		uiController.add(fieldPanel,bdayField.getThinletPanel());
		uiController.add(fieldPanel,genderBox.getThinletPanel());
		uiController.setInteger(fieldPanel,"weightx",1);
		uiController.setInteger(fieldPanel,"weighty",1);
		uiController.setInteger(fieldPanel, "columns", 3);
		uiController.setInteger(fieldPanel, "top", 6);
		uiController.setInteger(fieldPanel, "bottom", 6);
		uiController.setInteger(fieldPanel, "left", 6);
		uiController.setInteger(fieldPanel, "right", 6);
		uiController.setInteger(fieldPanel, "gap", 6);
		
		uiController.setColumns(fieldPanel, 1);
		return fieldPanel;
	}
	
	private Object getPatientPanel(){
		//create the search panel for patients
		Object managePatientsPanel = uiController.loadComponentFromFile(UI_FILE_SEARCH_ACTION, this);
		//set the buttons to the proper names
		uiController.setText(uiController.find(managePatientsPanel,ADD_BUTTON), "Add Patient");
		uiController.setText(uiController.find(managePatientsPanel,REMOVE_BUTTON), "Remove Patient");
		uiController.setText(uiController.find(managePatientsPanel,EDIT_BUTTON), "Edit Patient");
		//load the default field set
		Object patientFields = getCoreFields();
		uiController.setText(uiController.find(patientFields,INFO_LABEL), "Patient Info");
		//create the one non-default field, the CHW box
		CHWComboBox chwcombo = new CHWComboBox(uiController,appCon,null);
		uiController.setInteger(chwcombo.getThinletPanel(), "colspan", 1);
		//add the CHW combo box to the field panel
		uiController.add(patientFields, chwcombo.getThinletPanel());
		//add the field panel to the search panel
		uiController.add(uiController.find(managePatientsPanel, FIELDS_PANEL_CONTAINER),patientFields);
		setAllEnabled(patientFields,false);
		return managePatientsPanel;
	}
	
	private Object getCHWPanel(){
		Object manageCHWsPanel = uiController.loadComponentFromFile(UI_FILE_SEARCH_ACTION, this);
		
		uiController.setText(uiController.find(manageCHWsPanel,ADD_BUTTON), "Add CHW");
		uiController.setText(uiController.find(manageCHWsPanel,REMOVE_BUTTON), "Remove CHW");
		uiController.setText(uiController.find(manageCHWsPanel,EDIT_BUTTON), "Edit CHW");
		Object chwFields = getCoreFields();
		PhoneNumberField pField = new PhoneNumberField(uiController,"Phone Number:");
		uiController.add(chwFields,pField.getThinletPanel());		
		uiController.setText(uiController.find(chwFields,INFO_LABEL), "CHW Info");
		uiController.add(uiController.find(manageCHWsPanel, FIELDS_PANEL_CONTAINER),chwFields);
		setAllEnabled(chwFields,false);
		return manageCHWsPanel;
	}
	
	private Object getUserPanel(){
		//create search panel for managing users
		Object manageUsersPanel = uiController.loadComponentFromFile(UI_FILE_SEARCH_ACTION, this);
		//set button names
		uiController.setText(uiController.find(manageUsersPanel,ADD_BUTTON), "Add User");
		uiController.setText(uiController.find(manageUsersPanel,REMOVE_BUTTON), "Remove User");
		uiController.setText(uiController.find(manageUsersPanel,EDIT_BUTTON), "Edit User");
		
		//load the default set of fields
		Object userFields = getCoreFields();
		uiController.setText(uiController.find(userFields,INFO_LABEL), "User Info");
		//create a username field
		UsernameField uField = new UsernameField(uiController,appCon,true);
		//create a password field
		PasswordTextField pwField = new PasswordTextField(uiController,"Password:");
		//create a role combobox
		RoleComboBox rCombo = new RoleComboBox(uiController,null);
		//add all the extra fields
		uiController.add(userFields, uField.getThinletPanel());
		uiController.add(userFields, pwField.getThinletPanel());
		uiController.add(userFields, rCombo.getThinletPanel());
		
		uiController.add(uiController.find(manageUsersPanel,FIELDS_PANEL_CONTAINER),userFields);
		setAllEnabled(userFields,false);
		return manageUsersPanel;
	}

	
	private void setAllEnabled(Object container, boolean enabled){
		try{
		uiController.setEnabled(container,enabled);
		}catch(Throwable t){}
		for(Object b : uiController.getItems(container)){
			uiController.setEnabled(b, enabled);
			try{
				setAllEnabled(b, enabled);
			}catch(Throwable t){}
		}
	}
	
	private void setAllEditable(Object container, boolean editable){
		try{
		uiController.setEditable(container,editable);
		}catch(Throwable t){}
		for(Object b : uiController.getItems(container)){
			uiController.setEnabled(b, editable);
			try{
				setAllEditable(b, editable);
			}catch(Throwable t){}
		}
	}
	
	private Object getSearchField(){
		return uiController.find(splitPanel, SEARCH_BOX);
	}
	
	private Object getResultsTable(){
		return uiController.find(splitPanel, RESULTS_TABLE);
	}
	
	private Object getFieldsPanel(){
		return uiController.find(splitPanel,FIELDS_PANEL);
	}
	
	private Object getActionPanel(){
		return uiController.find(splitPanel, ACTION_PANEL);
	}
	
	private String getFieldName(String field){
		return field + FIELD_SUFFIX;
	}
	
	private Object getField(String field){
		return uiController.find(getFieldsPanel(),getFieldName(field));
	}
	
	private void setSelection(int index){
		Object panel = uiController.getAttachedObject(uiController.getItem(actionList, index));
		uiController.remove(getActionPanel());
		uiController.add(splitPanel, panel);
		uiController.setSelectedIndex(actionList, index);
		uiController.setText(getSearchField(), "");
		search("");
	}
	
	public void listSelectionChanged(){
		Object panel = uiController.getAttachedObject(uiController.getSelectedItem(actionList));
		uiController.remove(getActionPanel());
		uiController.add(splitPanel, panel);
		uiController.setText(getSearchField(), "");
		search("");
	}
	
	public Object getMainPanel(){
		return adminTab;
	}

	public void doubleClickAction(Object selectedObject) {
		System.out.println("Double Click");
	}

	public Object getTable() {
		return getResultsTable();
	}

	public void selectionChanged(Object selectedObject) {
		personSelected((Person) selectedObject);
	}
	
	
	private void personSelected(Person p){
		for(Object o : uiController.getItems(getFieldsPanel())){
			setField(p,o);
		}
	}
	
	private void setField(Person p, Object fieldContainer){
		ThinletFormField ff = (ThinletFormField) uiController.getAttachedObject(fieldContainer);
		if(ff.getClass() == NameField.class){
			ff.setResponse(p.getName());
		}else if(ff.getClass() == BirthdateField.class){
			
		}
	}
	
	
	public void search(String text){
		Class c = listClassMapping.get(uiController.getSelectedItem(actionList));
		Collection cresults = null;
		if(c == Patient.class){
			cresults = patientDao.getPatientsByName(text, 32);
		}else if(c == CommunityHealthWorker.class){
			cresults = chwDao.getCommunityHealthWorkerByName(text,32);
		}else if(c == User.class){
			cresults = userDao.getUsersByName(text,32);
		}
		ArrayList results = new ArrayList();
		results.addAll(cresults);
		advancedTable.setResults(results);
	}
	
	public QueryGenerator getQueryGenerator(){
		return null;
	}
}
