package net.frontlinesms.plugins.patientview.graph;

import net.frontlinesms.plugins.patientview.data.domain.graph.NumericGraphSettings;
import net.frontlinesms.plugins.patientview.data.domain.graph.GraphSettings.DateRange;
import net.frontlinesms.plugins.patientview.data.domain.graph.NumericGraphSettings.NumericGraphType;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.jfree.data.time.TimeSeries;
import org.springframework.context.ApplicationContext;

public class NumericGraphController extends GraphController<NumericGraphSettings> implements ThinletUiEventHandler {

	public NumericGraphController(NumericGraphSettings settings, Object graphPanel, UiGeneratorController uiController, ApplicationContext appCon) {
		super(settings, graphPanel, NumericGraphSettings.class, uiController, appCon);
		
	}
	
	private static final String CONTROL_UI_XML = "/ui/plugins/patientview/graphControls.xml";

	private Object dateRangeCombo;
	private Object graphTypeCombo;
	
	@Override
	public Object getControlPanel() {
		Object panel = uiController.loadComponentFromFile(CONTROL_UI_XML, this);
		dateRangeCombo = uiController.find(panel,"comboOne");
		int index = 0;
		for(DateRange dr: DateRange.values()){
			uiController.add(dateRangeCombo, uiController.createComboboxChoice(dr.name(), dr));
			if(dr.equals(settings.getDateRange())){
				uiController.setSelectedIndex(dateRangeCombo, index);
				uiController.setText(dateRangeCombo, dr.name());
			}
			index++;
		}
		index=0;
		graphTypeCombo = uiController.find(panel,"comboTwo");
		for(NumericGraphType gt: NumericGraphType.values()){
			uiController.add(dateRangeCombo, uiController.createComboboxChoice(gt.name(), gt));
			if(gt.equals(settings.getNumericGraphType())){
				uiController.setSelectedIndex(graphTypeCombo, index);
				uiController.setText(graphTypeCombo, gt.name());
			}
			index++;
		}
		return null;
	}

	@Override
	public Object getGraph() {
		 TimeSeries series = null;
		 boolean cumulative = settings.getNumericGraphType().equals(NumericGraphType.CUMULATIVE);
		 series = new TimeSeries(settings.getTitle());
		 series.add
		return null;
	}
	
	public void comboOneChanged(){
		settings.setDateRange(getSelectedDateRange());
		settingsDao.saveOrUpdateGraphSettings(settings);
	}
	
	public void comboTwoChanged(){
		settings.setNumericGraphType(getSelectedGraphType());
		settingsDao.saveOrUpdateGraphSettings(settings);
	}

	private NumericGraphType getSelectedGraphType(){
		return uiController.getAttachedObject(uiController.getSelectedItem(graphTypeCombo),NumericGraphType.class);
	}
	
	private DateRange getSelectedDateRange(){
		return uiController.getAttachedObject(uiController.getSelectedItem(dateRangeCombo),DateRange.class);
	}
}
