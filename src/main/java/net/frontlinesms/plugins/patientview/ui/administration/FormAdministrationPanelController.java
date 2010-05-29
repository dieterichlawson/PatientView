package net.frontlinesms.plugins.patientview.ui.administration;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import java.util.Collection;

import net.frontlinesms.data.events.EntityDeletedNotification;
import net.frontlinesms.data.events.EntitySavedNotification;
import net.frontlinesms.data.events.EntityUpdatedNotification;
import net.frontlinesms.events.EventBus;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.events.FrontlineEventNotification;
import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField.PatientFieldMapping;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormDao;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormFieldDao;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormResponseDao;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.context.ApplicationContext;

public class FormAdministrationPanelController implements AdministrationTabPanel, ThinletUiEventHandler, EventObserver{

	private EventBus eventNotifier;
	
	private static final String FORM_PANEL_TITLE = "admin.tabs.form.panel.title";
	private static final String FIELDS_ON_FORM_PREFIX = "admin.forms.fields.on.form.prefix";
	private static final String FORM_ALREADY_RESPONDED_TO_DIALG = "admin.forms.form.already.responded.to.dialog";
	
	private static final String FORM_PANEL_XML = "/ui/plugins/patientview/administration/formAdministrationPanel.xml";

	private UiGeneratorController uiController;
	private ApplicationContext appCon;
	
	//Thinlet objects
	/**The main Thinlet container for this panel */
	private Object mainPanel;
	/**The list second from the left, with all patient view forms in it */
	private Object patientViewFormList;
	/** The list farthest to the right, with the fields of the currently selected medic form in it*/
	private Object fieldList;
	/** The panel that displays information about the field that is selected in the field list*/
	private Object fieldInfoPanel;
	
	/** The combo box that holds the choices for the form field -> patient field mapping*/
	private Object mappingComboBox;
	
	//Daos
	MedicFormDao patientViewFormDao;
	MedicFormFieldDao patientViewFieldDao;
	
	public String getListItemTitle() {
		return InternationalisationUtils.getI18NString(FORM_PANEL_TITLE);
	}

	public FormAdministrationPanelController(UiGeneratorController uiController, ApplicationContext appCon){
		this.uiController = uiController;
		this.appCon = appCon;
		init();
	}
	
	private void init(){
		mainPanel = uiController.loadComponentFromFile(FORM_PANEL_XML, this);
		patientViewFormList = uiController.find(mainPanel,"patientViewFormList");
		fieldList = uiController.find(mainPanel,"fieldList");
		fieldInfoPanel = uiController.find(mainPanel,"fieldInfoPanel");
		mappingComboBox = uiController.find(mainPanel,"mappingComboBox");
		//initialize the daos
		patientViewFormDao = (MedicFormDao) appCon.getBean("MedicFormDao");
		patientViewFieldDao = (MedicFormFieldDao) appCon.getBean("MedicFormFieldDao");
		eventNotifier = (EventBus) appCon.getBean("eventBus");
		eventNotifier.registerObserver(this);
		//initialize the lists, etc..
		populatePatientViewFormList();
	}

	/**
	 * Takes a hibernate proxy object and returns the real object
	 * @param entity
	 * @return
	 */
	public static Form initializeAndUnproxy(Form entity) {
	    if (entity == null) {
	        throw new  NullPointerException("Entity passed for initialization is null");
	    }

	    Hibernate.initialize(entity);
	    if (entity instanceof HibernateProxy) {
	        entity = (Form) ((HibernateProxy) entity).getHibernateLazyInitializer().getImplementation();
	    }
	    return entity;
	}

	
	/**
	 * Gets all forms in the Patient View system and displays them in the
	 * proper list
	 */
	private void populatePatientViewFormList(){
		Collection<MedicForm> pvForms = patientViewFormDao.getAllMedicForms();
		uiController.removeAll(patientViewFormList);
		for(MedicForm f: pvForms){
			Object item = uiController.createListItem(f.getName(), f);
			uiController.add(patientViewFormList,item);
		}
		uiController.setSelectedIndex(patientViewFormList, 0);
		patientViewFormListSelectionChanged();
	}
	
	/**
	 * Called when the user makes a selection in the list of
	 * Patient View forms. Gets the form that was selected
	 * and changes the fields that are displayed in the field list
	 */
	public void patientViewFormListSelectionChanged(){
		MedicForm selectedForm = (MedicForm) uiController.getAttachedObject(uiController.getSelectedItem(patientViewFormList));
		if(selectedForm != null)
			populateFieldList(selectedForm);
	}
	
	/**
	 * Called when the field list selection is changed.
	 */
	public void fieldListSelectionChanged(){
		MedicFormField field = (MedicFormField) uiController.getAttachedObject(uiController.getSelectedItem(fieldList));
		populateFieldMappingPanel(field);
	}
	
	/**
	 * Populates the field list with the fields of the form that is passed into
	 * this method
	 * @param form
	 */
	private void populateFieldList(MedicForm form){
		uiController.setText(uiController.find(mainPanel,"fieldListTitle"),getI18NString(FIELDS_ON_FORM_PREFIX)+ " \"" + form.getName()+"\"");
		uiController.removeAll(fieldList);
		for(MedicFormField mff: patientViewFieldDao.getFieldsOnForm(form)){
			Object item = uiController.createListItem(mff.getLabel(), mff);
			uiController.add(fieldList,item);
		}
		uiController.setSelectedIndex(fieldList, 0);
		fieldListSelectionChanged();
	}
	
	/**
	 * Called when the field selection is changed. Populates the field mapping panel 
	 * with the combo box and selects the propper mapping choice based on the value stored
	 * @param field
	 */
	private void populateFieldMappingPanel(MedicFormField field){
		uiController.removeAll(mappingComboBox);
		uiController.setAction(mappingComboBox, "mappingComboBoxSelectionChanged()", null, this);
		uiController.add(mappingComboBox,uiController.createComboboxChoice("None",null));
		uiController.setSelectedIndex(mappingComboBox,0);
		uiController.setText(mappingComboBox, "None");
		for(int i = 0; i < PatientFieldMapping.values().length; i++){
			PatientFieldMapping m = PatientFieldMapping.values()[i];
			Object choice = uiController.createComboboxChoice(m.toString(), m);
			uiController.add(mappingComboBox,choice);
			if(field.getMapping() == m){
				uiController.setSelectedIndex(mappingComboBox, i+1);
				uiController.setText(mappingComboBox, m.toString());
			}
		}
	}
	
	/**
	 * Called when the mapping combo box selection is changed. It saves the new mapping selection
	 */
	public void mappingComboBoxSelectionChanged(){
		PatientFieldMapping mapping = (PatientFieldMapping) uiController.getAttachedObject(uiController.getSelectedItem(mappingComboBox));
		MedicFormField field = (MedicFormField) uiController.getAttachedObject(uiController.getSelectedItem(fieldList));
		field.setMapping(mapping);
		patientViewFieldDao.updateField(field);
	}

	public Object getPanel() {
		return mainPanel;
	}
	
	public void removeButtonClicked() {
		MedicForm mf = (MedicForm) uiController.getAttachedObject(uiController .getSelectedItem(patientViewFormList));
		if (mf != null) {
			if (((MedicFormResponseDao) appCon.getBean("MedicFormResponseDao")) .getFormResponsesForForm(mf).size() == 0) {
				patientViewFormDao.deleteMedicForm(mf);
				populatePatientViewFormList();
			} else {
				uiController.alert(getI18NString(FORM_ALREADY_RESPONDED_TO_DIALG));
			}
		}
	}

	public void notify(FrontlineEventNotification event) {
		if(event instanceof EntitySavedNotification){
			EntitySavedNotification castEvent = (EntitySavedNotification) event;
			if(castEvent.getDatabaseEntity() instanceof Form || castEvent.getDatabaseEntity() instanceof MedicForm){
				populatePatientViewFormList();
			}
		}else if(event instanceof EntityUpdatedNotification){
			EntityUpdatedNotification castEvent = (EntityUpdatedNotification) event;
			if(castEvent.getDatabaseEntity() instanceof Form || castEvent.getDatabaseEntity() instanceof MedicForm){
				populatePatientViewFormList();
			}
		}else if(event instanceof EntityDeletedNotification){
			EntityDeletedNotification castEvent = (EntityDeletedNotification) event;
			if(castEvent.getDatabaseEntity() instanceof Form || castEvent.getDatabaseEntity() instanceof MedicForm){
				populatePatientViewFormList();
			}
		}
		
	}

	public String getIconPath() {
		return "/icons/big_form.png";
	}

}
