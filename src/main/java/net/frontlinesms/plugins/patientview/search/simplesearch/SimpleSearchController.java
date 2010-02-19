package net.frontlinesms.plugins.patientview.search.simplesearch;

import java.util.Date;

import net.frontlinesms.plugins.patientview.data.domain.people.Person.Gender;
import net.frontlinesms.plugins.patientview.search.FieldDescriptor;
import net.frontlinesms.plugins.patientview.search.QueryGenerator;
import net.frontlinesms.plugins.patientview.search.SearchController;
import net.frontlinesms.plugins.patientview.ui.AdvancedTable;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.DateField;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.springframework.context.ApplicationContext;

public class SimpleSearchController implements ThinletUiEventHandler, SearchController{
	
	//ui files
	private static String UI_FILE = "/ui/plugins/patientview/simple_search.xml";
	
	//state objects
	private FieldDescriptor currentField;
	private SimpleSearchEntity currentSearchEntity;
	
	//other controllers
	private UiGeneratorController uiController;
	private SimpleSearchQueryGenerator queryGenerator;
	
	//main panels and controls
	private Object mainPanel;
	private Object fieldComboBox;
	private Object entityComboBox;
	private Object descriptorPanel;
	
	//entry controls
	private Object textField;
	private Object numberField;
	private DateField dateFieldAfter;
	private DateField dateFieldBefore;
	private Object comboBox;
	
	//i18n
	private static final String BEFORE = "simplesearch.labels.before";
	private static final String AFTER = "simplesearch.labels.after";
	
	public SimpleSearchController(UiGeneratorController uiController, ApplicationContext appCon, AdvancedTable resultsTable){
		this.uiController = uiController;
		queryGenerator = new SimpleSearchQueryGenerator(this,appCon, resultsTable);
		mainPanel = uiController.loadComponentFromFile(UI_FILE, this);
		entityComboBox = uiController.find(mainPanel, "entityComboBox");
		fieldComboBox = uiController.find(mainPanel, "fieldComboBox");
		descriptorPanel = uiController.find(mainPanel, "descriptorPanel");
		for(SimpleSearchEntity sse: SimpleSearchEntity.values()){
			uiController.add(entityComboBox, uiController.createComboboxChoice(sse.getEntityDisplayName(), sse));
		}
		uiController.setSelectedIndex(entityComboBox, 0);
		entityComboBoxSelectionChanged();
	}
			
	public void entityComboBoxSelectionChanged(){
		SimpleSearchEntity sse = (SimpleSearchEntity) uiController.getAttachedObject(uiController.getSelectedItem(entityComboBox));
		uiController.removeAll(fieldComboBox);
		for(FieldDescriptor field: sse.getFields()){
			uiController.add(fieldComboBox, uiController.createComboboxChoice(field.getDisplayName(), field));
		}
		uiController.setText(entityComboBox, sse.getEntityDisplayName());
		currentSearchEntity = sse;
		uiController.setSelectedIndex(fieldComboBox, 0);
		fieldComboBoxSelectionChanged();
		searchButtonPressed();
	}
	
	public void fieldComboBoxSelectionChanged(){
		FieldDescriptor field = (FieldDescriptor) uiController.getAttachedObject(uiController.getSelectedItem(fieldComboBox));
		uiController.setText(fieldComboBox,field.getDisplayName());
		uiController.removeAll(descriptorPanel);
		//this is a hack because we don't want to store class info for all the fields, and I don't
		//want to mess with all that reflection at this point
		if(field.getDisplayName().equalsIgnoreCase("gender")){
			uiController.add(descriptorPanel, getEnumFieldEntry(Gender.class));
		}else if(field.getDataType() == SimpleSearchDataType.STRING){
			uiController.add(descriptorPanel, getTextFieldEntry());
		}else if(field.getDataType()== SimpleSearchDataType.DATE){
			uiController.add(descriptorPanel, getDateFieldEntry());
		}else if(field.getDataType() == SimpleSearchDataType.NUMBER){
			uiController.add(descriptorPanel, getNumberFieldEntry());
		}
		currentField = field;
	}
	
	public Object getDateFieldEntry(){
		if(dateFieldAfter == null){
			dateFieldAfter = new DateField(uiController,InternationalisationUtils.getI18NString(AFTER));
			dateFieldBefore = new DateField(uiController,InternationalisationUtils.getI18NString(BEFORE));
		}
		dateFieldAfter.setResponse("");
		dateFieldBefore.setResponse("");
		Object spanel = uiController.create("panel");
		uiController.add(spanel,dateFieldAfter.getThinletPanel());
		uiController.add(spanel,dateFieldBefore.getThinletPanel());
		uiController.setInteger(spanel, "columns", 1);
		uiController.setInteger(spanel, "gap", 6);
		uiController.setInteger(spanel, "weightx", 1);
		uiController.setInteger(spanel, "rowspan", 2);
		return spanel;
	}
	
	public <E> Object getEnumFieldEntry(Class<E> enumType){
		if(comboBox == null){
			comboBox = uiController.create("combobox");
			uiController.setName(comboBox,"comboBox");
			uiController.setInteger(comboBox,"weightx",1);
			uiController.setAction(comboBox, "comboSelectionChanged()", null, this);
		}
		uiController.removeAll(comboBox);
		for(E object: enumType.getEnumConstants()){
			uiController.add(comboBox,uiController.createComboboxChoice(object.toString(), object));
		}
		
		return comboBox;
	}
	
	public Object getTextFieldEntry(){
		if(textField == null){
			textField = uiController.createTextfield("textField", "");
			uiController.setInteger(textField,"weightx",1);
			uiController.setAction(textField, "textEntryChanged()", null, this);
		}
		uiController.setText(textField,"");
		return textField;
	}
	
	public Object getNumberFieldEntry(){
		if(numberField == null){
		numberField = uiController.createTextfield("numberField", "");
		uiController.setInteger(numberField,"weightx",1);
		uiController.setAction(numberField, "textEntryChanged()", null, this);
		}
		uiController.setText(numberField,"");
		return numberField;
	}
	
	public Object getMainPanel(){
		return mainPanel;
	}
	
	public FieldDescriptor getCurrentField(){
		return currentField;
	}
	
	public SimpleSearchEntity getCurrentEntity(){
		return currentSearchEntity;
	}
	
	public Date getBeforeDate(){
		if (currentField.getDataType().equals(SimpleSearchDataType.DATE)){
			return dateFieldBefore.getDateResponse();
		}else{
			return null;
		}
	}
	
	public Date getAfterDate(){
		if (currentField.getDataType().equals(SimpleSearchDataType.DATE)){
			return dateFieldAfter.getDateResponse();
		}else{
			return null;
		}
	}
	
	public String getTextInput(){
		if (currentField.getDataType().equals(SimpleSearchDataType.STRING)){
			return uiController.getText(textField);
		}else{
			return null;
		}
	}
	
	public String getNumberInput(){
		if (currentField.getDataType().equals(SimpleSearchDataType.NUMBER)){
			return uiController.getText(numberField);
		}else{
			return null;
		}
	}
	
	public String getEnumInput(){
		return uiController.getAttachedObject(uiController.getSelectedItem(comboBox)).toString();
	}
	
	public void searchButtonPressed(){
		queryGenerator.startSearch();
	}
	
	public void textEntryChanged(){
		searchButtonPressed();
	}
	
	public void comboSelectionChanged(){
		searchButtonPressed();
	}

	public void controllerWillAppear() {
		searchButtonPressed();
	}

	public QueryGenerator getQueryGenerator() {
		return queryGenerator;
	}
	
	
		
}
