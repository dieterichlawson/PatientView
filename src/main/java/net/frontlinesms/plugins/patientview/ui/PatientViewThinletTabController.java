package net.frontlinesms.plugins.patientview.ui;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;
import net.frontlinesms.FrontlineUtils;
import net.frontlinesms.plugins.PluginController;
import net.frontlinesms.plugins.patientview.DummyDataGenerator;
import net.frontlinesms.plugins.patientview.PatientViewPluginController;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.framework.PersonAttribute;
import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.people.User.Role;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormFieldResponse;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicMessageResponse;
import net.frontlinesms.plugins.patientview.data.domain.response.PersonAttributeResponse;
import net.frontlinesms.plugins.patientview.data.repository.UserDao;
import net.frontlinesms.plugins.patientview.registration.ui.RegistrationScreenController;
import net.frontlinesms.plugins.patientview.search.simplesearch.SimpleSearchController;
import net.frontlinesms.plugins.patientview.security.UserSessionManager;
import net.frontlinesms.plugins.patientview.security.ui.LoginScreen;
import net.frontlinesms.plugins.patientview.ui.administration.AdministrationTabController;
import net.frontlinesms.plugins.patientview.ui.advancedtable.AdvancedTableActionDelegate;
import net.frontlinesms.plugins.patientview.ui.advancedtable.HeaderColumn;
import net.frontlinesms.plugins.patientview.ui.advancedtable.PagedAdvancedTableController;
import net.frontlinesms.plugins.patientview.ui.detailview.DetailViewController;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.apache.log4j.Logger;

public class PatientViewThinletTabController implements ThinletUiEventHandler, AdvancedTableActionDelegate {

	/** Logging object */
	private final Logger LOG = FrontlineUtils.getLogger(this.getClass());

	/** The {@link PluginController} that owns this class. */
	private final PatientViewPluginController pluginController;

	public PatientViewPluginController getPluginController() {
		return pluginController;
	}

	/** The {@link UiGeneratorController} that shows the tab. */
	private final UiGeneratorController uiController;

	// Thinlet UI objects
	/** the main tab **/
	private Object mainTab;

	private AdministrationTabController adminTab;

	/** login screen */
	private LoginScreen loginScreen;

	private Object mainPanel;

	// UI Resource file paths
	private static final String XML_MEDIC_TAB = "/ui/plugins/patientview/patientViewTab.xml";

	// other sub-controllers
	/** controller for the detailed view **/
	private DetailViewController detailViewController;
	private PagedAdvancedTableController tableController;

	// Search Controllers
	private SimpleSearchController simpleSearch;

	// i18n strings
	private static final String NAME_COLUMN = "medic.common.labels.name";
	private static final String BDAY_COLUMN = "thinletformfields.birthdate";
	private static final String ID_COLUMN = "medic.common.labels.id";
	private static final String PHONE_NUMBER_COLUMN = "medic.common.labels.phone.number";
	private static final String SENDER_COLUMN = "medic.common.labels.sender";
	private static final String SUBJECT_COLUMN = "medic.common.labels.subject";
	private static final String DATE_SENT_COLUMN = "medic.common.labels.date.sent";
	private static final String DATE_SUBMITTED_COLUMN = "medic.common.labels.date.submitted";
	private static final String MESSAGE_CONTENT_COLUMN = "medic.common.labels.message";
	private static final String LABEL_COLUMN = "medic.common.labels.label";
	private static final String PARENT_FORM_COLUMN = "medic.common.labels.parent.form";
	private static final String FORM_NAME_COLUMN = "medic.common.labels.form.name";
	private static final String FIELD_LABEL_COLUMN = "medic.common.labels.field.label";
	private static final String RESPONSE_COLUMN = "medic.common.labels.response";
	private static final String CHW_COLUMN = "medic.common.chw";
	private static final String LOGGED_IN_MESSAGE = "login.logged.in.as";

	/**
	 * Create a new instance of this class.
	 * 
	 * @param pluginController
	 * @param uiController
	 */
	public PatientViewThinletTabController(PatientViewPluginController pluginController, UiGeneratorController uiController) {
		this.pluginController = pluginController;
		this.uiController = uiController;
		loginScreen = new LoginScreen(uiController, this, pluginController.getApplicationContext());
		initialInit();
	}

	public Object getTab() {
		return mainTab;
	}

	/**
	 * performs the initialization required for the login screen
	 */
	public void initialInit() {
		//load the main tab, and then gut it
		mainTab = uiController.loadComponentFromFile(XML_MEDIC_TAB, this);
		uiController.removeAll(mainTab);
		UserDao userDao = (UserDao) pluginController.getApplicationContext().getBean("UserDao");
		if(userDao.getAllUsers().size() ==0){
			DummyDataGenerator ddg = new DummyDataGenerator(pluginController, uiController);
			uiController.add(mainTab, ddg.getMainPanel());
		}else{
			uiController.add(mainTab, loginScreen.getMainPanel());
		}
	}
	
	public void dummyDataDone(){
		uiController.removeAll(mainTab);
		uiController.add(mainTab, loginScreen.getMainPanel());
	}

	/**
	 * ends the user's session, and returns them to the login screen
	 */
	public void logout() {
		UserSessionManager.getUserSessionManager().logout();
		uiController.removeAll(mainTab);
		loginScreen.reset();
		uiController.add(mainTab, loginScreen.getMainPanel());
		if (adminTab != null) {
			uiController.remove(adminTab.getMainPanel());
		}
	}

	/**
	 * performs the initialization required for the main patient view screen
	 */
	public void init() {
			uiController.removeAll(uiController.find(mainTab,"medic"));
			uiController.add(uiController.find(mainTab,"medic"),uiController.find(uiController.loadComponentFromFile(XML_MEDIC_TAB, this),"medicTabMainPanel"));
			detailViewController = new DetailViewController(uiController.find(mainTab,"detailViewPanel"),uiController,pluginController.getApplicationContext());
			uiController.setInteger(uiController.find(mainTab,"splitPanel"), "divider", (int) (uiController.getWidth() * 0.56));
			mainPanel =  uiController.find(mainTab,"medicTabMainPanel");
			//if user is an admin, add the admin tab
			if(UserSessionManager.getUserSessionManager().getCurrentUserRole() == Role.ADMIN){
				adminTab = new AdministrationTabController(uiController,pluginController.getApplicationContext());
				uiController.add(uiController.getParent(getTab()),adminTab.getMainPanel());
			}
			if(UserSessionManager.getUserSessionManager().getCurrentUserRole() == Role.REGISTRAR){
				uiController.removeAll(mainPanel);
				RegistrationScreenController rsc = new RegistrationScreenController(uiController,pluginController.getApplicationContext(),this);
				uiController.add(mainPanel,rsc.getMainPanel());
			}else{
			//initialize the results table
			tableController = new PagedAdvancedTableController(this, uiController, uiController.find(mainTab, "resultTable"));
			//create all the column labels
			String nameLabel = getI18NString(NAME_COLUMN);
			String bdayLabel = getI18NString(BDAY_COLUMN);
			String idLabel = getI18NString(ID_COLUMN);
			String phoneNumberLabel=getI18NString(PHONE_NUMBER_COLUMN);
			String chwLabel=getI18NString(CHW_COLUMN);
			String senderLabel = getI18NString(SENDER_COLUMN);
			String subjectLabel = getI18NString(SUBJECT_COLUMN);
			String dateSentLabel =getI18NString(DATE_SENT_COLUMN);
			String dateSubmittedLabel =getI18NString(DATE_SUBMITTED_COLUMN);
			String messageContentLabel=getI18NString(MESSAGE_CONTENT_COLUMN);
			String labelLabel =getI18NString(LABEL_COLUMN);
			String parentFormLabel =getI18NString(PARENT_FORM_COLUMN);
			String formNameLabel = getI18NString(FORM_NAME_COLUMN);
			String fieldLabelLabel =getI18NString(FIELD_LABEL_COLUMN);
			String responseLabel =getI18NString(RESPONSE_COLUMN);
			//create all the table headers
			tableController.putHeader(CommunityHealthWorker.class, HeaderColumn.createColumnList(new String[]{nameLabel, bdayLabel, idLabel,phoneNumberLabel},
																								 new String[]{"/icons/user.png", "/icons/cake.png", "/icons/key.png","/icons/phone_number.png"},
																								 new String[]{"getName", "getStringBirthdate", "getStringID","getPhoneNumber"}));
			tableController.putHeader(Patient.class, HeaderColumn.createColumnList(new String[]{nameLabel, bdayLabel, idLabel,chwLabel},
					 																			 new String[]{"/icons/user.png", "/icons/cake.png", "/icons/key.png","/icons/user_phone.png"},
					 																			 new String[]{"getName", "getStringBirthdate", "getStringID","getCHWName"}));
			tableController.putHeader(MedicForm.class,  HeaderColumn.createColumnList(new String[]{nameLabel}, new String[]{"/icons/form.png"}, new String[]{"getName"}));
			tableController.putHeader(PersonAttribute.class, HeaderColumn.createColumnList(new String[]{labelLabel}, new String[]{"/icons.textfield.png"},new String[]{"getLabel"}));
			tableController.putHeader(PersonAttributeResponse.class, HeaderColumn.createColumnList(new String[]{labelLabel, senderLabel,subjectLabel, dateSubmittedLabel,responseLabel},
																								   new String[]{"/icons/tag_purple.png", "/icons/user_sender.png","", "/icons/date_sent.png","/icons/description.png"},
																								   new String[]{"getAttributeLabel","getSubmitterName","getSubjectName","getStringDateSubmitted","getValue"}));
			tableController.putHeader(MedicFormField.class, HeaderColumn.createColumnList(new String[]{labelLabel, parentFormLabel},new String[]{"/icons/tag_purple.png", "/icons/form.png"}, new String[]{"getLabel","getParentFormName"}));
			tableController.putHeader(MedicMessageResponse.class,HeaderColumn.createColumnList(new String[]{senderLabel,dateSentLabel, messageContentLabel},
																  							   new String[]{"/icons/user_sender.png", "/icons/date_sent.png","/icons/description.png"},
																  							   new String[]{"getSubmitterName","getStringDateSubmitted","getMessageContent"}));
			tableController.putHeader(MedicFormResponse.class, HeaderColumn.createColumnList(new String[]{formNameLabel, senderLabel,subjectLabel, dateSubmittedLabel}, 
															 								 new String[]{"/icons/form.png","/icons/user_sender.png","", "/icons/date_sent.png"},
															 								 new String[]{"getFormName","getSubmitterName","getSubjectName","getStringDateSubmitted"}));
			tableController.putHeader(MedicFormFieldResponse.class, HeaderColumn.createColumnList(new String[]{fieldLabelLabel, senderLabel,subjectLabel, dateSubmittedLabel,responseLabel}, 
					 												new String[]{"/icons/tag_purple.png", "/icons/user_sender.png","", "/icons/date_sent.png","/icons/description.png"},
					 												new String[]{"getFieldLabel","getSubmitterName","getSubjectName","getStringDateSubmitted","getValue"}));
			
			tableController.enableRefreshButton(pluginController.getApplicationContext());
			Object label = uiController.createLabel(getI18NString(LOGGED_IN_MESSAGE) + " " + UserSessionManager.getUserSessionManager().getCurrentUser().getName());
			Object logoutButton = uiController.createButton(getI18NString("buttons.logout"));
			uiController.setAction(logoutButton, "logout()", null, this);
			uiController.setIcon(logoutButton, "/icons/exit.png");
			uiController.add(uiController.find(tableController.getMainPanel(),"bottomButtonPanel"),logoutButton,0);
			uiController.add(uiController.find(tableController.getMainPanel(),"bottomButtonPanel"),label,0);
			// intialize the search controllers
			simpleSearch = new SimpleSearchController(uiController, pluginController.getApplicationContext(), tableController);
			uiController.add(uiController.find(mainTab, "searchContainer"), simpleSearch.getMainPanel());
		}
	}

	// TableActionDelegate methods

	public void doubleClickAction(Object selectedObject) {}
	public void resultsChanged() {}

	public void selectionChanged(Object selectedObject) {
		detailViewController.selectionChanged(selectedObject);
	}

}
