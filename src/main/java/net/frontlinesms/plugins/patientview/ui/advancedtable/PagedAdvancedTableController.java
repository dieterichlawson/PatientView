package net.frontlinesms.plugins.patientview.ui.advancedtable;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.frontlinesms.data.events.DatabaseEntityNotification;
import net.frontlinesms.events.EventBus;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.events.FrontlineEventNotification;
import net.frontlinesms.plugins.patientview.search.PagedResultSet;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;
public class PagedAdvancedTableController extends AdvancedTableController implements ThinletUiEventHandler, EventObserver{

	private PagedResultSet resultSet;
	
	protected Object pagingControls;
	protected Object mainPanel;
	private final static String PAGING_CONTROLS_XML = "/ui/plugins/patientview/components/pagingControls.xml";
		
	protected Map<Class,Boolean> refreshButtonStates = Collections.synchronizedMap(new HashMap<Class,Boolean>());
	
	public PagedAdvancedTableController(AdvancedTableActionDelegate delegate,UiGeneratorController uiController, Object panel) {
			super(delegate, uiController);
			if(panel == null){
				mainPanel = uiController.create("panel");
			}else{
				this.mainPanel = panel;
			}
			uiController.setWeight(mainPanel, 1, 1);
			uiController.setColumns(mainPanel,1);
			uiController.setGap(mainPanel, 6);
			uiController.add(mainPanel,super.getTable());
			pagingControls = uiController.loadComponentFromFile(PAGING_CONTROLS_XML, this);
			uiController.add(mainPanel,pagingControls);			
	}
	
	public void updateTable(){
		setResults(resultSet.getFreshResultsPage());
	}

	/**
	 * action method for left page button
	 */
	public void pageLeft(){
		this.resultSet.previousPage();
		this.setResults(resultSet.getFreshResultsPage());
		updatePagingControls();
	}
	
	/**
	 * action method for right page button
	 */
	public void pageRight(){
		this.resultSet.nextPage();
		this.setResults(resultSet.getFreshResultsPage());
		updatePagingControls();
	}
	
	@Override
	public void putHeader(Class headerClass, List<HeaderColumn> columns){
		super.putHeader(headerClass,columns);
		refreshButtonStates.put(headerClass, false);
	}
		
	
	//TODO: this should be private
	public void updatePagingControls(){
		if(resultSet == null || resultSet.getTotalResults() == 0){
			uiController.setEnabled(uiController.find(pagingControls, "rightPageButton"),false);
			uiController.setEnabled(uiController.find(pagingControls, "leftPageButton"),false);
			uiController.setText(uiController.find(pagingControls, "resultsLabel"),getI18NString("pagingcontrols.no.results"));
			return;
		}
		//set the paging buttons
		uiController.setEnabled(uiController.find(pagingControls, "refreshButton"),refreshButtonStates.get(currentClass));
		uiController.setEnabled(uiController.find(pagingControls, "leftPageButton"),resultSet.hasPreviousPage());
		uiController.setEnabled(uiController.find(pagingControls, "rightPageButton"),resultSet.hasNextPage());
		String pagingLabel = getI18NString("pagingcontrols.results")+" " + getResultsSet().getFirstResultOnPage() + " "+getI18NString("pagingcontrols.to")+" " +
					resultSet.getLastResultOnPage() + " "+getI18NString("pagingcontrols.of")+" " + resultSet.getTotalResults();
		uiController.setText(uiController.find(pagingControls, "resultsLabel"),pagingLabel);
	}

	@Override
	public void setResults(List results){
		super.setResults(results);
		updatePagingControls();
	}

	public Object getMainPanel(){
		return mainPanel;
	}

	public void setResultsSet(PagedResultSet resultsManager) {
		this.resultSet = resultsManager;
	}

	public PagedResultSet getResultsSet() {
		return resultSet;
	}

	public void setPagingControlBorder(boolean hasBorder){
		Object panel = uiController.find(pagingControls,"bottomButtonPanel");
		uiController.setBorder(panel, hasBorder);
		if(hasBorder){
			uiController.setInteger(panel, "top", 5);
			uiController.setInteger(panel, "left", 5);
			uiController.setInteger(panel, "right", 5);
			uiController.setInteger(panel, "bottom", 5);
		}else{
			uiController.setInteger(panel, "top", 0);
			uiController.setInteger(panel, "left", 0);
			uiController.setInteger(panel, "right", 0);
			uiController.setInteger(panel, "bottom", 0);

		}
	}
	
	public void enableRefreshButton(ApplicationContext appCon){
		((EventBus) appCon.getBean("eventBus")).registerObserver(this);
		uiController.setVisible(uiController.find(mainPanel,"refreshButton"), true);
		if(resultSet != null){
			updatePagingControls();
		}else{
			uiController.setEnabled(uiController.find(mainPanel,"refreshButton"), false);
		}
	}
	
	public void refresh(){
		refreshButtonStates.put(currentClass, false);
		updateTable();
	}

	@Override
	public void notify(FrontlineEventNotification notification) {
		if(notification instanceof DatabaseEntityNotification){
			DatabaseEntityNotification dbNotification = (DatabaseEntityNotification) notification;
			for(Class c: refreshButtonStates.keySet()){
				if(dbNotification.getDatabaseEntity().getClass().equals(c)){
					refreshButtonStates.put(c, true);
				}
			}
		}
		updatePagingControls();
	}
}
