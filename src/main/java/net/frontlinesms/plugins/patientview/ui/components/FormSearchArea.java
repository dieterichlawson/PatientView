package net.frontlinesms.plugins.patientview.ui.components;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormDao;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class FormSearchArea implements ThinletUiEventHandler{

	protected Object mainPanel;
	protected Object resultsList;
	
	protected UiGeneratorController uiController;
	
	protected MedicForm currentForm;
	
	protected SearchAreaDelegate<MedicForm> delegate;
	
	protected MedicFormDao formDao;
	
	protected List<MedicForm> results;
	
	private static final String SEARCH_AREA_XML = "/ui/plugins/patientview/components/searchPanel.xml";
	
	public FormSearchArea(UiGeneratorController uiController, ApplicationContext appCon, SearchAreaDelegate<MedicForm> delegate){
		this.uiController = uiController;
		mainPanel = uiController.loadComponentFromFile(SEARCH_AREA_XML, this);
		resultsList = uiController.find(mainPanel,"formList");
		formDao = (MedicFormDao) appCon.getBean("MedicFormDao");
		this.delegate =delegate;
		textChanged("");
	}
	
	public Object getMainPanel(){
		return mainPanel;
	}
	
	public void textChanged(String searchString){
		List<MedicForm> results = formDao.getMedicFormsByName(searchString);
		setResults(results);
	}
	
	protected void setResults(List<MedicForm> results){
		this.results = results;
		uiController.removeAll(resultsList);
		for(MedicForm mf: results){
			uiController.add(resultsList,uiController.createListItem(mf.getName(), mf));
		}
		uiController.setSelectedIndex(resultsList, 0);
		selectionChanged();
	}
	
	public MedicForm getCurrentlySelectedForm(){
		return (MedicForm) uiController.getAttachedObject(uiController.getSelectedItem(resultsList));
	}
	
	public void selectionChanged(){
		currentForm = (MedicForm) uiController.getAttachedObject(uiController.getSelectedItem(resultsList));
		if(delegate != null){
			delegate.selectionChanged(currentForm);
		}
	}
	
	
}
