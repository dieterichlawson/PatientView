package net.frontlinesms.plugins.patientview.ui.administration;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import java.util.ArrayList;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.framework.DataType;
import net.frontlinesms.plugins.patientview.data.domain.framework.Field;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.framework.PersonAttribute;
import net.frontlinesms.plugins.patientview.data.domain.framework.PersonAttribute.PersonType;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormFieldDao;
import net.frontlinesms.plugins.patientview.data.repository.PersonAttributeDao;
import net.frontlinesms.plugins.patientview.data.repository.PersonAttributeResponseDao;
import net.frontlinesms.plugins.patientview.ui.advancedtable.AdvancedTableActionDelegate;
import net.frontlinesms.plugins.patientview.ui.advancedtable.AdvancedTableController;
import net.frontlinesms.plugins.patientview.ui.personpanel.CommunityHealthWorkerPanel;
import net.frontlinesms.plugins.patientview.ui.personpanel.PatientPanel;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.springframework.context.ApplicationContext;

public class AttributeAdministrationPanelController implements AdministrationTabPanel, ThinletUiEventHandler, AdvancedTableActionDelegate {

	
	private static final String PANEL_TITLE = "admin.attributes.panel.title";
	
	public String getListItemTitle() {
		return getI18NString(PANEL_TITLE);
	}

	public Object getPanel() {
		return mainPanel;
	}

	/**boolean to signify whether we are editing the screen for patients (true) or chws(false) **/
	private boolean currentlyEditingPatient;
	
	/**Thinlet Objects**/
	private Object fieldSearchTable;
	private Object fieldSearchBar;
	private Object labelTextField;
	private Object currentItemTable;
	private Object dataTypeComboBox;
	
	private Object mainPanel;
	
	private AdvancedTableController fieldSearchTableController;
	private AdvancedTableController currentItemTableController;
	
	
	//DAOs
	private PersonAttributeDao attributeDao;
	private PersonAttributeResponseDao attributeResponseDao;
	private MedicFormFieldDao formFieldDao;
	
	//resource files containing ui components
	private static final String UI_FILE_AAG_VIEW_EDITOR = "/ui/plugins/patientview/administration/attributeAdministrationPanel.xml";
	
	/** the Ui Controller**/
	private UiGeneratorController uiController;
	
	//i18n
	private static final String LABEL_COLUMN = "medic.common.labels.label";
	private static final String PARENT_FORM_COLUMN = "medic.common.labels.parent.form";
	private static final String DATA_TYPE_COLUMN = "datatype.datatype";

	private static final String ALREADY_RESPONDED_TO_DIALOG = "admin.attributes.responses.already.present.dialog";

	private static final String ATTRIBUTE_INFO_MISSING_DIALOG = "admin.attributes.fields.not.filled.out.dialog";
	
	public AttributeAdministrationPanelController(UiGeneratorController uiController, ApplicationContext appContext){
		this.uiController = uiController;
		//load resources from files
		mainPanel = uiController.loadComponentFromFile(UI_FILE_AAG_VIEW_EDITOR, this);
		//initialize all the uiController components
		fieldSearchTable = uiController.find(mainPanel,"fieldSearchTable");
		fieldSearchBar = uiController.find(mainPanel,"fieldSearchBar");
		labelTextField = uiController.find(mainPanel,"labelTextField");
		currentItemTable = uiController.find(mainPanel,"currentItemList");
		dataTypeComboBox = uiController.find(mainPanel,"dataTypeComboBox");
		
		//initialize the advanced tables
		fieldSearchTableController = new AdvancedTableController(this,uiController,fieldSearchTable);
		currentItemTableController = new AdvancedTableController(this,uiController,currentItemTable);
		
		fieldSearchTableController.putHeader(MedicFormField.class, new String[]{getI18NString(LABEL_COLUMN), getI18NString(PARENT_FORM_COLUMN)},
																   new String[]{"getLabel","getParentFormName" });
		
		currentItemTableController.putHeader(Field.class, new String[]{getI18NString(LABEL_COLUMN), getI18NString(DATA_TYPE_COLUMN)},
				   										  new String[]{"getLabel","getDataTypeName"});
		currentItemTableController.setNoResultsMessage(getI18NString("admin.attributes.advancedtable.no.results.message"));
		//initialize the combo box choices
		uiController.removeAll(dataTypeComboBox);
		for(DataType d: DataType.values()){
			if(!(d.equals(DataType.CURRENCY_FIELD) || 
				d.equals(DataType.EMAIL_FIELD) || 
				d.equals(DataType.PASSWORD_FIELD) || 
				d.equals(DataType.WRAPPED_TEXT) || 
				d.equals(DataType.TRUNCATED_TEXT))){
			Object choice = uiController.createComboboxChoice(d.toString(), d);
			uiController.add(dataTypeComboBox,choice);
			}
		}
		//initialize the DAOs
		attributeResponseDao = (PersonAttributeResponseDao) appContext.getBean("PersonAttributeResponseDao");
		attributeDao = (PersonAttributeDao) appContext.getBean("PersonAttributeDao");
		formFieldDao = (MedicFormFieldDao) appContext.getBean("MedicFormFieldDao");
		//setting up the toggle button
		patientToggleButtonClicked();
		uiController.setSelected(uiController.find(mainPanel,"patientToggle"), true);
		uiController.setSelected(uiController.find(mainPanel,"chwToggle"), false);
		fieldSearchBarKeyPress("");
	}
	
	/**
	 * Switches from editing chw attributes to editing patient attributes
	 */
	public void patientToggleButtonClicked(){
		currentlyEditingPatient=true;
		uiController.setEnabled(fieldSearchTable,true);
		uiController.setEnabled(fieldSearchBar,true);
		uiController.setEnabled(uiController.find(mainPanel,"fieldSearchLabel"),true);
		updateCurrentItemTable();
	}
	
	/**
	 * Switches from editing Patient attributes to editing CHW attributes
	 */
	public void chwToggleButtonClicked(){
		currentlyEditingPatient=false;
		uiController.setEnabled(fieldSearchTable,false);
		uiController.setEnabled(fieldSearchBar,false);
		uiController.setEnabled(uiController.find("fieldSearchLabel"),false);
		uiController.setText(fieldSearchBar, "");
		updateCurrentItemTable();
	}
	
	/**
	 * Called by thinlet when the text in the field search box changes,
	 * updates the field search list
	 * @param text
	 */
	public void fieldSearchBarKeyPress(String text){
		updateFieldSearchTable(formFieldDao.getFieldsByName(text));
		uiController.setText(labelTextField, "");
		uiController.setText(dataTypeComboBox, "");
		uiController.setSelectedIndex(dataTypeComboBox, -1);
	}
	
	private void updateFieldSearchTable(List<MedicFormField> fields){
		fieldSearchTableController.setResults(fields);
	}
	
	/**
	 * Called by thinlet when the add button is pressed
	 * Makes sure all the required information is available, and 
	 * then adds the attribute, or directs the user to enter more data
	 */
	public void addItemButtonPressed(){
		String label = uiController.getText(labelTextField);
		if((label != "" && label !=null) && uiController.getSelectedItem(dataTypeComboBox) != null){
			DataType dataType = (DataType) uiController.getAttachedObject(uiController.getSelectedItem(dataTypeComboBox));
			PersonType personType = (currentlyEditingPatient) ? PersonType.PATIENT : PersonType.CHW;
			PersonAttribute newAttribute = new PersonAttribute(label,dataType);
			newAttribute.setPersonType(personType);
			attributeDao.saveAttribute(newAttribute);
		}else if(fieldSearchTableController.getCurrentlySelectedObject() != null){
			MedicFormField field = (MedicFormField) fieldSearchTableController.getCurrentlySelectedObject();
			field.setIsAttributePanelField(true);
			formFieldDao.updateField(field);
		}else{
			uiController.createDialog(getI18NString(ATTRIBUTE_INFO_MISSING_DIALOG));
		}
		updateCurrentItemTable();
		clearInputs();
	}
	
	private void clearInputs(){
		uiController.setText(labelTextField, "");
		uiController.setText(dataTypeComboBox, "");
		uiController.setSelectedIndex(dataTypeComboBox, -1);
		uiController.setText(fieldSearchBar, "");
		fieldSearchBarKeyPress("");
	}
	
	public void removeItemButtonPressed(){
		Object field = currentItemTableController.getCurrentlySelectedObject();
		if(field instanceof MedicFormField){
			((MedicFormField) field).setIsAttributePanelField(false);
			formFieldDao.updateField((MedicFormField) field);
		}else if(field instanceof PersonAttribute){
			if(attributeResponseDao.getResponsesForAttribute((PersonAttribute) field).size() == 0){
				attributeDao.deleteAttribute((PersonAttribute) field);
			}else{
				uiController.alert(getI18NString(ALREADY_RESPONDED_TO_DIALOG));
			}
		}
		updateCurrentItemTable();
	}
	
	private void updateCurrentItemTable(){
		if(currentlyEditingPatient){
			List<Field> patientFields = new ArrayList<Field>();
			patientFields.addAll(formFieldDao.getAttributePanelFields());
			patientFields.addAll(attributeDao.getAttributesForPersonType(PersonType.PATIENT));
			currentItemTableController.setResults(patientFields);
		} else{
			currentItemTableController.setResults(attributeDao.getAttributesForPersonType(PersonType.CHW));
		}
	}
	
	public void fieldSearchTableSelectionChanged(){
		uiController.setText(fieldSearchBar, ((MedicFormField) uiController.getAttachedObject(uiController.getSelectedItem(fieldSearchTable))).getLabel());
	}

	public void doubleClickAction(Object selectedObject) {/*do nothing*/}

	public Object getTable() {
		return null;
	}

	public void resultsChanged() {/*do nothing*/}

	public void selectionChanged(Object selectedObject) {/*do nothing*/}
	
	public String getIconPath() {
		return "/icons/patient_data_card.png";
	}
}
