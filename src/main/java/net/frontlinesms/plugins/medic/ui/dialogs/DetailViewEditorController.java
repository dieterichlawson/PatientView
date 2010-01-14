package net.frontlinesms.plugins.medic.ui.dialogs;

import java.awt.event.WindowEvent;
import java.util.Collection;

import net.frontlinesms.plugins.medic.data.domain.framework.DataType;
import net.frontlinesms.plugins.medic.data.domain.framework.MedicField;
import net.frontlinesms.plugins.medic.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.medic.data.domain.framework.MedicField.PersonType;
import net.frontlinesms.plugins.medic.data.repository.hibernate.HibernateMedicFieldDao;
import net.frontlinesms.plugins.medic.data.repository.hibernate.HibernateMedicFieldResponseDao;
import net.frontlinesms.plugins.medic.data.repository.hibernate.HibernateMedicFormFieldDao;
import net.frontlinesms.ui.ExtendedThinlet;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

import thinlet.FrameLauncher;

public class DetailViewEditorController implements ThinletUiEventHandler{

	/**boolean to signify whether we are editing the screen for patients (true) or chws(false) **/
	private boolean currentlyEditingPatient;
	
	/**Thinlet Objects**/
	private Object fieldSearchTable;
	private Object fieldSearchBar;
	private Object labelTextField;
	private Object currentItemTable;
	private Object previewPanel;
	private Object dataTypeComboBox;
	private Object personPanel;
	
	/**DAOs**/
	private HibernateMedicFieldDao fieldsDao;
	private HibernateMedicFormFieldDao formFieldDao;
	private HibernateMedicFieldResponseDao fieldResponseDao;
	
	/**resource files containing ui components**/
	private static final String UI_FILE_AAG_VIEW_EDITOR = "/ui/plugins/medic/AAG_view_editor.xml";
	private static final String UI_FILE_PERSON_AAG =  "/ui/plugins/medic/person_AAG.xml";
	
	/** the Ui Controller**/
	private ExtendedThinlet thinlet;
	
	public DetailViewEditorController(UiGeneratorController uiController, ApplicationContext appContext, boolean isEditingPatient){
		thinlet = new ExtendedThinlet();
		//set currently editing patient
		currentlyEditingPatient=isEditingPatient;
		//load resources from files
		Object mainPanel = uiController.loadComponentFromFile(UI_FILE_AAG_VIEW_EDITOR, this);
		personPanel = uiController.loadComponentFromFile(UI_FILE_PERSON_AAG, this);

		//initialize all the thinlet components
		fieldSearchTable = thinlet.find(mainPanel,"fieldSearchTable");
		fieldSearchBar = thinlet.find(mainPanel,"fieldSearchBar");
		labelTextField = thinlet.find(mainPanel,"labelTextField");
		currentItemTable = thinlet.find(mainPanel,"currentItemList");
		previewPanel = thinlet.find(mainPanel,"previewPanel");
		dataTypeComboBox = thinlet.find(mainPanel,"dataTypeComboBox");
		
		//initialize the combo box choices
		thinlet.removeAll(dataTypeComboBox);
		for(DataType d: DataType.values()){
			Object choice = thinlet.createComboboxChoice(d.toString(), d);
			thinlet.add(dataTypeComboBox,choice);
		}
		
		//initialize the DAOs
		fieldsDao = (HibernateMedicFieldDao) appContext.getBean("MedicFieldDao");
		formFieldDao = (HibernateMedicFormFieldDao) appContext.getBean("MedicFormFieldDao");
		fieldResponseDao = (HibernateMedicFieldResponseDao) appContext.getBean("MedicFieldResponseDao");
		
		//initialize the lists and preview box
		updatePreview();
		updateCurrentItemTable();
		
		thinlet.add(mainPanel);
		//you have to use a special framelauncher class because otherwise it will close all open windows
		FrameLauncher f = new FrameLauncher("Edit the Detail View",thinlet,1150,500,null)
		{ public void windowClosing(WindowEvent e){  dispose(); }};   
		
		if(currentlyEditingPatient){
			patientToggleButtonClicked();
			uiController.setSelected(uiController.find(mainPanel,"patientToggle"), true);
			uiController.setSelected(uiController.find(mainPanel,"chwToggle"), false);
		}else{
			chwToggleButtonClicked();
			uiController.setSelected(uiController.find(mainPanel,"chwToggle"), true);
			uiController.setSelected(uiController.find(mainPanel,"patientToggle"), false);
		}
	}
	
	
	public void patientToggleButtonClicked(){
		currentlyEditingPatient=true;
		thinlet.setEnabled(fieldSearchTable,true);
		thinlet.setEnabled(fieldSearchBar,true);
		thinlet.setEnabled(thinlet.find("fieldSearchLabel"),true);
		updateCurrentItemTable();
		updatePreview();
	}
	
	public void chwToggleButtonClicked(){
		currentlyEditingPatient=false;
		thinlet.setEnabled(fieldSearchTable,false);
		thinlet.setEnabled(fieldSearchBar,false);
		thinlet.setEnabled(thinlet.find("fieldSearchLabel"),false);
		thinlet.removeAll(fieldSearchTable);
		thinlet.setText(fieldSearchBar, "");
		updateCurrentItemTable();
		updatePreview();
	}
	
	public void fieldSearchBarKeyPress(String text){
		updateFieldSearchTable(formFieldDao.getFieldsByName(text));
	}
	
	private void updateFieldSearchTable(Collection<MedicFormField> fields){
		thinlet.removeAll(fieldSearchTable);
		for(MedicFormField f: fields){
			Object row = thinlet.createTableRow(f);
			thinlet.add(row,thinlet.createTableCell(f.getLabel()));
			thinlet.add(row,thinlet.createTableCell(f.getForm().getName()));
			thinlet.add(fieldSearchTable,row);
		}
	}
	
	public void addItemButtonPressed(){
		String label = thinlet.getText(labelTextField);
		if(label != "" && label !=null){
			DataType dataType = (DataType) thinlet.getAttachedObject(thinlet.getSelectedItem(dataTypeComboBox));
			PersonType personType = (currentlyEditingPatient) ? PersonType.PATIENT : PersonType.CHW;
			MedicField newField = new MedicField(label,dataType);
			newField.setDetailViewField(true);
			newField.setDetailViewPersonType(currentlyEditingPatient? PersonType.PATIENT:PersonType.CHW);
			fieldsDao.saveMedicField(newField);
		}else{
			MedicFormField field = (MedicFormField) thinlet.getAttachedObject(thinlet.getSelectedItem(fieldSearchTable));
			field.setDetailViewField(true);
			field.setDetailViewPersonType(currentlyEditingPatient? PersonType.PATIENT:PersonType.CHW);
			formFieldDao.updateMedicFormField(field);
		}//TODO: validation
		//validation
		if(label == "" || label == null){
			
		}
//		if(dataType == null){
//			//warn them to select a datatype
//		}
		//also disallow if field type is wrong
		
		updateCurrentItemTable();
		updatePreview();
	}
	
	public void removeItemButtonPressed(){
		MedicField field = (MedicField) thinlet.getAttachedObject(thinlet.getSelectedItem(currentItemTable));
		if(field.isDetailViewField()){
			fieldResponseDao.deleteResponsesForField(field);
		}
		fieldsDao.deleteMedicField(field);
		updateCurrentItemTable();
		updatePreview();
	}
	
	private void updatePreview(){
		thinlet.removeAll(previewPanel);
		if(currentlyEditingPatient){
			thinlet.add(previewPanel,getPersonPanel());
			thinlet.setInteger(thinlet.find(previewPanel,"personAAGPanel"), "colspan", 1);
			for(MedicField f: fieldsDao.getDetailViewFieldsForPersonType(PersonType.PATIENT)){
				Object item = thinlet.createLabel(f.getLabel() + ":");
				thinlet.add(previewPanel,item);
				thinlet.setInteger(item,"colspan",1);
			}
		}else{
			thinlet.add(previewPanel,getPersonPanel());
			thinlet.setInteger(thinlet.find(previewPanel,"personAAGPanel"), "colspan", 1);
			for(MedicField f: fieldsDao.getDetailViewFieldsForPersonType(PersonType.PATIENT)){
				Object item = thinlet.createLabel(f.getLabel() + ":");
				thinlet.add(previewPanel,item);
				thinlet.setInteger(item,"colspan",1);
			}
		}
	}
	
	private Object getPersonPanel(){
		Object labelPanel = thinlet.find(personPanel,"labelPanel");
		thinlet.setText(thinlet.find(labelPanel,"label1"), "Jane Doe");
		thinlet.setText(thinlet.find(labelPanel,"label2"), "ID: 1234567");
		thinlet.setText(thinlet.find(labelPanel,"label3"), "Female" );
		thinlet.setText(thinlet.find(labelPanel,"label4"), "Age: 37");
		if(!currentlyEditingPatient){
			thinlet.setText(thinlet.find(labelPanel,"label5"), "Phone Number: 888-999-2492");
			thinlet.setText(personPanel, "CHW at a Glance");
		}else{
			thinlet.setText(thinlet.find(labelPanel,"label5"), "CHW: John Doe");
			thinlet.setText(personPanel, "Patient at a Glance");
		}
		return personPanel;
	}
	
	private void updateCurrentItemTable(){
		Object header = thinlet.create("header");
		thinlet.add(header,thinlet.createColumn("Label", null));
		//TODO: make sure that this belongs here
		thinlet.add(header,thinlet.createColumn("Type",null));
		thinlet.removeAll(currentItemTable);
		thinlet.add(currentItemTable,header);
		if(currentlyEditingPatient){
			putInCurrentItemTable(fieldsDao.getDetailViewFieldsForPersonType(PersonType.PATIENT));
		} else{
			putInCurrentItemTable(fieldsDao.getDetailViewFieldsForPersonType(PersonType.CHW));
		}
	}
	
	private void putInCurrentItemTable(Collection<MedicField> fields){
		thinlet.removeAll(currentItemTable);
		for(MedicField f: fields){
			Object row = thinlet.createTableRow(f);
			thinlet.add(row, thinlet.createTableCell(f.getLabel()));
			thinlet.add(row, thinlet.createTableCell(f.getDatatype().toString()));
			thinlet.add(currentItemTable,row);
		}
	}
	
	public void fieldSearchTableSelectionChanged(){
		thinlet.setText(fieldSearchBar, ((MedicFormField) thinlet.getAttachedObject(thinlet.getSelectedItem(fieldSearchTable))).getLabel());
	}
}
