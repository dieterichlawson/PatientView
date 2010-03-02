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
import net.frontlinesms.plugins.patientview.ui.AdvancedTableActionDelegate;
import net.frontlinesms.plugins.patientview.ui.AdvancedTableController;
import net.frontlinesms.plugins.patientview.ui.personpanel.CommunityHealthWorkerPanel;
import net.frontlinesms.plugins.patientview.ui.personpanel.PatientPanel;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class AttributeAdministrationPanelController implements AdministrationTabPanel, ThinletUiEventHandler, AdvancedTableActionDelegate {

	
	private static final String PANEL_TITLE = "admin.tabs.attribute.panel.title";
	
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
	private Object previewPanel;
	private Object dataTypeComboBox;
	
	private Object mainPanel;
	
	private AdvancedTableController fieldSearchTableController;
	private AdvancedTableController currentItemTableController;
	
	
	//DAOs
	private PersonAttributeDao attributeDao;
	private PersonAttributeResponseDao attributeResponseDao;
	private MedicFormFieldDao formFieldDao;
	
	//resource files containing ui components
	private static final String UI_FILE_AAG_VIEW_EDITOR = "/ui/plugins/patientview/AtAGlance/AAG_view_editor.xml";
	
	/** the Ui Controller**/
	private UiGeneratorController uiController;
	
	//i18n
	private static final String LABEL_COLUMN = "medic.common.labels.label";
	private static final String PARENT_FORM_COLUMN = "medic.common.labels.parent.form";
	private static final String DATA_TYPE_COLUMN = "datatype.datatype";
	
	public AttributeAdministrationPanelController(UiGeneratorController uiController, ApplicationContext appContext){
		this.uiController = uiController;
		//load resources from files
		mainPanel = uiController.loadComponentFromFile(UI_FILE_AAG_VIEW_EDITOR, this);
		//initialize all the uiController components
		fieldSearchTable = uiController.find(mainPanel,"fieldSearchTable");
		fieldSearchBar = uiController.find(mainPanel,"fieldSearchBar");
		labelTextField = uiController.find(mainPanel,"labelTextField");
		currentItemTable = uiController.find(mainPanel,"currentItemList");
		previewPanel = uiController.find(mainPanel,"previewPanel");
		dataTypeComboBox = uiController.find(mainPanel,"dataTypeComboBox");
		
		//initialize the advanced tables
		fieldSearchTableController = new AdvancedTableController(this,uiController,fieldSearchTable);
		currentItemTableController = new AdvancedTableController(this,uiController,currentItemTable);
		
		fieldSearchTableController.putHeader(MedicFormField.class, new String[]{getI18NString(LABEL_COLUMN), getI18NString(PARENT_FORM_COLUMN)},
																   new String[]{"getLabel","getParentFormName" });
		
		currentItemTableController.putHeader(Field.class, new String[]{getI18NString(LABEL_COLUMN), getI18NString(DATA_TYPE_COLUMN)},
				   										  new String[]{"getLabel","getDataTypeName"});
		//initialize the combo box choices
		uiController.removeAll(dataTypeComboBox);
		for(DataType d: DataType.values()){
			if(!(d.equals(DataType.CURRENCY_FIELD) || d.equals(DataType.EMAIL_FIELD) || d.equals(DataType.PASSWORD_FIELD))){
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
		updatePreview();
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
		updatePreview();
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
			uiController.createDialog("You haven't entered all the data required yet: Please either enter a data type and a title for a new attribute or select a field.");
		}
		updateCurrentItemTable();
		updatePreview();
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
				uiController.alert("You cannot delete attributes that have been responded to");
			}
		}
		updateCurrentItemTable();
		updatePreview();
	}
	
	private void updatePreview(){
		uiController.removeAll(previewPanel);
		if(currentlyEditingPatient){
			uiController.add(previewPanel,new PatientPanel(uiController).getMainPanel());
			uiController.setInteger(uiController.find(previewPanel,"personAAGPanel"), "colspan", 1);
			//add all the form attribute fields
			for(MedicFormField f: formFieldDao.getAttributePanelFields()){
				Object item = uiController.createLabel(f.getLabel() + ":");
				uiController.add(previewPanel,item);
				uiController.setInteger(item,"colspan",1);
			}
			for(PersonAttribute pa: attributeDao.getAttributesForPersonType(PersonType.PATIENT)){
				Object item = uiController.createLabel(pa.getLabel() + ":");
				uiController.add(previewPanel,item);
				uiController.setInteger(item,"colspan",1);
			}
		}else{
			uiController.add(previewPanel,new CommunityHealthWorkerPanel(uiController).getMainPanel());
			uiController.setInteger(uiController.find(previewPanel,"personAAGPanel"), "colspan", 1);
			for(PersonAttribute pa: attributeDao.getAttributesForPersonType(PersonType.CHW)){
				Object item = uiController.createLabel(pa.getLabel() + ":");
				uiController.add(previewPanel,item);
				uiController.setInteger(item,"colspan",1);
			}
		}
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
}
