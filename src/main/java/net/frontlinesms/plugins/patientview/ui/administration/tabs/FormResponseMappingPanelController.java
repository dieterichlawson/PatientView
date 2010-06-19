package net.frontlinesms.plugins.patientview.ui.administration.tabs;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import java.util.Date;
import java.util.List;

import net.frontlinesms.data.events.EntitySavedNotification;
import net.frontlinesms.events.EventBus;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.events.FrontlineEventNotification;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormDao;
import net.frontlinesms.plugins.patientview.search.impl.FormMappingResultSet;
import net.frontlinesms.plugins.patientview.ui.administration.AdministrationTabPanel;
import net.frontlinesms.plugins.patientview.ui.advancedtable.AdvancedTableActionDelegate;
import net.frontlinesms.plugins.patientview.ui.advancedtable.HeaderColumn;
import net.frontlinesms.plugins.patientview.ui.advancedtable.PagedAdvancedTableController;
import net.frontlinesms.plugins.patientview.ui.components.CandidateSearchPanel;
import net.frontlinesms.plugins.patientview.ui.components.FlexibleFormResponsePanel;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.DateField;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.FormFieldDelegate;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.ThinletFormField;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;
public class FormResponseMappingPanelController implements AdministrationTabPanel, ThinletUiEventHandler, AdvancedTableActionDelegate, EventObserver, FormFieldDelegate{

	private UiGeneratorController uiController;
	private ApplicationContext appCon;
	
	private Object mainPanel;
	private Object actionPanel;
	private Object comboBox;
	private PagedAdvancedTableController tableController;
	private FormMappingResultSet resultSet;
	private MedicFormResponse currentResponse;
	
	private static final String UI_FILE ="/ui/plugins/patientview/administration/responsemapping/formResponseMappingAdministrationPanel.xml";
		
	public FormResponseMappingPanelController(UiGeneratorController uiController, ApplicationContext appCon) {
		this.uiController = uiController;
		this.appCon = appCon;
		((EventBus) appCon.getBean("eventBus")).registerObserver(this);
		init();
	}

	private void init(){
		this.mainPanel = uiController.loadComponentFromFile(UI_FILE,this);
		actionPanel = uiController.find(mainPanel,"actionPanel");
		//set up the table
		tableController = new PagedAdvancedTableController(this,uiController, uiController.find(mainPanel,"tablePanel"));
		tableController.putHeader(MedicFormResponse.class, HeaderColumn.createColumnList(new String[]{getI18NString("medic.common.labels.form.name"),getI18NString("medic.common.labels.date.submitted"),getI18NString("medic.common.labels.submitter")}, 
				 																		 new String[]{"/icons/form.png","/icons/date_sent.png","/icons/user_sender.png"},
				 																		 new String[]{"getFormName","getStringDateSubmitted","getSubmitterName"}));
		tableController.setNoResultsMessage(getI18NString("medic.form.response.mapping.panel.no.responses.yet"));
		tableController.enableRefreshButton(appCon);
		//set up the results set
		resultSet = new FormMappingResultSet(appCon);
		resultSet.setSearchingMapped(false);
		tableController.setResultsSet(resultSet);
		//set up the control panel
		DateField dateField = new DateField(uiController,getI18NString("medic.common.labels.date.submitted"),this);
		dateField.setLabelIcon("/icons/date.png");
		uiController.add(uiController.find(mainPanel,"controlPanel"),dateField.getThinletPanel());
		//uiController.add(uiController.find(mainPanel,"controlPanel"),uiController.createLabel("   "));
		//create the form combo box
		List<MedicForm> forms = ((MedicFormDao) appCon.getBean("MedicFormDao")).getAllMedicForms();
		comboBox = uiController.create("combobox");
		uiController.add(comboBox,uiController.createComboboxChoice(getI18NString("medic.common.all.forms"), null));
		for(MedicForm mf: forms){
			uiController.add(comboBox,uiController.createComboboxChoice(mf.getName(), mf));
		}
		uiController.setAction(comboBox, "formChanged(this.selected)", null, this);
		uiController.setWeight(comboBox,1,0);
		Object label = uiController.createLabel(getI18NString("medic.common.form"));
		uiController.setIcon(label, "/icons/form.png");
		uiController.add(uiController.find(mainPanel,"controlPanel"),label);
		uiController.add(uiController.find(mainPanel,"controlPanel"),comboBox);
		dateField.setRawResponse(new Date());
		uiController.setText(comboBox, getI18NString("medic.common.all.forms"));
		tableController.updateTable();
	}
	
	public String getListItemTitle() {
		return getI18NString("admin.actionlist.map.form.responses");
	}

	public Object getPanel() {
		return mainPanel;
	}

	public void selectionChanged(Object selectedObject) {
		uiController.removeAll(actionPanel);
		if(selectedObject == null){
			return;
		}
		currentResponse = (MedicFormResponse) selectedObject;
		uiController.add(actionPanel,new FlexibleFormResponsePanel(uiController, appCon,currentResponse).getMainPanel());
		uiController.add(actionPanel, new CandidateSearchPanel(uiController,appCon,currentResponse,this).getMainPanel());
	}
	
	public void toggleChanged(Object button){
		if(uiController.getName(button).equals("mappedToggle")){
			resultSet.setSearchingMapped(true);
		}else if(uiController.getName(button).equals("unmappedToggle")){
			resultSet.setSearchingMapped(false);
		}
		tableController.updateTable();
		tableController.updatePagingControls();
		tableController.setSelected(0);
		if(tableController.getResultsSet().getTotalResults() == 0){
			selectionChanged(null);
		}
	}

	public void doubleClickAction(Object selectedObject) {/*do nothing*/}
	public void resultsChanged() {/*do nothing*/}

	public void notify(FrontlineEventNotification notification) {
		if(notification instanceof EntitySavedNotification){
			tableController.updateTable();
			tableController.setSelected(0);
		}
	}

	/**
	 * Called by the candidate search panel when a mapping event occurs
	 */
	public void currentResponseMappingChanged(){
		//if we're in the 'unmapped' panel and the 
		if(uiController.isSelected(uiController.find(mainPanel,"unmappedToggle")) && currentResponse.getSubject() != null){
			tableController.updateTable();
			tableController.setSelected(0);
			if(tableController.getResultsSet().getTotalResults() == 0){
				selectionChanged(null);
			}
		}else{
			selectionChanged(tableController.getCurrentlySelectedObject());
		}
	}
	
	public String getIconPath() {
		return "/icons/map_form.png";
	}
	
	/**
	 * Called when the date filtering field has changed
	 * @see net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.FormFieldDelegate#formFieldChanged(net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.ThinletFormField, java.lang.String)
	 */
	public void formFieldChanged(ThinletFormField changedField, String newValue) {
		resultSet.setAroundDate(((DateField) changedField).getRawResponse());
		tableController.updateTable();
	}
	
	/**
	 * Called when the form filtering combobox has changed
	 * @param selectedIndex
	 */
	public void formChanged(int selectedIndex){
		MedicForm mf = (MedicForm) uiController.getAttachedObject(uiController.getItem(comboBox, selectedIndex));
		resultSet.setForm(mf);
		tableController.updateTable();
	}
	
	public void viewWillAppear() {}
}
