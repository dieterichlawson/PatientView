package net.frontlinesms.plugins.patientview.ui;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import net.frontlinesms.Utils;
import net.frontlinesms.plugins.PluginController;
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
import net.frontlinesms.plugins.patientview.search.QueryGenerator;
import net.frontlinesms.plugins.patientview.search.simplesearch.SimpleSearchController;
import net.frontlinesms.plugins.patientview.ui.administration.AdministrationTabController;
import net.frontlinesms.plugins.patientview.ui.detailview.DetailViewController;
import net.frontlinesms.plugins.patientview.ui.registrar.RegistrationScreenController;
import net.frontlinesms.plugins.patientview.userlogin.UserSessionManager;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.apache.log4j.Logger;

public class PatientViewThinletTabController implements ThinletUiEventHandler,
		AdvancedTableActionDelegate {

	/** Logging object */
	private final Logger LOG = Utils.getLogger(this.getClass());

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
	private AdvancedTableController tableController;

	// Search Controllers
	private SimpleSearchController simpleSearch;

	// private DrillDownSearchController drillDownSearch;

	// current search controls
	private static enum SearchState {
		SIMPLESEARCH(), DRILLDOWNSEARCH();
	}

	// i18n strings
	private static final String NAME_COLUMN = "medic.common.labels.name";
	private static final String AGE_COLUMN = "medic.common.labels.age";
	private static final String GENDER_COLUMN = "medic.common.labels.gender";
	private static final String PHONE_NUMBER_COLUMN = "medic.common.labels.phone.number";
	private static final String SENDER_COLUMN = "medic.common.labels.sender";
	private static final String SUBJECT_COLUMN = "medic.common.labels.subject";
	private static final String DATE_SENT_COLUMN = "medic.common.labels.date.sent";
	private static final String DATE_SUBMITTED_COLUMN = "medic.common.labels.date.submitted";
	private static final String MESSAGE_CONTENT_COLUMN = "medic.common.labels.message.content";
	private static final String LABEL_COLUMN = "medic.common.labels.label";
	private static final String PARENT_FORM_COLUMN = "medic.common.labels.parent.form";
	private static final String FORM_NAME_COLUMN = "medic.common.labels.form.name";
	private static final String FIELD_LABEL_COLUMN = "medic.common.labels.field.label";
	private static final String RESPONSE_COLUMN = "medic.common.labels.response";
	private static final String CHW_COLUMN = "medic.common.chw";
	private static final String LOGGED_IN_MESSAGE = "login.logged.in.as";

	private SearchState currentSearchState;

	/**
	 * Create a new instance of this class.
	 * 
	 * @param pluginController
	 * @param uiController
	 */
	public PatientViewThinletTabController(
			PatientViewPluginController pluginController,
			UiGeneratorController uiController) {
		this.pluginController = pluginController;
		this.uiController = uiController;
		loginScreen = new LoginScreen(uiController, this, pluginController
				.getApplicationContext());
		initialInit();
	}

	public Object getTab() {
		return mainTab;
	}

	/**
	 * performs the initialization required for the login screen
	 */
	public void initialInit() {
		mainTab = uiController.loadComponentFromFile(XML_MEDIC_TAB, this);
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
		uiController.removeAll(uiController.find(mainTab, "medic"));
		uiController.add(uiController.find(mainTab, "medic"), uiController
				.find(uiController.loadComponentFromFile(XML_MEDIC_TAB, this),
						"medicTabMainPanel"));
		detailViewController = new DetailViewController(uiController.find(
				mainTab, "detailViewPanel"), uiController, pluginController
				.getApplicationContext());
		uiController.setInteger(uiController.find(mainTab, "splitPanel"),
				"divider", (int) (uiController.getWidth() * 0.56));
		mainPanel = uiController.find(mainTab, "medicTabMainPanel");
		// if user is an admin, add the admin tab
		if (UserSessionManager.getUserSessionManager().getCurrentUserRole() == Role.ADMIN) {
			adminTab = new AdministrationTabController(uiController,
					pluginController.getApplicationContext());
			uiController.add(uiController.getParent(getTab()), adminTab
					.getMainPanel());
		}
		if (UserSessionManager.getUserSessionManager().getCurrentUserRole() == Role.REGISTRAR) {
			uiController.removeAll(mainPanel);
			RegistrationScreenController rsc = new RegistrationScreenController(
					uiController, pluginController.getApplicationContext(),
					this);
			uiController.add(mainPanel, rsc.getMainPanel());
		} else {
			// initialize the results table
			tableController = new AdvancedTableController(this, uiController,
					true);
			String nameLabel = getI18NString(NAME_COLUMN);
			String ageLabel = getI18NString(AGE_COLUMN);
			String genderLabel = getI18NString(GENDER_COLUMN);
			String phoneNumberLabel = getI18NString(PHONE_NUMBER_COLUMN);
			String chwLabel = getI18NString(CHW_COLUMN);
			String senderLabel = getI18NString(SENDER_COLUMN);
			String subjectLabel = getI18NString(SUBJECT_COLUMN);
			String dateSentLabel = getI18NString(DATE_SENT_COLUMN);
			String dateSubmittedLabel = getI18NString(DATE_SUBMITTED_COLUMN);
			String messageContentLabel = getI18NString(MESSAGE_CONTENT_COLUMN);
			String labelLabel = getI18NString(LABEL_COLUMN);
			String parentFormLabel = getI18NString(PARENT_FORM_COLUMN);
			String formNameLabel = getI18NString(FORM_NAME_COLUMN);
			String fieldLabelLabel = getI18NString(FIELD_LABEL_COLUMN);
			String responseLabel = getI18NString(RESPONSE_COLUMN);
			tableController.putHeader(CommunityHealthWorker.class,
					new String[] { nameLabel, ageLabel, genderLabel,
							phoneNumberLabel },
					new String[] { "getName", "getStringAge",
							"getStringGender", "getPhoneNumber" });
			tableController.putHeader(Patient.class, new String[] { nameLabel,
					ageLabel, genderLabel, chwLabel },
					new String[] { "getName", "getStringAge",
							"getStringGender", "getCHWName" });
			tableController.putHeader(MedicForm.class,
					new String[] { nameLabel }, new String[] { "getName" });
			tableController.putHeader(PersonAttribute.class,
					new String[] { labelLabel }, new String[] { "getLabel" });
			tableController.putHeader(PersonAttributeResponse.class,
					new String[] { labelLabel, senderLabel, subjectLabel,
							dateSubmittedLabel, responseLabel }, new String[] {
							"getAttributeLabel", "getSubmitterName",
							"getSubjectName", "getStringDateSubmitted",
							"getValue" });
			tableController.putHeader(MedicFormField.class, new String[] {
					labelLabel, parentFormLabel }, new String[] { "getLabel",
					"getParentFormName" });
			tableController.putHeader(MedicMessageResponse.class, new String[] {
					senderLabel, dateSentLabel, messageContentLabel },
					new String[] { "getSubmitterName",
							"getStringDateSubmitted", "getMessageContent" });
			tableController.putHeader(MedicFormResponse.class, new String[] {
					formNameLabel, senderLabel, subjectLabel,
					dateSubmittedLabel }, new String[] { "getFormName",
					"getSubmitterName", "getSubjectName",
					"getStringDateSubmitted" });
			tableController.putHeader(MedicFormFieldResponse.class,
					new String[] { fieldLabelLabel, senderLabel, subjectLabel,
							dateSubmittedLabel, responseLabel }, new String[] {
							"getFieldLabel", "getSubmitterName",
							"getSubjectName", "getStringDateSubmitted",
							"getValue" });

			currentSearchState = SearchState.SIMPLESEARCH;
			// intialize the search controllers
			simpleSearch = new SimpleSearchController(uiController,
					pluginController.getApplicationContext(), tableController);
			// drillDownSearch = new
			// DrillDownSearchController(uiController,pluginController.getApplicationContext(),tableController);
			// add the simple search controller
			uiController.add(uiController.find(mainTab, "searchContainer"),
					simpleSearch.getMainPanel());
			currentSearchState = SearchState.SIMPLESEARCH;
			// set the login label
			uiController.setText(uiController.find(mainTab, "userStatusLabel"),
					getI18NString(LOGGED_IN_MESSAGE)
							+ " "
							+ UserSessionManager.getUserSessionManager()
									.getCurrentUser().getName());
			updatePagingControls();
		}
	}

	// TableActionDelegate methods

	public void doubleClickAction(Object selectedObject) {
		if (currentSearchState == SearchState.DRILLDOWNSEARCH) {
			// drillDownSearch.drillDown(selectedObject);
		}
	}

	public void selectionChanged(Object selectedObject) {
		detailViewController.selectionChanged(selectedObject);
	}

	public Object getTable() {
		return uiController.find(mainTab, "resultTable");
	}

	public QueryGenerator getQueryGenerator() {
		if (simpleSearch != null) {
			return simpleSearch.getQueryGenerator();
		} else {
			return null;
		}
	}

	public void refresh() {
		simpleSearch.searchButtonPressed();
	}

	/**
	 * Switches from Drill-Down Search to Simple search or vice-versa when one
	 * of the selection buttons is clicked
	 * 
	 * @param sender
	 *            the button that was clicked
	 */
	public void switchSearchControls(Object sender) {
		if (uiController.getName(sender).equalsIgnoreCase("simpleSearchButton")) {
			if (currentSearchState == SearchState.SIMPLESEARCH) {
				return;
			} else {
				// uiController.remove(drillDownSearch.getMainPanel());
				uiController.add(uiController.find(mainTab, "searchContainer"),
						simpleSearch.getMainPanel());
				simpleSearch.controllerWillAppear();
				currentSearchState = SearchState.SIMPLESEARCH;
			}
		} else if (uiController.getName(sender).equalsIgnoreCase(
				"drillDownSearchButton")) {
			if (currentSearchState == SearchState.DRILLDOWNSEARCH) {
				return;
			} else {
				uiController.remove(simpleSearch.getMainPanel());
				// uiController.add(uiController.find(mainTab,"searchContainer"),
				// drillDownSearch.getMainPanel());
				// drillDownSearch.controllerWillAppear();
				currentSearchState = SearchState.DRILLDOWNSEARCH;
			}
		}
	}

	/**
	 * action method for left page button
	 */
	public void pageLeft() {
		this.getQueryGenerator().previousPage();
		updatePagingControls();
	}

	/**
	 * action method for right page button
	 */
	public void pageRight() {
		this.getQueryGenerator().nextPage();
		updatePagingControls();
	}

	private void updatePagingControls() {
		// update the paging buttons
		if (this.getQueryGenerator() == null) {
			return;
		}
		System.out.println(this.getQueryGenerator().getTotalResults());
		if (this.getQueryGenerator().getTotalResults() == 0) {
			uiController.setEnabled(uiController.find(mainTab,
					"rightPageButton"), false);
			uiController.setEnabled(uiController
					.find(mainTab, "leftPageButton"), false);
			uiController.setText(uiController.find(mainTab, "resultsLabel"),
					getI18NString("pagingcontrols.no.results"));
			return;
		}
		if (this.getQueryGenerator().hasPreviousPage()) {
			uiController.setEnabled(uiController
					.find(mainTab, "leftPageButton"), true);
		} else {
			uiController.setEnabled(uiController
					.find(mainTab, "leftPageButton"), false);
		}

		if (this.getQueryGenerator().hasNextPage()) {
			uiController.setEnabled(uiController.find(mainTab,
					"rightPageButton"), true);
		} else {
			uiController.setEnabled(uiController.find(mainTab,
					"rightPageButton"), false);
		}
		String pagingLabel = getI18NString("pagingcontrols.results") + " "
				+ getQueryGenerator().getFirstResultOnPage() + " "
				+ getI18NString("pagingcontrols.to") + " "
				+ getQueryGenerator().getLastResultOnPage() + " "
				+ getI18NString("pagingcontrols.of") + " "
				+ getQueryGenerator().getTotalResults();
		uiController.setText(uiController.find(mainTab, "resultsLabel"),
				pagingLabel);
	}

	public void resultsChanged() {
		updatePagingControls();
	}
}
