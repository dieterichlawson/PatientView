package net.frontlinesms.plugins.patientview.ui.detailview;

import java.util.HashMap;

import net.frontlinesms.plugins.patientview.data.domain.framework.DataType;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import thinlet.Thinlet;

public class FormDetailViewPanelController implements DetailViewPanelController<MedicForm>, ThinletUiEventHandler {

	private UiGeneratorController uiController;
	private Object mainPanel;
	
	private static final String FORM_PANEL = "/ui/plugins/patientview/AtAGlance/form_AAG.xml";
	
	public FormDetailViewPanelController(UiGeneratorController uiController){
		this.uiController = uiController;
		mainPanel = uiController.loadComponentFromFile(FORM_PANEL, this);
		uiController.remove(uiController.find(mainPanel,"submitterLabel"));
		uiController.remove(uiController.find(mainPanel,"subjectLabel"));
		uiController.remove(uiController.find(mainPanel,"dateSubmittedLabel"));
	}
	/**
	 * @see net.frontlinesms.plugins.patientview.ui.detailview.DetailViewPanelController#getEntityClass()
	 */
	public Class getEntityClass() {
		return MedicForm.class;
	}

	/**
	 * @see net.frontlinesms.plugins.patientview.ui.detailview.DetailViewPanelController#getFurtherOptions()
	 */
	public HashMap<String, String> getFurtherOptions() {
		return null;
	}

	/**
	 * @see net.frontlinesms.plugins.patientview.ui.detailview.DetailViewPanelController#getPanel()
	 */
	public Object getPanel() {
		return mainPanel;
	}

	/**
	 * Populates the main panel with a picture of the form. 
	 * @see net.frontlinesms.plugins.patientview.ui.detailview.DetailViewPanelController#viewWillAppear(java.lang.Object)
	 */
	public void viewWillAppear(MedicForm form) {
		uiController.setText(uiController.find(mainPanel,"nameLabel"), form.getName());
		Object fieldContainer = uiController.find(mainPanel,"fieldPanel");
		uiController.removeAll(fieldContainer);
		for(MedicFormField ff: form.getFields()){
			Object field = null;
			if(ff.getDatatype() == DataType.CHECK_BOX){
				field =uiController.createCheckbox(null, ff.getLabel(), false);
				uiController.add(fieldContainer,field);
				uiController.setEnabled(field,false);
				uiController.setInteger(field, "weightx", 1);
				uiController.setChoice(field, "halign", "fill");
			}else if(ff.getDatatype() == DataType.TEXT_AREA){
				field = Thinlet.create("textarea");
				Object field2 = uiController.createLabel(ff.getLabel());
				uiController.add(fieldContainer,field2);
				uiController.add(fieldContainer,field);
				uiController.setEditable(field,false);
				uiController.setInteger(field, "weightx", 1);
				uiController.setChoice(field, "halign", "fill");
				uiController.setChoice(field2, "halign","left");
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
				uiController.setEditable(field,false);
				uiController.setInteger(field, "weightx", 1);
				uiController.setChoice(field, "halign", "fill");
				uiController.setChoice(field2, "halign", "center");
			}
		}
	}

	public void viewWillDisappear() {/*do nothing*/}

}
