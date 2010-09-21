package net.frontlinesms.plugins.patientview.ui.dashboard.tabs;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;
import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicMessageResponse;
import net.frontlinesms.plugins.patientview.search.impl.SmsMessageResultSet;
import net.frontlinesms.plugins.patientview.ui.advancedtable.AdvancedTableActionDelegate;
import net.frontlinesms.plugins.patientview.ui.advancedtable.HeaderColumn;
import net.frontlinesms.plugins.patientview.ui.advancedtable.PagedAdvancedTableController;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.CheckBox;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.DateField;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.FormFieldDelegate;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.TextField;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.ThinletFormField;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.UiGeneratorControllerConstants;
import net.frontlinesms.ui.handler.message.MessagePanelHandler;

import org.springframework.context.ApplicationContext;
public class SmsMessagesTab extends TabController implements AdvancedTableActionDelegate, FormFieldDelegate, ThinletUiEventHandler{

	private UiGeneratorController uiController;
	private SmsMessageResultSet resultSet;
	private PagedAdvancedTableController tableController;
	private MessagePanelHandler messagePanelHandler;
	
	private static final String UI_FILE = "/ui/plugins/patientview/dashboard/tabs/smsMessageTab.xml";
	
	public SmsMessagesTab(UiGeneratorController uiController, ApplicationContext appCon, CommunityHealthWorker chw) {
		super(uiController, appCon);
		uiController.add(super.getMainPanel(),uiController.loadComponentFromFile(UI_FILE,this));
		//create the message panel
		messagePanelHandler = MessagePanelHandler.create(uiController, false, true, 1);
		messagePanelHandler.setShouldClearRecipientField(false);
		uiController.setText(uiController.find(messagePanelHandler.getPanel(), UiGeneratorControllerConstants.COMPONENT_TF_RECIPIENT),chw.getPhoneNumber());
		uiController.add(uiController.find(getMainPanel(),"topPanel"),messagePanelHandler.getPanel());
		
		Object controlPanel = uiController.find(getMainPanel(),"controlPanel");
		//create the thinlet form fields that make up the control panel
		DateField dateField = new DateField(uiController,getI18NString("medic.common.labels.date.submitted"),this);
		dateField.setLabelIcon("/icons/date.png");
		TextField textField = new TextField(uiController,getI18NString("medic.common.labels.message"),this);
		textField.setLabelIcon("/icons/message.png");
		CheckBox toCheckBox = new CheckBox(uiController, getI18NString("medic.common.sent.to"), this);
		toCheckBox.setLabelIcon("/icons/sms_send.png");
		toCheckBox.setRawResponse(true);
		uiController.setWeight(toCheckBox.getThinletPanel(), 0, 0);
		CheckBox fromCheckBox = new CheckBox(uiController, getI18NString("medic.common.received.from"), this);
		fromCheckBox.setLabelIcon("/icons/sms_receive.png");
		fromCheckBox.setRawResponse(true);
		uiController.setWeight(fromCheckBox.getThinletPanel(), 0, 0);
		//add form fields
		uiController.add(controlPanel,dateField.getThinletPanel());
		uiController.add(controlPanel,uiController.createLabel("   "));
		uiController.add(controlPanel,textField.getThinletPanel());
		uiController.add(controlPanel,uiController.createLabel("   "));
		uiController.add(controlPanel,toCheckBox.getThinletPanel());
		uiController.add(controlPanel,uiController.createLabel("   "));
		uiController.add(controlPanel,fromCheckBox.getThinletPanel());
		//create the table
		tableController = new PagedAdvancedTableController(this, uiController, null);
		tableController.putHeader(MedicMessageResponse.class, HeaderColumn.createColumnList(new String[]{getI18NString("common.status"),getI18NString("medic.common.labels.date"), getI18NString("medic.common.labels.sender"),getI18NString("medic.common.labels.recipient"),getI18NString("medic.common.labels.message")}, 
				new String[]{"/icons/status.png","", "/icons/user_sender.png","/icons/user_receiver.png","/icons/description.png"},
				new String[]{"getStatus","getStringDateSubmitted","getSenderMsisdn","getRecipientMsisdn","getMessageContent"}));
		tableController.setNoResultsMessage(getI18NString("medic.sms.messages.tab.no.search.results") +" "+ chw.getName());
		tableController.setPagingControlBorder(false);
		tableController.enableRefreshButton(appCon);
		uiController.add(uiController.find(getMainPanel(),"tablePanel"),tableController.getMainPanel());
		//set up the result set
		resultSet = new SmsMessageResultSet(appCon);
		resultSet.setSenderNumber(chw.getPhoneNumber());
		tableController.setResultsSet(resultSet);
		tableController.updateTable();
		
		super.setTitle(getI18NString("medic.common.text.messages"));
		super.setIconPath("/icons/big_history.png");
	}

	public void doubleClickAction(Object selectedObject) {}

	public void resultsChanged() {}

	public void selectionChanged(Object selectedObject) {}

	public void formFieldChanged(ThinletFormField changedField, String newValue) {
		if(changedField instanceof DateField){
			resultSet.setAroundDate(((DateField) changedField).getRawResponse());
		}else if(changedField instanceof TextField){
			resultSet.setContentSearchString(newValue);
		}else if(changedField.getLabel().equalsIgnoreCase(getI18NString("medic.common.sent.to"))){
			resultSet.setSearchingTo(((CheckBox) changedField).getRawResponse());
		}else{
			resultSet.setSearchingFrom(((CheckBox) changedField).getRawResponse());
		}
		tableController.updateTable();
	}
}