package net.frontlinesms.plugins.patientview.ui.detailview;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.framework.DataType;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormFieldResponse;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormFieldDao;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormFieldResponseDao;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormResponseDao;
import net.frontlinesms.plugins.patientview.utils.DateUtils;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class FormResponseDetailViewPanelController implements DetailViewPanelController<MedicFormResponse>, ThinletUiEventHandler {

	private UiGeneratorController uiController;
	private ApplicationContext appCon;
	private Object mainPanel;
	
	private static final String FORM_RESPONSE_PANEL = "/ui/plugins/patientview/AtAGlance/form_AAG.xml";
	//i18n
	private static final String FORM = "medic.common.form";
	private static final String SUBJECT = "medic.common.labels.subject";
	private static final String SUBMITTER = "medic.common.labels.submitter";
	private static final String DATE_SUBMITTED = "medic.common.labels.date.submitted";
	
	public FormResponseDetailViewPanelController(UiGeneratorController uiController, ApplicationContext appCon){
		this.uiController = uiController;
		this.appCon = appCon;
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
		try{
			response.getResponses();
			response.getForm().getName();
			response.getSubject().getName();
			response.getDateSubmitted();
		}catch(Exception e){
			((MedicFormResponseDao) appCon.getBean("MedicFormResponseDao")).reattach(response);
		}
		String form = getI18NString(FORM) + ": " + response.getForm().getName();
		String submitter = getI18NString(SUBMITTER) + ": " + response.getSubmitter().getName();
		String subject = getI18NString(SUBJECT) + ": " + response.getSubject().getName();
		DateFormat df = DateUtils.getDateFormatter();
		String date = getI18NString(DATE_SUBMITTED) + " " + df.format(response.getDateSubmitted());
		uiController.setText(uiController.find(mainPanel,"nameLabel"),  form);
		uiController.setText(uiController.find(mainPanel,"submitterLabel"),  submitter);
		uiController.setText(uiController.find(mainPanel,"dateSubmittedLabel"),  date);
		uiController.setText(uiController.find(mainPanel,"subjectLabel"), subject);
		Object fieldContainer = uiController.find(mainPanel,"fieldPanel");
		uiController.removeAll(fieldContainer);
		ArrayList<String> responses = new ArrayList<String>();
		List<MedicFormFieldResponse> fieldResponses = ((MedicFormFieldResponseDao) appCon.getBean("MedicFormFieldResponseDao")).getResponsesForForm(response);
		for(MedicFormFieldResponse r: fieldResponses){
			responses.add(r.getValue());
		}
		Iterator<String> responseIt = responses.iterator();
		List<MedicFormField> fields = ((MedicFormFieldDao) appCon.getBean("MedicFormFieldDao")).getFieldsOnForm(response.getForm());
		for(MedicFormField ff: fields){
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
