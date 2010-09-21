package net.frontlinesms.plugins.patientview.ui.detailview.panels;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;
import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.people.User.Role;
import net.frontlinesms.plugins.patientview.data.repository.PersonAttributeDao;
import net.frontlinesms.plugins.patientview.security.UserSessionManager;
import net.frontlinesms.plugins.patientview.ui.dashboard.CommunityHealthWorkerDashboard;
import net.frontlinesms.plugins.patientview.ui.detailview.DetailViewPanelController;
import net.frontlinesms.plugins.patientview.ui.personpanel.CommunityHealthWorkerPanel;
import net.frontlinesms.plugins.patientview.ui.personpanel.PersonAttributePanel;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

import thinlet.Thinlet;

public class CommunityHealthWorkerDetailViewPanelController implements DetailViewPanelController<CommunityHealthWorker> {

	private static final String EDIT_CHW_ATTRIBUTES = "detailview.buttons.edit.attributes";
	private static final String SAVE_CHW_ATTRIBUTES = "detailview.buttons.save";
	private static final String SEE_MORE = "detailview.buttons.see.more";
	private static final String CANCEL = "detailview.buttons.cancel";
	private static final String EDIT_ATTRIBUTE_ICON = "/icons/big_user_edit.png";
	private static final String SAVE_ICON = "/icons/tick.png";
	private static final String CANCEL_ICON = "/icons/cross.png";
	private static final String EXPAND_DETAIL_VIEW_ICON = "/icons/patient_file.png";
	
	
	private Object mainPanel;
	private UiGeneratorController uiController;
	private ApplicationContext appCon;
	private boolean inEditingMode;
	private CommunityHealthWorker currentCHW;
	
	private CommunityHealthWorkerPanel currentCHWPanel;
	private PersonAttributePanel currentAttributePanel;
	
	public CommunityHealthWorkerDetailViewPanelController(UiGeneratorController uiController,ApplicationContext appCon){
		this.uiController = uiController;
		this.appCon = appCon;
	}
	public Class getEntityClass() {
		return CommunityHealthWorker.class;
	}

	public Object getPanel() {
		return mainPanel;
	}

	/**
	 * Puts in a CHW person panel and the attribute panel
	 * to go with it.
	 * 
	 * @see net.frontlinesms.plugins.patientview.ui.detailview.DetailViewPanelController#viewWillAppear(java.lang.Object)
	 */
	public void viewWillAppear(CommunityHealthWorker p) {
		currentCHW = p;
		mainPanel = uiController.create("panel");
		uiController.setWeight(mainPanel, 1, 1);
		uiController.setColumns(mainPanel, 1);
		currentCHWPanel = new CommunityHealthWorkerPanel(uiController,appCon,p);
		currentAttributePanel =new PersonAttributePanel(uiController,appCon,p);
		uiController.add(mainPanel, currentCHWPanel.getMainPanel());
		uiController.add(mainPanel, currentAttributePanel.getMainPanel());
		uiController.add(mainPanel, getBottomButtons());
	}
	
	/**
	 * @return The buttons that go at the bottom of the panel,
	 * currently an "Edit this info" button if not in editing mode
	 * and a save/cancel pair if in editing mode.
	 */
	private Object getBottomButtons(){
		Object buttonPanel = uiController.create("panel");
		uiController.setName(buttonPanel, "buttonPanel");
		uiController.setColumns(buttonPanel, 3);
		Object leftButton = uiController.createButton(!inEditingMode?getI18NString(EDIT_CHW_ATTRIBUTES):getI18NString(SAVE_CHW_ATTRIBUTES));
		Object rightButton = uiController.createButton(!inEditingMode?getI18NString(SEE_MORE):getI18NString(CANCEL));
		if(inEditingMode){
			uiController.setAction(leftButton, "saveButtonClicked", null, this);
			uiController.setIcon(leftButton, SAVE_ICON);
			uiController.setAction(rightButton, "cancelButtonClicked", null, this);
			uiController.setIcon(rightButton, CANCEL_ICON);
		}else{
			uiController.setAction(leftButton, "editButtonClicked", null, this);
			uiController.setIcon(leftButton, EDIT_ATTRIBUTE_ICON);
			if(((PersonAttributeDao) appCon.getBean("PersonAttributeDao")).getAllAttributesForPerson(currentCHW).size() == 0){
				uiController.setEnabled(leftButton,false);
			}
			uiController.setAction(rightButton, "showCHWDashboard", null, this);
			uiController.setIcon(rightButton, EXPAND_DETAIL_VIEW_ICON);
		}
		uiController.setHAlign(leftButton, Thinlet.LEFT);
		uiController.setVAlign(leftButton, Thinlet.BOTTOM);
		if(UserSessionManager.getUserSessionManager().getCurrentUserRole() == Role.READWRITE||
		   UserSessionManager.getUserSessionManager().getCurrentUserRole() == Role.ADMIN){
				uiController.add(buttonPanel,leftButton);
		}
		Object spacerLabel = uiController.createLabel("");
		uiController.setWeight(spacerLabel, 1, 0);
		uiController.add(buttonPanel,spacerLabel);
		uiController.setHAlign(rightButton, Thinlet.RIGHT);
		uiController.setVAlign(rightButton, Thinlet.BOTTOM);
		uiController.add(buttonPanel, rightButton);
		uiController.setWeight(buttonPanel, 1, 1);
		uiController.setVAlign(buttonPanel, Thinlet.BOTTOM);
		return buttonPanel;
	}

	/**
	 * Action method for the edit button
	 */
	public void editButtonClicked(){
		inEditingMode=true;
		currentAttributePanel.switchToEditingPanel();
		uiController.remove(uiController.find(mainPanel,"buttonPanel"));
		uiController.add(mainPanel,getBottomButtons());
	}
	
	/**
	 * Action method for the save button
	 */
	public void saveButtonClicked(){
		if(currentAttributePanel.stopEditingWithSave()){
			inEditingMode=false;
			uiController.remove(uiController.find(mainPanel,"buttonPanel"));
			uiController.add(mainPanel,getBottomButtons());
		}
	}
	
	/**
	 * Action method for the cancel button
	 */
	public void cancelButtonClicked(){
		inEditingMode=false;
		currentAttributePanel.stopEditingWithoutSave();
		uiController.remove(uiController.find(mainPanel,"buttonPanel"));
		uiController.add(mainPanel,getBottomButtons());
	}

	public void showCHWDashboard(){
		CommunityHealthWorkerDashboard chwDashboard = new CommunityHealthWorkerDashboard(uiController,appCon,currentCHW);
		chwDashboard.expandDashboard();
	}
	
	public void viewWillDisappear() {/* do nothing */}


}
