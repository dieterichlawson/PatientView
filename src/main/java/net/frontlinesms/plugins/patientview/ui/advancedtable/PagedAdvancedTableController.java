package net.frontlinesms.plugins.patientview.ui.advancedtable;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import java.util.List;

import net.frontlinesms.plugins.patientview.search.PagedResultSet;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
public class PagedAdvancedTableController extends AdvancedTableController implements ThinletUiEventHandler{

	private PagedResultSet resultSet;
	
	protected Object pagingControls;
	protected Object mainPanel;
	private final static String PAGING_CONTROLS_XML = "/ui/plugins/patientview/components/pagingControls.xml";
		
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
		setResults(resultSet.getResultsPage());
	}

	/**
	 * action method for left page button
	 */
	public void pageLeft(){
		this.resultSet.previousPage();
		this.setResults(resultSet.getResultsPage());
		updatePagingControls();
	}
	
	/**
	 * action method for right page button
	 */
	public void pageRight(){
		this.resultSet.nextPage();
		this.setResults(resultSet.getResultsPage());
		updatePagingControls();
	}
	
	
	//TODO: this should be private
	public void updatePagingControls(){
		System.out.println(this.resultSet.getTotalResults());
		if(resultSet.getTotalResults() == 0){
			uiController.setEnabled(uiController.find(pagingControls, "rightPageButton"),false);
			uiController.setEnabled(uiController.find(pagingControls, "leftPageButton"),false);
			uiController.setText(uiController.find(pagingControls, "resultsLabel"),getI18NString("pagingcontrols.no.results"));
			return;
		}
		
		//set the paging buttons
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
}
