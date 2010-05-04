package net.frontlinesms.plugins.patientview.ui;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import java.util.List;

import net.frontlinesms.plugins.patientview.search.QueryGenerator;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
public class PagedAdvancedTableController extends AdvancedTableController implements ThinletUiEventHandler{

	private QueryGenerator queryGenerator;
	protected Object pagingControls;
	protected Object mainPanel;
	private final static String PAGING_CONTROLS_XML = "/ui/plugins/patientview/pagingControls.xml";
		
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

	/**
	 * action method for left page button
	 */
	public void pageLeft(){
		this.getQueryGenerator().previousPage();
		updatePagingControls();
	}
	
	/**
	 * action method for right page button
	 */
	public void pageRight(){
		this.getQueryGenerator().nextPage();
		updatePagingControls();
	}
	
	private void updatePagingControls(){
		System.out.println(this.getQueryGenerator().getTotalResults());
		if(getQueryGenerator().getTotalResults() == 0){
			uiController.setEnabled(uiController.find(pagingControls, "rightPageButton"),false);
			uiController.setEnabled(uiController.find(pagingControls, "leftPageButton"),false);
			uiController.setText(uiController.find(pagingControls, "resultsLabel"),getI18NString("pagingcontrols.no.results"));
			return;
		}
		
		//set the paging buttons
		uiController.setEnabled(uiController.find(pagingControls, "leftPageButton"),getQueryGenerator().hasPreviousPage());
		uiController.setEnabled(uiController.find(pagingControls, "rightPageButton"),getQueryGenerator().hasNextPage());
		String pagingLabel = getI18NString("pagingcontrols.results")+" " + getQueryGenerator().getFirstResultOnPage() + " "+getI18NString("pagingcontrols.to")+" " +
							 getQueryGenerator().getLastResultOnPage() + " "+getI18NString("pagingcontrols.of")+" " + getQueryGenerator().getTotalResults();
		uiController.setText(uiController.find(pagingControls, "resultsLabel"),pagingLabel);
	}

	@Override
	public void setResults(List results){
		super.setResults(results);
		updatePagingControls();
	}

	public void setQueryGenerator(QueryGenerator queryGenerator) {
		this.queryGenerator = queryGenerator;
	}

	public QueryGenerator getQueryGenerator() {
		return queryGenerator;
	}
	
	public Object getMainPanel(){
		return mainPanel;
	}

}
