package net.frontlinesms.plugins.patientview.ui.administration.tabs;

import java.awt.Color;
import java.util.List;

import net.frontlinesms.data.events.DatabaseEntityNotification;
import net.frontlinesms.data.events.EntityDeletedNotification;
import net.frontlinesms.data.events.EntitySavedNotification;
import net.frontlinesms.events.EventBus;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.events.FrontlineEventNotification;
import net.frontlinesms.plugins.patientview.data.domain.flag.FlagDefinition;
import net.frontlinesms.plugins.patientview.data.repository.FlagDefinitionDao;
import net.frontlinesms.plugins.patientview.flag.FlagConditionValidator;
import net.frontlinesms.plugins.patientview.ui.administration.AdministrationTabPanel;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.hibernate.classic.ValidationFailure;
import org.springframework.context.ApplicationContext;

import thinlet.Thinlet;

public class FlagAdministrationPanelController implements AdministrationTabPanel, EventObserver, ThinletUiEventHandler{

	private static final String UI_FILE = "/ui/plugins/patientview/administration/flagAdministrationPanel.xml";
	
	private UiGeneratorController uiController;
	private FlagDefinitionDao flagDefinitionDao;
	private FlagConditionValidator flagValidator;
	private Object mainPanel;
	
	
	//text fields
	private Object nameField;
	private Object descriptionField;
	private Object conditionArea;
	private Object conditionMessageLabel;
	
	//lists
	private Object flagList;
	
	public FlagAdministrationPanelController(UiGeneratorController uiController, ApplicationContext appCon){
		this.uiController = uiController;
		((EventBus) appCon.getBean("eventBus")).registerObserver(this);
		this.flagDefinitionDao = (FlagDefinitionDao) appCon.getBean("FlagDefinitionDao");
		this.mainPanel = uiController.loadComponentFromFile(UI_FILE,this);
		this.flagList = uiController.find(mainPanel,"flagList");
		this.nameField = uiController.find(mainPanel,"nameField");
		this.descriptionField = uiController.find(mainPanel,"descriptionField");
		this.conditionArea = uiController.find(mainPanel, "conditionArea");
		this.conditionMessageLabel = uiController.find(mainPanel,"conditionMessageLabel");
		this.flagValidator = new FlagConditionValidator(appCon);
		updateFlagList();
	}
	
	public String getIconPath() {
		return "/icons/flag_red.png";
	}

	public String getListItemTitle() {
		return InternationalisationUtils.getI18NString("medic.flags.admin.tab.title");
	}

	public Object getPanel() {
		return mainPanel;
	}
	
	public void addFlag(){
		FlagDefinition fd = new FlagDefinition("<New Flag>", "", "");
		flagDefinitionDao.saveFlagDefinition(fd);
		uiController.setFocus(nameField);
	}
	
	public void removeFlag(){
		FlagDefinition fd = (FlagDefinition) uiController.getAttachedObject(uiController.getSelectedItem(flagList));
		if(fd != null){
			flagDefinitionDao.deleteFlagDefinition(fd);
			clearFields();
			flagListSelectionChanged();
		}
	}
	
	private void clearFields(){
		uiController.setText(nameField, "");
		uiController.setText(descriptionField, "");
		uiController.setText(conditionArea, "");
	}
	
	public void flagListSelectionChanged(){
		flagListSelectionChanged(uiController.getSelectedIndex(flagList));
	}
	public void flagListSelectionChanged(int selectedIndex){
		FlagDefinition df = uiController.getAttachedObject(uiController.getItem(flagList, selectedIndex), FlagDefinition.class);
		if(df != null){
			uiController.setEnabledRecursively(uiController.find(mainPanel, "flagSettingsPanel"), true);
			uiController.setText(nameField, df.getName());
			uiController.setText(descriptionField, df.getShortDescription());
			uiController.setText(conditionArea, df.getFlagCondition());
		}else{
			uiController.setEnabledRecursively(uiController.find(mainPanel, "flagSettingsPanel"), false);
		}
	}
	
	private void updateFlagList(){
		List<FlagDefinition> flags = flagDefinitionDao.getAllFlagDefinitions();
		uiController.removeAll(flagList);
		for(FlagDefinition fd: flags){
			Object listItem = uiController.createListItem(fd.getName(), fd);
			uiController.setString(listItem, "tooltip", fd.getShortDescription());
			uiController.setIcon(listItem, fd.getIconPath());
			uiController.add(flagList,listItem);
		}
		uiController.setSelectedIndex(flagList, 0);
		flagListSelectionChanged(0);
	}

	public void notify(FrontlineEventNotification notification) {
		if(notification instanceof EntityDeletedNotification || notification instanceof EntitySavedNotification){
			if(((DatabaseEntityNotification<?>) notification).getDatabaseEntity() instanceof FlagDefinition){
				updateFlagList();
			}
		}
	}
	
	//thinlet change methods
	public void nameChanged(String text){
		getCurrentlySelectedFlagDefinition().setName(text);
		uiController.setText(uiController.getSelectedItem(flagList), text);
		flagDefinitionDao.updateFlagDefinition(getCurrentlySelectedFlagDefinition());
	}
	
	public void descriptionChanged(String text){
		getCurrentlySelectedFlagDefinition().setShortDescription(text);
		flagDefinitionDao.updateFlagDefinition(getCurrentlySelectedFlagDefinition());
	}
	
	public void conditionChanged(String text){
		try{
			flagValidator.validate(text);
		}catch(ValidationFailure e){
			uiController.setColor(conditionMessageLabel, Thinlet.FOREGROUND, new Color(184,0,0));
			uiController.setIcon(conditionMessageLabel, "/icons/cross.png");
			uiController.setText(conditionMessageLabel,e.getMessage());
			return;
		}
		uiController.setColor(conditionMessageLabel, Thinlet.FOREGROUND, new Color(0,168,0));
		uiController.setIcon(conditionMessageLabel, "/icons/tick.png");
		uiController.setText(conditionMessageLabel,"Condition is valid");
		getCurrentlySelectedFlagDefinition().setFlagCondition(text);
		flagDefinitionDao.updateFlagDefinition(getCurrentlySelectedFlagDefinition());
	}
	
	private FlagDefinition getCurrentlySelectedFlagDefinition(){
		return uiController.getAttachedObject(uiController.getSelectedItem(flagList),FlagDefinition.class);
	}

}
