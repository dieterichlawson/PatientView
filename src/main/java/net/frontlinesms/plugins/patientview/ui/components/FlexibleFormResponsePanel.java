package net.frontlinesms.plugins.patientview.ui.components;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.framework.DataType;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormFieldResponse;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormFieldDao;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormFieldResponseDao;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.springframework.context.ApplicationContext;

public class FlexibleFormResponsePanel implements ThinletUiEventHandler{
	
	private Object mainPanel;
	private Object formPanel;
	private Object hideButton;
	private Object showAllButton;
	private Object showIdButton;
	
	public enum State{
		HIDDEN,
		ALL_VISIBLE,
		ID_FIELDS_VISIBLE;
	}
	private State state;
	
	private UiGeneratorController uiController;
	private ApplicationContext appCon;
	private static final String UI_FILE= "/ui/plugins/patientview/components/flexibleFormResponsePanel.xml";
	
	private MedicFormResponse response;
	public FlexibleFormResponsePanel(UiGeneratorController uiController, ApplicationContext appCon, MedicFormResponse response){
		this.uiController = uiController;
		this.appCon = appCon;
		this.response= response;
		init();
	}
	
	private void init(){
		mainPanel = uiController.loadComponentFromFile(UI_FILE,this);
		hideButton = uiController.find(mainPanel,"hideButton");
		showAllButton = uiController.find(mainPanel,"showAllButton");
		showIdButton = uiController.find(mainPanel,"showIdButton");
		formPanel = uiController.find(mainPanel,"formPanel");
		uiController.setText(uiController.find(mainPanel,"formNameLabel"), response.getFormName());
		showId();
	}
	
	public void hide(){
		uiController.setVisible(hideButton,false);
		uiController.setVisible(showAllButton,true);
		uiController.setVisible(showIdButton,true);
		uiController.remove(formPanel);
		state= State.HIDDEN;
	}
	
	public void showAll(){
		if(state == State.HIDDEN){
			uiController.add(mainPanel,formPanel);
		}
		uiController.setVisible(hideButton,true);
		uiController.setVisible(showAllButton,false);
		uiController.setVisible(showIdButton,true);
		state = State.ALL_VISIBLE;
		showForm();
	}
	public void showId(){
		if(state == State.HIDDEN){
			uiController.add(mainPanel,formPanel);
		}
		uiController.setVisible(hideButton,true);
		uiController.setVisible(showAllButton,true);
		uiController.setVisible(showIdButton,false);
		state = State.ID_FIELDS_VISIBLE;
		showForm();
	}
	
	public void showForm() {
		uiController.removeAll(formPanel);
		ArrayList<String> responses = new ArrayList<String>();
		List<MedicFormFieldResponse> fieldResponses = ((MedicFormFieldResponseDao) appCon.getBean("MedicFormFieldResponseDao")).getResponsesForForm(response);
		for(MedicFormFieldResponse r: fieldResponses){
			responses.add(r.getValue());
		}
		Iterator<String> responseIt = responses.iterator();
		List<MedicFormField> fields = ((MedicFormFieldDao) appCon.getBean("MedicFormFieldDao")).getFieldsOnForm(response.getForm());
		for(MedicFormField ff: fields){
			if(!(ff.getMapping()==null && state == State.ID_FIELDS_VISIBLE)){
				Object field = null;
				if(ff.getDatatype() == DataType.CHECK_BOX ||
				   ff.getDatatype() == DataType.TRUEFALSE ||
				   ff.getDatatype() == DataType.POSITIVENEGATIVE ||
				   ff.getDatatype() == DataType.YESNO){
					field =uiController.createCheckbox(null, ff.getLabel(), false);
					uiController.add(formPanel,field);
					uiController.setEnabled(field, false);
					uiController.setInteger(field, "weightx", 1);
					uiController.setChoice(field, "halign", "fill");
					String r = responseIt.next();
					if(r.equalsIgnoreCase(InternationalisationUtils.getI18NString("datatype.true"))){
						uiController.setSelected(field, true);
					}
				}else if(ff.getDatatype() == DataType.TEXT_AREA){
					field = uiController.create("textarea");
					Object field2 = uiController.createLabel(ff.getLabel());
					uiController.add(formPanel,field2);
					uiController.add(formPanel,field);
					uiController.setEditable(field, false);
					uiController.setInteger(field, "weightx", 1);
					uiController.setChoice(field, "halign", "fill");
					uiController.setChoice(field2, "halign","left");
					uiController.setText(field,responseIt.next());
				}else if(ff.getDatatype() == DataType.TRUNCATED_TEXT ||
						ff.getDatatype() == DataType.WRAPPED_TEXT){
					field = uiController.createLabel(ff.getLabel());
					uiController.add(formPanel,field);
					uiController.setChoice(field, "halign", "center");
				}else{
					field = uiController.createTextfield(null, "");
					Object field2 = uiController.createLabel(ff.getLabel());
					uiController.add(formPanel,field2);
					uiController.add(formPanel,field);
					uiController.setEditable(field, false);
					uiController.setInteger(field, "weightx", 1);
					uiController.setChoice(field, "halign", "fill");
					uiController.setChoice(field2, "halign", "center");
					uiController.setText(field, responseIt.next());
				}
			}
		}
	}
	
	public Object getMainPanel(){
		return mainPanel;
	}
}
