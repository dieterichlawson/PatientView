package net.frontlinesms.plugins.patientview.ui.detailview;

import java.text.DateFormat;
import java.util.HashMap;

import net.frontlinesms.plugins.patientview.data.domain.response.MedicMessageResponse;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

public class MessageResponseDetailViewPanelController implements
		DetailViewPanelController<MedicMessageResponse>, ThinletUiEventHandler {

	private UiGeneratorController uiController;
	private Object mainPanel;
	
	private static final String MESSAGE_RESPONSE_PANEL = "/ui/plugins/patientview/AtAGlance/form_AAG.xml";
	//i18n
	private static final String SUBMITTED_BY = "detailview.labels.submitted.by";
	private static final String ON = "detailview.labels.on";
	
	public MessageResponseDetailViewPanelController(UiGeneratorController uiController){
		this.uiController = uiController;
		mainPanel = uiController.loadComponentFromFile(MESSAGE_RESPONSE_PANEL, this);
	}
	
	public Class<MedicMessageResponse> getEntityClass() {
		return MedicMessageResponse.class;
	}

	public HashMap<String, String> getFurtherOptions() {
		return null;
	}

	public Object getPanel() {
		return mainPanel;
	}

	public void viewWillAppear(MedicMessageResponse message) {
		uiController.removeAll(mainPanel);
		DateFormat df = InternationalisationUtils.getDateFormat();
		Object submitterLabel = uiController.createLabel(InternationalisationUtils.getI18NString(SUBMITTED_BY)+" "+ message.getSubmitter().getName());
		Object dateLabel = uiController.createLabel(InternationalisationUtils.getI18NString(ON)+" " +  df.format(message.getDateSubmitted()));
		Object textarea = uiController.create("textarea");
		uiController.setText(textarea, message.getMessageContent());
		uiController.setEditable (textarea,false);
		uiController.setInteger(textarea, "weightx", 1);
		uiController.setInteger(submitterLabel,"weightx",1);
		uiController.setInteger(dateLabel,"weightx",1);
		uiController.setChoice(submitterLabel,"halign","center");
		uiController.setChoice(dateLabel,"halign","center");
		uiController.add(mainPanel,submitterLabel);
		uiController.add(mainPanel,dateLabel);
		uiController.add(mainPanel,textarea);
	}

	public void viewWillDisappear() {/* do nothing*/}

}
