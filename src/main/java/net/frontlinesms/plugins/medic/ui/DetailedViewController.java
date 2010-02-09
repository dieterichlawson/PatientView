package net.frontlinesms.plugins.medic.ui;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import net.frontlinesms.plugins.medic.data.domain.framework.DataType;
import net.frontlinesms.plugins.medic.data.domain.framework.MedicField;
import net.frontlinesms.plugins.medic.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.medic.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.medic.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.medic.data.domain.people.Patient;
import net.frontlinesms.plugins.medic.data.domain.people.Person;
import net.frontlinesms.plugins.medic.data.domain.people.User.Role;
import net.frontlinesms.plugins.medic.data.domain.response.MedicFieldResponse;
import net.frontlinesms.plugins.medic.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.medic.data.domain.response.MedicMessageResponse;
import net.frontlinesms.plugins.medic.data.domain.response.Response;
import net.frontlinesms.plugins.medic.data.repository.CommunityHealthWorkerDao;
import net.frontlinesms.plugins.medic.data.repository.PatientDao;
import net.frontlinesms.plugins.medic.data.repository.hibernate.HibernateMedicFieldDao;
import net.frontlinesms.plugins.medic.data.repository.hibernate.HibernateMedicFieldResponseDao;
import net.frontlinesms.plugins.medic.history.HistoryManager;
import net.frontlinesms.plugins.medic.ui.ChartGenerator.CalcType;
import net.frontlinesms.plugins.medic.ui.ChartGenerator.ChartType;
import net.frontlinesms.plugins.medic.ui.ChartGenerator.TimeSpan;
import net.frontlinesms.plugins.medic.ui.dialogs.DetailViewEditorController;
import net.frontlinesms.plugins.medic.ui.dialogs.SubmitFormDialog;
import net.frontlinesms.plugins.medic.ui.expandeddetailview.PersonExpandedDetailView;
import net.frontlinesms.plugins.medic.ui.helpers.thinletformfields.ButtonGroup;
import net.frontlinesms.plugins.medic.ui.helpers.thinletformfields.CheckBox;
import net.frontlinesms.plugins.medic.ui.helpers.thinletformfields.DateField;
import net.frontlinesms.plugins.medic.ui.helpers.thinletformfields.NumericTextField;
import net.frontlinesms.plugins.medic.ui.helpers.thinletformfields.PasswordTextField;
import net.frontlinesms.plugins.medic.ui.helpers.thinletformfields.PhoneNumberField;
import net.frontlinesms.plugins.medic.ui.helpers.thinletformfields.TextArea;
import net.frontlinesms.plugins.medic.ui.helpers.thinletformfields.TextBox;
import net.frontlinesms.plugins.medic.ui.helpers.thinletformfields.ThinletFormField;
import net.frontlinesms.plugins.medic.ui.helpers.thinletformfields.TimeField;
import net.frontlinesms.plugins.medic.userlogin.UserSessionManager;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.springframework.context.ApplicationContext;

import thinlet.Thinlet;

public class DetailedViewController implements ThinletUiEventHandler{

	private UiGeneratorController uiController;
	private static final String UI_FILE_FORM_AAG = "/ui/plugins/medic/AtAGlance/form_AAG.xml";
	private static String UI_FILE_PERSON_AAG_PANEL = "/ui/plugins/medic/AtAGlance/person_AAG.xml"; 
	private static String CHART_OPTIONS = "/ui/plugins/medic/AtAGlance/chart_options.xml"; 
	

	/**the at a glance panel for Forms**/
	private Object formPanel;
	
	/**the panel that holds all at-a-glance panels **/
	private Object detailPanel;
	/**further options panel**/
	private Object furtherOptionsPanel;
	
	/**the currently selected entity**/
	private Object currentEntity;
	
	PersonPanel currentPersonPanel;
	/**the list of objects for filling out patient data**/
	private ArrayList<ThinletFormField> dvResponseObjects;
	/** the application context for getting DAOS**/
	private ApplicationContext appContext;
	/** the parent controller **/
	private MedicThinletTabController parent;
	
	private HibernateMedicFieldResponseDao fieldResponseDao;
	private HibernateMedicFieldDao fieldDao;
	private CommunityHealthWorkerDao chwDao;
	private PatientDao patientDao;
	
	private boolean inEditingMode;
	private HashMap<Class,String[][]> furtherOptions;
	
	//i18n
	private static final String PATIENT_AAG ="detailview.patient.at.a.glance";
	private static final String CHW_AAG ="detailview.chw.at.a.glance";
	private static final String BLANK_MESSAGE ="detailview.blank.message";
	private static final String EDIT_PATIENT_DATA_BUTTON = "personpanel.labels.edit.patient.data";
	private static final String EDIT_CHW_DATA_BUTTON = "personpanel.labels.edit.chw.data";
	private static final String SEE_MORE_BUTTON = "detailview.buttons.see.more";
	private static final String GO_BACK_BUTTON = "patientrecord.buttons.go.back";
	private static final String SUBMIT_FORM_PATIENT ="detailview.buttons.submit.form.patient";
	private static final String EDIT_VIEW_BUTTON="detailview.buttons.edit.view";
	private static final String FILL_OUT_FORM_BUTTON = "detailview.buttons.fill.out.form";
	private static final String PHONE_NUMBER_FIELD = "medic.common.labels.phone.number";
	private static final String CHW_FIELD = "medic.common.chw";
	private static final String SAVE_PATIENT_DATA_BUTTON = "detailview.buttons.save.patient.data";
	private static final String SAVE_CHW_DATA_BUTTON = "detailview.buttons.save.chw.data";
	private static final String TRUE = "datatype.true";
	private static final String FALSE = "datatype.false";
	private static final String POSITIVE = "datatype.positive";
	private static final String NEGATIVE = "datatype.negative";
	private static final String YES = "datatype.yes";
	private static final String NO = "datatype.no";
	private static final String FORM = "medic.common.form";
	private static final String SUBJECT = "medic.common.labels.subject";
	private static final String SUBMITTER = "medic.common.labels.submitter";
	private static final String DATE_SUBMITTED = "medic.common.labels.date.submitted";
	private static final String SUBMITTED_BY = "detailview.labels.submitted.by";
	private static final String ON = "detailview.labels.on";
	
	
	public DetailedViewController(UiGeneratorController uiController, ApplicationContext appContext, MedicThinletTabController controller){
		//set local variables
		this.uiController = uiController;
		this.appContext = appContext;
		this.parent = controller;
		inEditingMode=false;
		//load views from files
		formPanel = uiController.loadComponentFromFile(UI_FILE_FORM_AAG, this);
		//cache thinlet components
		detailPanel = uiController.find(controller.getTab(),"detailPanelMedic");
		furtherOptionsPanel = uiController.find(controller.getTab(),"furtherOptionsPanel");
		//initialize the daos
		fieldResponseDao = (HibernateMedicFieldResponseDao) appContext.getBean("MedicFieldResponseDao");
		fieldDao = (HibernateMedicFieldDao) appContext.getBean("MedicFieldDao");
		chwDao = (CommunityHealthWorkerDao) appContext.getBean("CHWDao");
		patientDao = (PatientDao) appContext.getBean("PatientDao");
		
		//intialize further options mapping
		furtherOptions = new HashMap<Class,String[][]>();
		String[][] patientOptions = new String[][]{{InternationalisationUtils.getI18NString(SUBMIT_FORM_PATIENT), "submitFormForPatient()"},
												   {InternationalisationUtils.getI18NString(EDIT_VIEW_BUTTON), "deletePatient()"}};
		String[][] chwOptions = new String[][]{{InternationalisationUtils.getI18NString(EDIT_VIEW_BUTTON), "deleteCHW()"}};
		String[][] formOptions = new String[][]{{InternationalisationUtils.getI18NString(FILL_OUT_FORM_BUTTON), "fillOutForm()"}};
		String[][] fieldOptions = new String[0][0];
		furtherOptions.put(Patient.class, patientOptions);
		furtherOptions.put(CommunityHealthWorker.class, chwOptions);
		furtherOptions.put(MedicForm.class, formOptions);
		furtherOptions.put(MedicFormField.class, fieldOptions);
		selectionChanged(null);
	}
	
	public void selectionChanged(Object entity){
		currentEntity = entity;
		inEditingMode=false;
		if(entity!=null)
			updateFurtherOptions();
		
		if(detailPanel == null){
			detailPanel = uiController.find("detailPanelMedic");
		}
		if(entity instanceof Patient){
			switchToPatientPanel((Patient) entity);
		}else if(entity instanceof CommunityHealthWorker){
			switchToCHWPanel((CommunityHealthWorker) entity);
		}else if(entity instanceof MedicForm){
			switchToFormPanel((MedicForm) entity);
		}else if(entity instanceof MedicField){
			switchToFieldPanel((MedicField) entity);
		}else if(entity instanceof Response){
			switchToResponseInfoPanel((Response) entity);
		}else if(entity == null){
			switchToBlankPanel();
		}
	}
	
	public MedicThinletTabController getParent(){
		return parent;
	}

	private void switchToBlankPanel() {
		uiController.removeAll(detailPanel);
		Object label = uiController.createLabel(InternationalisationUtils.getI18NString(BLANK_MESSAGE));
		uiController.setInteger(label, "weightx", 1);
		uiController.setInteger(label, "weighty", 1);
		uiController.setChoice(label, "halign", "center");
		uiController.setChoice(label, "valign", "center");
		uiController.add(detailPanel,label);
	}
	
	
	private void switchToFieldPanel(MedicField entity){
		uiController.removeAll(detailPanel);
		if(entity.getDatatype() == DataType.NUMBER || entity.getDatatype().isBoolean()){
			uiController.removeAll(detailPanel);
			Object chartOptionsPanel = uiController.loadComponentFromFile(CHART_OPTIONS, this);
			uiController.add(detailPanel,chartOptionsPanel);
			if(entity.getDatatype().isBoolean()){
				Object yesVsNo = uiController.createComboboxChoice("Yes vs. No", ChartType.YESVNO);
				Object yes = uiController.createComboboxChoice("# of Yes Answers", ChartType.YES);
				Object no = uiController.createComboboxChoice("# of No Answers", ChartType.NO);
				uiController.add(uiController.find(chartOptionsPanel,"chartTypeCombo"),yes);
				uiController.add(uiController.find(chartOptionsPanel,"chartTypeCombo"),no);
				uiController.add(uiController.find(chartOptionsPanel,"chartTypeCombo"),yesVsNo);
				uiController.setText(uiController.find(chartOptionsPanel,"chartTypeCombo"),"Yes vs. No");
				uiController.setSelectedIndex(uiController.find(chartOptionsPanel,"chartTypeCombo"),2);
			}else{
				Object numChoice = uiController.createComboboxChoice("Number", ChartType.NUMBER);
				uiController.add(uiController.find(chartOptionsPanel,"chartTypeCombo"),numChoice);
				uiController.setText(uiController.find(chartOptionsPanel,"chartTypeCombo"),"Number");
				uiController.setSelectedIndex(uiController.find(chartOptionsPanel,"chartTypeCombo"),0);
				uiController.setEnabled(uiController.find(chartOptionsPanel,"chartTypeCombo"), false);
			}
			
			Object avgCalc = uiController.createComboboxChoice("Average", CalcType.AVERAGE);
			Object cumCalc = uiController.createComboboxChoice("Cumulative", CalcType.CUMULATIVE);
			Object milliTime = uiController.createComboboxChoice("Millisecond", TimeSpan.MILLISECOND);
			Object dayTime = uiController.createComboboxChoice("Day", TimeSpan.DAY);
			Object monthTime = uiController.createComboboxChoice("Month", TimeSpan.MONTH);
			Object yearTime = uiController.createComboboxChoice("Year", TimeSpan.YEAR);
			
			uiController.add(uiController.find(chartOptionsPanel,"calcTypeCombo"),avgCalc);
			uiController.add(uiController.find(chartOptionsPanel,"calcTypeCombo"),cumCalc);
			uiController.add(uiController.find(chartOptionsPanel,"timeIntervalCombo"),milliTime);
			uiController.add(uiController.find(chartOptionsPanel,"timeIntervalCombo"),dayTime);
			uiController.add(uiController.find(chartOptionsPanel,"timeIntervalCombo"),monthTime);
			uiController.add(uiController.find(chartOptionsPanel,"timeIntervalCombo"),yearTime);
			
			uiController.setSelectedIndex(uiController.find(chartOptionsPanel,"calcTypeCombo"),0);
			uiController.setText(uiController.find(chartOptionsPanel,"calcTypeCombo"),"Average");
			uiController.setSelectedIndex(uiController.find(chartOptionsPanel,"timeIntervalCombo"),2);
			uiController.setText(uiController.find(chartOptionsPanel,"timeIntervalCombo"),"Month");
			uiController.setAction(uiController.find(chartOptionsPanel,"generateButton"), "generateChart()", null, this);
			
		}
	}
	
	public void generateChart(){
		ChartType chartType = (ChartType) uiController.getAttachedObject(uiController.getSelectedItem(uiController.find(detailPanel,"chartTypeCombo")));
		CalcType calcType = (CalcType) uiController.getAttachedObject(uiController.getSelectedItem(uiController.find(detailPanel,"calcTypeCombo")));
		TimeSpan tInt = (TimeSpan) uiController.getAttachedObject(uiController.getSelectedItem(uiController.find(detailPanel,"timeIntervalCombo")));
		generateChart((MedicField) currentEntity,chartType,tInt,calcType);
	}
	
	private void generateChart(MedicField entity,ChartType chartType, TimeSpan timeSpan, CalcType calcType){
		Collection<MedicFieldResponse> responses = fieldResponseDao.getResponsesForField(entity);
		
		//generate the labels
		String chartName = "";
		String xName ="";
		String yName = "";
		
		//title and y axis
		if(chartType == ChartType.NUMBER){
				chartName ="Value of field \"" + entity.getLabel() + "\"";
				yName = "Value";
		}else if (chartType == ChartType.YESVNO){
			chartName ="True Responses vs. False Responses for Field \"" + entity.getLabel() + "\"";
			yName = "Number of Responses";
		}else if(chartType == ChartType.YES){
			chartName ="True Responses for Field \"" + entity.getLabel() + "\"";
			yName = "Number of Responses";
		}else if(chartType == ChartType.NO){
			chartName ="False Responses for Field \"" + entity.getLabel() + "\"";
			yName = "Number of Responses";
		}
		
		//add the cumulative/average qualifiers
		if(calcType == CalcType.CUMULATIVE){
			yName += " (Cumulative)";
		}else{
			yName += " (Average)";
		}
		
		//x axis
		xName = "Time";
		if(timeSpan == TimeSpan.DAY){
			xName +=" (Day)";
		}else if(timeSpan == TimeSpan.MONTH){
			xName += " (Month)";
		}else if(timeSpan == TimeSpan.YEAR){
			xName += " (Year)";
		}
		
		
		JFreeChart chart = ChartFactory.createTimeSeriesChart(
		   chartName,
		   xName,
		   yName,
		   ChartGenerator.generateDataSet(responses, calcType, timeSpan, chartType),
		   true,
		   true,
		   false);
		BufferedImage chartImage = chart.createBufferedImage(500, 400);
		
		Object button = uiController.find(detailPanel,"chartButton");
		uiController.setIcon(button, chartImage);
	}

	public Object switchToPatientPanel(Patient p){
		//clear the panel
		uiController.removeAll(detailPanel);
		addPersonPanel(p,true);
		addPersonPanel(p.getChw(),false);
		//add all the details
		addPersonDetails(p);
		//add the buttons to the bottom that allow you to edit stuff
		Object panel = Thinlet.create("panel");
		uiController.setName(panel,"buttonPanel");
		uiController.setInteger(panel, "columns", 2);
		uiController.add(panel,getEditDataButton(p));
		uiController.add(panel,getEditViewButton());
		uiController.setInteger(panel, "weightx", 1);
		uiController.setInteger(panel, "weighty", 1);
		uiController.setChoice(panel, "valign", "bottom");
		uiController.add(detailPanel,panel);
		return detailPanel;
	}
	
	/**
	 * This adds the 'person panel' to the detail view which includes the picture 
	 * and the core data of the person
	 * @param p
	 */
	private void addPersonPanel(Person p, boolean makeCurrentPersonPanel){
		//add the proper panel
		PersonPanel personPanel= new PersonPanel(uiController,p,appContext);
		if(makeCurrentPersonPanel){
			currentPersonPanel = personPanel;
		}
		uiController.add(detailPanel,personPanel.getMainPanel());	
	}

	private void switchToCHWPanel(CommunityHealthWorker chw){
		//clear the panel
		uiController.removeAll(detailPanel);
		addPersonPanel(chw,true);
		//add all the details
		addPersonDetails(chw);
		//add the two buttons at the bottom
		Object panel = Thinlet.create("panel");
		uiController.setName(panel,"buttonPanel");
		uiController.setInteger(panel, "columns", 2);
		uiController.add(panel,getEditDataButton(chw));
		uiController.add(panel,getEditViewButton());
		uiController.setInteger(panel, "weightx", 1);
		uiController.setInteger(panel, "weighty", 1);
		uiController.setChoice(panel, "valign", "bottom");
		uiController.add(detailPanel,panel);
	}

	//FOR EXPANDED VIEW
	public Object getPersonPanel(Patient p, ThinletUiEventHandler handler){
		Object panel = Thinlet.create("panel");
		uiController.setInteger(panel, "columns", 1);
		uiController.setInteger(panel, "weightx", 1);
		uiController.setInteger(panel, "gap", 10);
		uiController.setInteger(panel, "weighty", 1);
		uiController.setChoice(panel, "halign", "fill");
		uiController.add(panel, getPersonInfoPanel(p,handler));
		uiController.add(panel,getPersonInfoPanel(p.getChw(),handler));
		for(MedicFieldResponse response: fieldResponseDao.getDetailViewFieldResponsesForPerson(p)){
			String value = "";
			DataType type = response.getField().getDatatype();
			if(type.isBoolean()){
				value = (response.getValue().equals("true")) ? type.getTrueLabel() :type.getFalseLabel();
			}else if(type == DataType.TEXT_AREA){
				Object textArea = Thinlet.create("textarea");
				uiController.setInteger(textArea, "weightx",1);
				Object pr = Thinlet.create("panel");
				uiController.setInteger(pr,"columns",1);
				uiController.setEditable(textArea,false);
				uiController.setText(textArea, response.getValue());
				Object label = uiController.createLabel(response.getField().getLabel());
				uiController.add(pr,label);
				uiController.add(pr,textArea);
				uiController.add(panel,pr);
			}else{
				value = response.getValue();
			}
			if(type != DataType.TEXT_AREA){
				Object item = uiController.createLabel(response.getField().getLabel() +": "+ value);
				uiController.add(panel,item);
			}
		}
		Object buttonPanel = Thinlet.create("panel");
		uiController.setInteger(buttonPanel,"weightx",1);
		uiController.setInteger(buttonPanel,"weighty",1);
		uiController.setChoice(buttonPanel,"valign","bottom");
		uiController.setChoice(buttonPanel,"halign","left");
		
		Object button = uiController.createButton(InternationalisationUtils.getI18NString(GO_BACK_BUTTON));
		uiController.setInteger(button, "colspan", 1);
		uiController.setInteger(button, "weightx", 1);
		uiController.setChoice(button, "halign", "center");
		uiController.setAction(button, "goBack()", null, handler);
		uiController.setIcon(button, "/icons/arrow_turn_left.png");
		uiController.add(buttonPanel,button);
		uiController.add(panel,buttonPanel);
		return panel;
	}
	
	//FOR EXPANDED VIEW
	public Object getPersonInfoPanel(Person p, ThinletUiEventHandler handler){
		Object personPanel = uiController.loadComponentFromFile(UI_FILE_PERSON_AAG_PANEL, handler);
		uiController.setAction(uiController.find(personPanel,"imagePanel"),"loadImage()", null, handler);
		Object labelPanel = uiController.find(personPanel,"labelPanel");
		if(p.hasImage()){
			uiController.setIcon(uiController.find(personPanel, "imagePanel"), p.getResizedImage());
		}
		uiController.setText(uiController.find(labelPanel,"label1"), p.getName());
		uiController.setText(uiController.find(labelPanel,"label2"), "ID: "+ p.getPid());
		String gender = p.getGender().toString();
		uiController.setText(uiController.find(labelPanel,"label3"), gender );
		uiController.setText(uiController.find(labelPanel,"label4"), "Age: " + p.getAge());
		if(p instanceof CommunityHealthWorker){
			uiController.setText(uiController.find(labelPanel,"label5"), InternationalisationUtils.getI18NString(PHONE_NUMBER_FIELD)+": " + ((CommunityHealthWorker) p).getContactInfo().getPhoneNumber());
			uiController.setText(personPanel, InternationalisationUtils.getI18NString(CHW_AAG));
		}else{
			uiController.setText(uiController.find(labelPanel,"label5"), InternationalisationUtils.getI18NString(CHW_FIELD) + ": " + ((Patient) p).getChw().getName());
			uiController.setText(personPanel, InternationalisationUtils.getI18NString(PATIENT_AAG));
		}
		uiController.setInteger(personPanel, "weightx", 1);
		uiController.setInteger(personPanel, "colspan", 1);
		return personPanel;
	}
	
	/** Adds all non-critical details to the detail view. These are fields that the user
	 * has created by editing the detail view
	 * 
	 * @param p Person to add details for
	 */
	private void addPersonDetails(Person p){
		for(MedicFieldResponse response: fieldResponseDao.getDetailViewFieldResponsesForPerson(p)){
			String value = "";
			DataType type = response.getField().getDatatype();
			if(type.isBoolean()){
				value = (response.getValue().equals("true")) ? type.getTrueLabel() :type.getFalseLabel();
			}else if(type == DataType.TEXT_AREA){
				Object textArea = Thinlet.create("textarea");
				uiController.setInteger(textArea, "weightx",1);
				Object panel = Thinlet.create("panel");
				uiController.setInteger(panel,"columns",1);
				uiController.setEditable(textArea,false);
				uiController.setText(textArea, response.getValue());
				Object label = uiController.createLabel(response.getField().getLabel());
				uiController.add(panel,label);
				uiController.add(panel,textArea);
				uiController.add(detailPanel,panel);
			}else{
				value = response.getValue();
			}
			if(type != DataType.TEXT_AREA){
				Object item = uiController.createLabel(response.getField().getLabel() +": "+ value);
				uiController.add(detailPanel,item);
			}
		}
	}
	
	
	/**
	 * Returns an edit data button that reflects the current state (i.e. is
	 * the user currently editing a person? Is the person a chw or a patient?)
	 * 
	 * @param entity
	 * @return
	 */
	private Object getEditDataButton(Person entity){
		Object btn;
		if(!inEditingMode){
			if(entity instanceof CommunityHealthWorker){
				 btn = uiController.createButton(InternationalisationUtils.getI18NString(EDIT_CHW_DATA_BUTTON));			
			}else{
				btn = uiController.createButton(InternationalisationUtils.getI18NString(EDIT_PATIENT_DATA_BUTTON));		
			}
			uiController.setIcon(btn, "/icons/user_edit.png");
		}else{
			if(entity instanceof CommunityHealthWorker){
				 btn = uiController.createButton(InternationalisationUtils.getI18NString(SAVE_CHW_DATA_BUTTON));			
			}else{
				btn = uiController.createButton(InternationalisationUtils.getI18NString(SAVE_PATIENT_DATA_BUTTON));		
			}
			uiController.setInteger(btn,"colspan", 2);
			uiController.setIcon(btn, "/icons/disk.png");
		}
		uiController.setChoice(btn, "halign", "left");
		uiController.setChoice(btn, "valign", "bottom");
		uiController.setInteger(btn, "weightx", 1);
		uiController.setInteger(btn, "weighty", 1);
		uiController.setAction(btn, "editDataButtonClicked(this)", null, this);
		
		uiController.setAttachedObject(btn, entity);
		if(UserSessionManager.getUserSessionManager().getCurrentUserRole() == Role.READ){
			uiController.setEnabled(btn, false);
		}
		return btn;
	}
	
	/**
	 * Action method for when the edit data button is clicked
	 * if in editing mode, it adds all the proper editable controls
	 * to the interface. If not in editing mode, it validates and saves responses
	 * to the fields already in the interface
	 * 
	 * @param btn
	 */
	public void editDataButtonClicked(Object btn){
		inEditingMode = !inEditingMode;
		if(inEditingMode){
			currentPersonPanel.switchToEditingPanel();
			uiController.remove(uiController.find(detailPanel, "buttonPanel"));
			Collection<MedicField> fields = fieldDao.getAllPossibleDetailViewFieldsForPerson((Person) currentEntity);
			//a list of the input objects so that when the user clicks save,
			//you can grab the input, validate it, and save it
			dvResponseObjects = new ArrayList<ThinletFormField>();
			for(MedicField ff:fields){
					ThinletFormField tff = null;
					String label = ff.getLabel() +":";
					if(ff.getDatatype() == DataType.CHECK_BOX){ 
						tff = new CheckBox(uiController,label);
					}else if(ff.getDatatype() ==  DataType.DATE_FIELD){
						tff = new DateField(uiController,label);
					}else if(ff.getDatatype() == DataType.NUMBER){
						tff = new NumericTextField(uiController,label);
					}else if(ff.getDatatype() == DataType.PASSWORD_FIELD){
						tff = new PasswordTextField(uiController,label);
					}else if(ff.getDatatype() == DataType.PHONE_NUMBER_FIELD){
						tff = new PhoneNumberField(uiController,label);
					}else if(ff.getDatatype() == DataType.TIME_FIELD){
						tff = new TimeField(uiController,label);
					}else if(ff.getDatatype() == DataType.TEXT_AREA){
						tff = new TextArea(uiController,label);
					}else if(ff.getDatatype() == DataType.TEXT){
						tff = new TextBox(uiController,label);
					}else if(ff.getDatatype() == DataType.POSITIVENEGATIVE){
						tff = new ButtonGroup(uiController,label,InternationalisationUtils.getI18NString(POSITIVE),InternationalisationUtils.getI18NString(NEGATIVE));
					}else if(ff.getDatatype() == DataType.TRUEFALSE){
						tff = new ButtonGroup(uiController,label,InternationalisationUtils.getI18NString(TRUE),InternationalisationUtils.getI18NString(FALSE));
					}else if(ff.getDatatype() == DataType.YESNO){
						tff = new ButtonGroup(uiController,label,InternationalisationUtils.getI18NString(YES),InternationalisationUtils.getI18NString(NO));
					}
					if(tff != null){
						tff.setField(ff);
						dvResponseObjects.add(tff);
						MedicFieldResponse response = fieldResponseDao.getDetailViewFieldResponseForFieldPerson(ff, (Person) currentEntity);
						if(response !=null){
							tff.setResponse(response.getValue());
						}
						uiController.add(detailPanel,tff.getThinletPanel());
						uiController.setInteger(tff.getThinletPanel(), "weightx", 1);
						uiController.setInteger(tff.getThinletPanel(), "colspan", 2);
						uiController.setChoice(tff.getThinletPanel(), "halign", "fill");
					}
			}
			//add the "save" button
			uiController.add(detailPanel, getEditDataButton((Person) currentEntity));
			
		}else{
			currentPersonPanel.stopEditingWithSave();
			validateAndSavePersonFieldResponses();
			if(currentEntity instanceof Patient){
				switchToPatientPanel((Patient) currentEntity);
			}else{
				switchToCHWPanel((CommunityHealthWorker) currentEntity);
			}
			parent.refresh();
		}
	}
	
	/**
	 * Action method that is called when the "Edit View" button is clicked. 
	 * It creates a new DetailViewEditorController, which allows the user to create and
	 * edit the extra detail fields that are displayed for people entities in the 
	 * detail view
	 */
	public void editViewButtonClicked(){
		//DetailViewEditorController dvec = new DetailViewEditorController(uiController, appContext, currentEntity instanceof Patient);
		parent.expandDetailView(new PersonExpandedDetailView(uiController,appContext,(Patient) currentEntity, this).getMainPanel());
	}
	
	/**
	 * Returns a thinlet edit view button
	 * @return
	 */
	private Object getEditViewButton(){
		Object btn = uiController.createButton(InternationalisationUtils.getI18NString(SEE_MORE_BUTTON));
		uiController.setChoice(btn, "halign", "right");
		uiController.setChoice(btn, "valign", "bottom");
		uiController.setInteger(btn, "weightx", 1);
		uiController.setInteger(btn, "weighty", 1);
		uiController.setAction(btn, "editViewButtonClicked()", null, this);
		uiController.setIcon(btn, "/icons/note_go.png");
		if(currentEntity instanceof CommunityHealthWorker){
			uiController.setVisible(btn, false);
		}
//		if(UserSessionManager.getUserSessionManager().getCurrentUserRole() == Role.ADMIN){
//			uiController.setEnabled(btn, true);
//		}else{
//			uiController.setEnabled(btn, false);
//		}
		return btn;
	}
	


	/**
	 * This method goes through all the controls used to edit a person,
	 * both the controls to edit the person's critical data and the controls
	 * used to edit the additional detail data
	 */
	private void validateAndSavePersonFieldResponses(){
		//save the detail view field responses
		if(currentEntity instanceof CommunityHealthWorker){
			try{
				chwDao.updateCommunityHealthWorker((CommunityHealthWorker) currentEntity);
			}catch(Exception e){
				System.out.println("Error updating CHW");
				e.printStackTrace();
			}
		}else if(currentEntity instanceof Patient){
			patientDao.updatePatient((Patient) currentEntity);
		}
		
		//next, check and save the detail view fields
		for(ThinletFormField tff : dvResponseObjects){
			if(tff.hasResponse() && tff.isValid()){
					HistoryManager.logDetailViewChange((Person) currentEntity, tff.getField(), tff.getResponse());
					MedicFieldResponse response = new MedicFieldResponse(tff.getResponse(),tff.getField(),(Person) currentEntity,
							UserSessionManager.getUserSessionManager().getCurrentUser());
					fieldResponseDao.saveMedicFieldResponse(response);
			}
		}
		parent.refresh();
	}
	
	private void switchToFormPanel(MedicForm f){
		//clear the panel
		uiController.removeAll(detailPanel);
		//add the proper panel
		uiController.add(detailPanel,formPanel);
		uiController.setText(uiController.find(formPanel,"nameLabel"), f.getName());
		Object fieldContainer = uiController.find(formPanel,"formPanel");
		uiController.removeAll(fieldContainer);
		for(MedicFormField ff: f.getFields()){
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
	private void switchToResponseInfoPanel(Response entity) {
		if(entity instanceof MedicFormResponse){
			switchToFormResponsePanel((MedicFormResponse) entity);
		}else if(entity instanceof MedicMessageResponse){
			switchToMessagePanel((MedicMessageResponse) entity);
		}else if(entity instanceof MedicFieldResponse){
			//switchToFieldResponsePanel(entity);
		}
		
	}
	
	public Object switchToFormResponsePanel(MedicFormResponse ri, ThinletUiEventHandler handler){
		//clear the panel
		Object results = Thinlet.create("panel");
		String form = InternationalisationUtils.getI18NString(FORM) + ": " + ri.getForm().getName();
		String submitter = InternationalisationUtils.getI18NString(SUBMITTER) + ": " + ri.getSubmitter().getName();
		String subject = InternationalisationUtils.getI18NString(SUBJECT) + ": " + ri.getSubject().getName();
		String date = InternationalisationUtils.getI18NString(DATE_SUBMITTED) + " " + ri.getDateSubmitted().toLocaleString();
		//add the proper panel
		Object fPanel =uiController.loadComponentFromFile(UI_FILE_FORM_AAG, handler);
		uiController.add(results,fPanel);
		uiController.setString(uiController.find(fPanel,"nameLabel"), "text", form);
		uiController.setString(uiController.find(fPanel,"submitterLabel"), "text", submitter);
		uiController.setString(uiController.find(fPanel,"dateSubmittedLabel"), "text", date);
		uiController.setString(uiController.find(fPanel,"subjectLabel"), "text", subject);
		Object fieldContainer = uiController.find(fPanel,"formPanel");
		uiController.removeAll(fieldContainer);
		ArrayList<String> responses = new ArrayList<String>();
		for(MedicFieldResponse r: ri.getResponses()){
			responses.add(r.getValue());
		}
		Iterator<String> responseIt = responses.iterator();
		for(MedicFormField ff: ri.getForm().getFields()){
			Object field = null;
			if(ff.getDatatype() == DataType.CHECK_BOX){
				field =uiController.createCheckbox(null, ff.getLabel(), false);
				uiController.add(fieldContainer,field);
				uiController.setEnabled(field, false);
				uiController.setInteger(field, "weightx", 1);
				uiController.setChoice(field, "halign", "fill");
				String response = responseIt.next();
				if(response.equals("true")){
					uiController.setSelected(field, true);
				}
			}else if(ff.getDatatype() == DataType.TEXT_AREA){
				field = Thinlet.create("textarea");
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
		uiController.setInteger(results, "weightx", 1);
		uiController.setInteger(results, "weighty", 1);
		return results;
	}
	
	public Object switchToFormResponsePanel(MedicFormResponse ri){
		//clear the panel
		uiController.removeAll(detailPanel);
		String form = InternationalisationUtils.getI18NString(FORM) + ": " + ri.getForm().getName();
		String submitter = InternationalisationUtils.getI18NString(SUBMITTER) + ": " + ri.getSubmitter().getName();
		String subject = InternationalisationUtils.getI18NString(SUBJECT) + ": " + ri.getSubject().getName();
		String date = InternationalisationUtils.getI18NString(DATE_SUBMITTED) + " " + ri.getDateSubmitted().toLocaleString();
		//add the proper panel
		uiController.add(detailPanel,formPanel);
		uiController.setString(uiController.find(formPanel,"nameLabel"), "text", form);
		uiController.setString(uiController.find(formPanel,"submitterLabel"), "text", submitter);
		uiController.setString(uiController.find(formPanel,"dateSubmittedLabel"), "text", date);
		uiController.setString(uiController.find(formPanel,"subjectLabel"), "text", subject);
		Object fieldContainer = uiController.find(formPanel,"formPanel");
		uiController.removeAll(fieldContainer);
		ArrayList<String> responses = new ArrayList<String>();
		for(MedicFieldResponse r: ri.getResponses()){
			responses.add(r.getValue());
		}
		Iterator<String> responseIt = responses.iterator();
		for(MedicFormField ff: ri.getForm().getFields()){
			Object field = null;
			if(ff.getDatatype() == DataType.CHECK_BOX){
				field =uiController.createCheckbox(null, ff.getLabel(), false);
				uiController.add(fieldContainer,field);
				uiController.setEnabled(field, false);
				uiController.setInteger(field, "weightx", 1);
				uiController.setChoice(field, "halign", "fill");
				String response = responseIt.next();
				if(response.equals("true")){
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
		return detailPanel;
	}
	
	public void switchToMessagePanel(MedicMessageResponse ri){
		uiController.removeAll(detailPanel);
		Object submitterLabel = uiController.createLabel(InternationalisationUtils.getI18NString(SUBMITTED_BY)+" "+ ri.getSubmitter().getName());
		Object dateLabel = uiController.createLabel(InternationalisationUtils.getI18NString(ON)+" " + ri.getDateSubmitted().toLocaleString());
		Object textarea = uiController.create("textarea");
		uiController.setText(textarea, ri.getMessageContent());
		uiController.setEditable (textarea,false);
		uiController.setInteger(textarea, "weightx", 1);
		uiController.setInteger(submitterLabel,"weightx",1);
		uiController.setInteger(dateLabel,"weightx",1);
		uiController.setChoice(submitterLabel,"halign","center");
		uiController.setChoice(dateLabel,"halign","center");
		uiController.add(detailPanel,submitterLabel);
		uiController.add(detailPanel,dateLabel);
		uiController.add(detailPanel,textarea);
	}
	
	//Further options methods
	private void updateFurtherOptions(){
		String[][] buttons= furtherOptions.get(currentEntity.getClass());
		uiController.removeAll(furtherOptionsPanel);
		if(buttons ==null || buttons.length ==0)
			return;
		
		for(int i=0; i<buttons.length;i++){
			Object button = uiController.createButton(buttons[i][0]);
			uiController.setAction(button, buttons[i][1], null, this);
			uiController.setInteger(button, "weightx", 1);
			uiController.setInteger(button, "weighty", 1);
			uiController.setChoice(button,"halign", "center");
			uiController.setChoice(button,"valign", "center");
			if(buttons[i][0].compareTo(InternationalisationUtils.getI18NString(SUBMIT_FORM_PATIENT)) == 0){
				if(UserSessionManager.getUserSessionManager().getCurrentUserRole() == Role.READ){
					uiController.setEnabled(button, false);
				}
			}
				
			uiController.add(furtherOptionsPanel,button);
		}
	}
	
	public void submitFormForPatient(){
		SubmitFormDialog sfDialog= new SubmitFormDialog(uiController,appContext,null,(Patient) currentEntity);
	}
	
	public void fillOutForm(){
		SubmitFormDialog sfDialog= new SubmitFormDialog(uiController,appContext,(MedicForm) currentEntity,null);
	}
	
	public void createPatient(){
		AdminTab tab = new AdminTab(uiController,appContext);
		uiController.add(uiController.getParent(parent.getTab()),tab.getMainPanel());
	}
	
	
	public void createCHW(){
			
	}
	
	public void deletePatient(){
		DetailViewEditorController dvec = new DetailViewEditorController(uiController, appContext, currentEntity instanceof Patient);

	}
	
	public void deleteCHW(){
		DetailViewEditorController dvec = new DetailViewEditorController(uiController, appContext, currentEntity instanceof CommunityHealthWorker);
	}
	

}