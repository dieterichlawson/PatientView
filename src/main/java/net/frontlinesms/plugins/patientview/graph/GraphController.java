package net.frontlinesms.plugins.patientview.graph;

import net.frontlinesms.plugins.patientview.data.domain.graph.GraphSettings;
import net.frontlinesms.plugins.patientview.data.repository.GraphSettingsDao;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;


public abstract class GraphController<S extends GraphSettings> {
	
	protected S settings;
	protected Object graphPanel;
	private Class<S> graphSettingsClass;
	protected UiGeneratorController uiController;
	protected GraphSettingsDao settingsDao;
	
	public GraphController(S settings, Object graphPanel, Class<S> clazz, UiGeneratorController uiController, ApplicationContext appCon){
		this.setSettings(settings);
		this.graphPanel = graphPanel;
		this.setGraphSettingsClass(clazz);
		this.uiController = uiController;
		settingsDao = (GraphSettingsDao) appCon.getBean("GraphSettingsDao");
	}
	
	/**
	 * @return The controls contained inside the control panel
	 */
	public abstract Object getControlPanel();
	
	public abstract Object getGraph();

	public void setSettings(S settings) {
		this.settings = settings;
	}

	public S getSettings() {
		return settings;
	}

	public void setGraphSettingsClass(Class<S> graphSettingsClass) {
		this.graphSettingsClass = graphSettingsClass;
	}

	public Class<S> getGraphSettingsClass() {
		return graphSettingsClass;
	}
	

}
