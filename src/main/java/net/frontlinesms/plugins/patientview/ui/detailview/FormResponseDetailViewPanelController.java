package net.frontlinesms.plugins.patientview.ui.detailview;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.frontlinesms.plugins.patientview.data.domain.framework.DataType;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormFieldResponse;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import static net.frontlinesms.ui.i18n.InternationalisationUtils.*;

public class FormResponseDetailViewPanelController implements
		DetailViewPanelController<MedicFormResponse>, ThinletUiEventHandler {

	private UiGeneratorController uiController;
	private Object mainPanel;
	
	private static final String FORM_RESPONSE_PANEL = "/ui/plugins/patientview/AtAGlance/form_AAG.xml";
	//i18n
	private static final String FORM = "medic.common.form";
	private static final String SUBJECT = "medic.common.labels.subject";
	private static final String SUBMITTER = "medic.common.labels.submitter";
	private static final String DATE_SUBMITTED = "medic.common.labels.date.submitted";
	
	public FormResponseDetailViewPanelController(UiGeneratorController uiController){
		this.uiController = uiController;
		mainPanel = uiController.loadComponentFromFile(FORM_RESPONSE_PANEL, this);
	}
	
	public Class getEntityClass() {
		return MedicFormResponse.class;
	}

	public HashMap<String, String> getFurtherOptions() {
		return null;
	}

	public Object getPanel() {
		return mainPanel;
	}

	public void viewWillAppear(MedicFormResponse response) {
		//set up the label meta-data
		String form = getI18NString(FORM) + ": " + response.getForm().getName();
		String submitter = getI18NString(SUBMITTER) + ": " + response.getSubmitter().getName();
		String subject = getI18NString(SUBJECT) + ": " + response.getSubject().getName();
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
		String date = getI18NString(DATE_SUBMITTED) + " " + df.format(response.getDateSubmitted());
		uiController.setText(uiController.find(mainPanel,"nameLabel"),  form);
		uiController.setText(uiController.find(mainPanel,"submitterLabel"),  submitter);
		uiController.setText(uiController.find(mainPanel,"dateSubmittedLabel"),  date);
		uiController.setText(uiController.find(mainPanel,"subjectLabel"), subject);
		Object fieldContainer = uiController.find(mainPanel,"fieldPanel");
		uiController.removeAll(fieldContainer);
		ArrayList<String> responses = new ArrayList<String>();
		for(MedicFormFieldResponse r: response.getResponses()){
			responses.add(r.getValue());
		}
		Iterator<String> responseIt = responses.iterator();
		for(MedicFormField ff: response.getForm().getFields()){
			Object field = null;
			if(ff.getDatatype() == DataType.CHECK_BOX){
				field =uiController.createCheckbox(null, ff.getLabel(), false);
				uiController.add(fieldContainer,field);
				uiController.setEnabled(field, false);
				uiController.setInteger(field, "weightx", 1);
				uiController.setChoice(field, "halign", "fill");
				String r = responseIt.next();
				if(r.equals("true")){
					uiController.setSelected(field, true);
				}
			}else if(ff.getDatatype() == DataType.TEXT_AREA){
				field = uiController.create("textarea");
				Object field2 = uiController.createLabel(ff.getLabel());
				uiController.add(fieldContainer,field2);
				uiController.add(fieldContainer,field);
				uiController.setEditable(field, false);
				uiController.setInteger(field, "weightx", 1);
				uiController.setChoice(field, "halign", "fill");
				uiController.setChoice(field2, "halign","left");
				uiController.setText(field,responseIt.next());
			}else if(ff.getDatatype() == DataType.TRUNCATED_TEXT ||
					ff.getDatatype() == DataType.WRAPPED_TEXT){
				field = uiController.createLabel(ff.getLabel());
				uiController.add(fieldContainer,field);
				uiController.setChoice(field, "halign", "center");
			}else{
				field = uiController.createTextfield(null, "");
				Object field2 = uiController.createLabel(ff.getLabel());
				uiController.add(fieldContainer,field2);
				uiController.add(fieldContainer,field);
				uiController.setEditable(field, false);
				uiController.setInteger(field, "weightx", 1);
				uiController.setChoice(field, "halign", "fill");
				uiController.setChoice(field2, "halign", "center");
				uiController.setText(field, responseIt.next());
			}
		}
	}

	public void viewWillDisappear() {/* do nothing*/}

}
