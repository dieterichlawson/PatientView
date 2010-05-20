package net.frontlinesms.plugins.patientview.ui.dashboard;

import java.util.HashMap;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.graph.GraphSettings;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.repository.GraphSettingsDao;
import net.frontlinesms.plugins.patientview.graph.GraphController;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class GraphTab extends TabController implements ThinletUiEventHandler {

	protected UiGeneratorController uiController;
	
	protected GraphSettingsDao graphSettingsDao;
	
	protected Person person;
	
	protected Object graphList;
	protected Object controlPanel;
	protected Object graphPanel;
	
	protected HashMap<Class<? extends GraphSettings>, GraphController> graphControllers;
	
	private static final String GRAPH_TAB_XML = "/ui/plugins/patientview/graphTab.xml";
	
	public GraphTab(UiGeneratorController uiController, ApplicationContext appCon) {
		super(uiController, appCon);
		this.graphSettingsDao = (GraphSettingsDao) appCon.getBean("GraphSettingsDao");
	}
	
	public void init(){
		Object panel = uiController.loadComponentFromFile(GRAPH_TAB_XML,this);
		uiController.add(mainPanel,panel);
		graphList = uiController.find(mainPanel,"graphList");
		controlPanel = uiController.find(mainPanel,"controlPanel");
		graph = uiController.find(mainPanel,"graph");
	}
	
	protected void updateGraphList(){
		List<GraphSettings> settings = graphSettingsDao.getGraphSettingsForPerson(person);
		uiController.removeAll(graphList);
		for(GraphSettings gs: settings){
			uiController.add(graphList, uiController.createListItem(gs.getTitle(), gs));
		}
		uiController.setSelectedIndex(graphList, 0);
	}
	
	public void addButtonClicked(){
		
	}
	
	public void removeButtonClicked(){
		graphSettingsDao.deleteGraphSettings(getSelectedGraph());
		updateGraphList();
	}
	
	public GraphSettings getSelectedGraph(){
		return (GraphSettings) uiController.getAttachedObject(uiController.getSelectedItem(graphList));
	}
	
	public void graphListSelectionChanged(){
		
	}
	
}

