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
import net.frontlinesms.plugins.medic.history.HistoryManager;
import net.frontlinesms.ui.ExtendedThinlet;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

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
	private static final String UI_FILE_AAG_VIEW_EDITOR = "/ui/plugins/medic/AtAGlance/AAG_view_editor.xml";
	private static final String UI_FILE_PERSON_AAG =  "/ui/plugins/medic/AtAGlance/person_AAG.xml";
	
	/** the Ui Controller**/
	private ExtendedThinlet thinlet;
	
	//i18n
	private static final String FRAME_TITLE = "editdetailview.title";
	private static final String TYPE_COLUMN = "medic.common.labels.type";
	private static final String LABEL_COLUMN = "medic.common.labels.label";
	private static final String PERSON_NAME = "editdetailview.preview.name";
	private static final String ID = "editdetailview.preview.id";
	private static final String AGE = "editdetailview.preview.age";
	private static final String CHW = "editdetailview.preview.chw";
	private static final String PHONE_NUMBER = "editdetailview.preview.phone.number";
	private static final String ID_LABEL = "medic.common.labels.id";
	private static final String FEMALE = "medic.common.female";
	private static final String AGE_LABEL = "medic.common.labels.age";
	private static final String CHW_LABEL = "medic.common.chw";
	private static final String PHONE_NUMBER_LABEL = "medic.common.labels.phone.number";
	private static final String PATIENT_AAG_LABEL = "detailview.patient.at.a.glance";
	private static final String CHW_AAG_LABEL = "detailview.chw.at.a.glance";
	
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
			if(!(d.equals(DataType.CURRENCY_FIELD) || d.equals(DataType.EMAIL_FIELD) || d.equals(DataType.PASSWORD_FIELD))){
			Object choice = thinlet.createComboboxChoice(d.toString(), d);
			thinlet.add(dataTypeComboBox,choice);
			}
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
		FrameLauncher f = new FrameLauncher(InternationalisationUtils.getI18NString(FRAME_TITLE),thinlet,1150,500,null)
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
		thinlet.setText(labelTextField, "");
		thinlet.setText(dataTypeComboBox, "");
		thinlet.setSelectedIndex(dataTypeComboBox, -1);
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
		if((label != "" && label !=null) && thinlet.getSelectedItem(dataTypeComboBox) != null){
			DataType dataType = (DataType) thinlet.getAttachedObject(thinlet.getSelectedItem(dataTypeComboBox));
			PersonType personType = (currentlyEditingPatient) ? PersonType.PATIENT : PersonType.CHW;
			MedicField newField = new MedicField(label,dataType);
			newField.setDetailViewField(true);
			newField.setDetailViewPersonType(currentlyEditingPatient? PersonType.PATIENT:PersonType.CHW);
			HistoryManager.logDetailViewFieldCreated(newField);
			fieldsDao.saveMedicField(newField);
		}else if(thinlet.getSelectedItem(fieldSearchTable) != null){
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
		clearInputs();
	}
	
	private void clearInputs(){
		thinlet.setText(labelTextField, "");
		thinlet.setText(dataTypeComboBox, "");
		thinlet.setSelectedIndex(dataTypeComboBox, -1);
		thinlet.setText(fieldSearchBar, "");
		thinlet.removeAll(fieldSearchTable);
	}
	
	public void removeItemButtonPressed(){
		MedicField field = (MedicField) thinlet.getAttachedObject(thinlet.getSelectedItem(currentItemTable));
		HistoryManager.logDetailViewFieldRemoved(field);
		if(field instanceof MedicFormField){
			field.setDetailViewField(false);
			field.setDetailViewPersonType(null);
			fieldsDao.updateMedicField(field);
		}else{
			fieldsDao.deleteMedicField(field);
			fieldResponseDao.deleteResponsesForField(field);
		}
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
			for(MedicField f: fieldsDao.getDetailViewFieldsForPersonType(PersonType.CHW)){
				Object item = thinlet.createLabel(f.getLabel() + ":");
				thinlet.add(previewPanel,item);
				thinlet.setInteger(item,"colspan",1);
			}
		}
	}
	
	private Object getPersonPanel(){
		Object labelPanel = thinlet.find(personPanel,"labelPanel");
		thinlet.setText(thinlet.find(labelPanel,"label1"), InternationalisationUtils.getI18NString(PERSON_NAME));
		thinlet.setText(thinlet.find(labelPanel,"label2"), InternationalisationUtils.getI18NString(ID_LABEL)+": "+ InternationalisationUtils.getI18NString(ID));
		thinlet.setText(thinlet.find(labelPanel,"label3"), InternationalisationUtils.getI18NString(FEMALE) );
		thinlet.setText(thinlet.find(labelPanel,"label4"), InternationalisationUtils.getI18NString(AGE_LABEL) + ": "+ InternationalisationUtils.getI18NString(AGE));
		
		if(!currentlyEditingPatient){
			thinlet.setText(thinlet.find(labelPanel,"label5"),InternationalisationUtils.getI18NString(PHONE_NUMBER_LABEL) + ": "+ InternationalisationUtils.getI18NString(PHONE_NUMBER));
			thinlet.setText(personPanel, InternationalisationUtils.getI18NString(CHW_AAG_LABEL));
		}else{
			thinlet.setText(thinlet.find(labelPanel,"label5"),InternationalisationUtils.getI18NString(CHW_LABEL) + ": "+ InternationalisationUtils.getI18NString(CHW));
			thinlet.setText(personPanel, InternationalisationUtils.getI18NString(PATIENT_AAG_LABEL));
		}
		return personPanel;
	}
	
	private void updateCurrentItemTable(){
		Object header = thinlet.create("header");
		thinlet.add(header,thinlet.createColumn(InternationalisationUtils.getI18NString(TYPE_COLUMN), null));
		//TODO: make sure that this belongs here
		thinlet.add(header,thinlet.createColumn(InternationalisationUtils.getI18NString(LABEL_COLUMN),null));
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
