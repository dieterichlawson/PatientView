package net.frontlinesms.plugins.patientview.responsemapping.ui;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import net.frontlinesms.plugins.patientview.PatientViewPluginController;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormFieldResponseDao;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormResponseDao;
import net.frontlinesms.plugins.patientview.data.repository.PatientDao;
import net.frontlinesms.plugins.patientview.responsemapping.Candidate;
import net.frontlinesms.plugins.patientview.responsemapping.IncomingFormMatcher;
import net.frontlinesms.plugins.patientview.ui.advancedtable.AdvancedTableActionDelegate;
import net.frontlinesms.plugins.patientview.ui.advancedtable.AdvancedTableController;
import net.frontlinesms.plugins.patientview.ui.advancedtable.HeaderColumn;
import net.frontlinesms.plugins.patientview.ui.personpanel.PatientPanel;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

/**
 * This is the panel in the form mapping screen that allows you to search through
 * candidates and patients to map forms to
 */
public class CandidateSearchPanel implements ThinletUiEventHandler, AdvancedTableActionDelegate{

	private UiGeneratorController uiController;
	private ApplicationContext appCon;
	
	private PatientDao patientDao;
	private MedicFormResponseDao formResponseDao;
	private MedicFormFieldResponseDao fieldResponseDao;
	
	private boolean searching;
	private boolean searchingCandidates;
	
	/** the list of candidates for this form response**/
	private List<Candidate> candidates;
	
	/** the table controller for viewing candidates and patients**/
	private AdvancedTableController tableController;
	/** the object that matches forms to patients**/
	private IncomingFormMatcher matcher;
	/** the form response for this panel**/
	private MedicFormResponse response;
	/** the currently selected (by the user) patient**/
	private Patient currentlySelectedPatient;
	
	private FormResponseMappingPanelController parentController;
	
	//thinlet objects
	private Object mainPanel;
	
	private static final String UI_FILE= "/ui/plugins/patientview/administration/responsemapping/candidateSearchPanel.xml";
	private static final String CHANGE_MAPPING_PANEL_UI_FILE ="/ui/plugins/patientview/administration/responsemapping/changeMappingPanel.xml";
	
	public CandidateSearchPanel(UiGeneratorController uiController, ApplicationContext appCon, MedicFormResponse response, FormResponseMappingPanelController parentController){
		this.uiController = uiController;
		this.parentController = parentController;
		this.patientDao = (PatientDao) appCon.getBean("PatientDao");
		this.response = response;
		this.appCon = appCon;
		formResponseDao = (MedicFormResponseDao) appCon.getBean("MedicFormResponseDao");
		fieldResponseDao = (MedicFormFieldResponseDao) appCon.getBean("MedicFormFieldResponseDao");
		matcher = PatientViewPluginController.getFormMatcher();
		mainPanel = uiController.createPanel("");
		uiController.setWeight(mainPanel,1,1);
		candidates = matcher.getCandidatesForResponse(response);
		init();
	}
	
	public void init(){
		searching=false;
		searchingCandidates = true;
		switchModes(false);
		//initialize the advanced table controller
	}
	
	private void switchModes(boolean searching){
		this.searching = searching;
		uiController.removeAll(mainPanel);
		//if we are not supposed to be searching through people
		if(!searching){
			//load the ui file
			Object panel = uiController.loadComponentFromFile(CHANGE_MAPPING_PANEL_UI_FILE, this);
			//if we aren't searching for more candidates and the response is mapped
			//then we should show the person that the response was mapped to
			if(response.isMapped()){
				//set the labels & icons for the whole panel
				uiController.setText(panel, getI18NString("medic.common.labels.mapped.to"));
				uiController.setIcon(panel,"/icons/user.png");
				//create the patient panel for the person that the response was mapped to
				PatientPanel pPanel = new PatientPanel(uiController,appCon,(Patient) response.getSubject());
				pPanel.setPanelTitle("");
				uiController.add(uiController.find(panel,"pPanel"),pPanel.getMainPanel());
				//set the confidence
				DecimalFormat myFormatter = new DecimalFormat("###.#%");
				uiController.setText(uiController.find(panel,"confidenceLabel"),getI18NString("medic.common.confidence")+ ": " + myFormatter.format(matcher.getConfidence((Patient) response.getSubject(), response)/100F));
				uiController.setColspan(uiController.find(panel,"confidenceLabel"),2);
				//make the 'map to this person' button invisible
				uiController.setVisible(uiController.find(panel,"mapButton"), false);
				//set the text & iconfor the 'change mapping' button
				uiController.setText(uiController.find(panel,"changeButton"),getI18NString("medic.candidate.search.panel.change.response.mapping"));
				uiController.setIcon(uiController.find(panel,"changeButton"),"/icons/change_mapping_small.png");
			}else {  //if we aren't searching for candidates and the response is not mapped
				//set the title and icons for the whole panel (top candidate - halo person)
				uiController.setText(panel, getI18NString("medic.candidate.search.panel.top.candidate"));
				uiController.setIcon(panel,"/icons/candidate.png");
				//initialize the person panel for the top candidate
				Candidate topCandidate = candidates.get(0);
				PatientPanel pPanel = new PatientPanel(uiController,appCon,topCandidate.getPatient());
				pPanel.setPanelTitle("");
				uiController.add(uiController.find(panel,"pPanel"),pPanel.getMainPanel());
				uiController.setAttachedObject(uiController.find(panel,"mapButton"), topCandidate.getPatient());
				//set the confidence label
				uiController.setText(uiController.find(panel,"confidenceLabel"),getI18NString("medic.common.confidence")+ ": " + topCandidate.getConfidence());
				this.currentlySelectedPatient = topCandidate.getPatient();
			}
			uiController.add(mainPanel,panel);
		}else{ //if we are searching for candidates
			//load the ui file
			
			Object panel = uiController.loadComponentFromFile(UI_FILE, this);
			//create the advanced table controller
			tableController = new AdvancedTableController(this, uiController, uiController.find(panel,"resultsTable"));
			//if searching patients, we want name, id and chw
			tableController.putHeader(Patient.class, HeaderColumn.createColumnList(new String[]{getI18NString("medic.common.labels.name"), getI18NString("medic.common.labels.id"), getI18NString("medic.common.chw")},
					 new String[]{"/icons/user.png", "/icons/key.png","/icons/user_phone.png"},
					 new String[]{"getName", "getStringID","getCHWName"}));
			//if searching Candidates, we want name, id , chw, and confidence
			tableController.putHeader(Candidate.class, HeaderColumn.createColumnList(new String[]{getI18NString("medic.common.labels.name"), getI18NString("medic.common.labels.id"), getI18NString("medic.common.chw"),getI18NString("medic.common.confidence")},
					 new String[]{"/icons/user.png", "/icons/key.png","/icons/user_phone.png","/icons/thumb_up.png"},
					 new String[]{"getName","getStringID","getCHWName","getConfidence"}));
			//if we are searching and the response is mapped, the title should be 'Change Response Mapping'
			if(response.isMapped()){
				uiController.setText(panel,getI18NString("medic.candidate.search.panel.change.response.mapping"));
				uiController.setIcon(panel, "/icons/change_mapping_small.png");
				uiController.setText(uiController.find(panel,"changeButton"), getI18NString("medic.candidate.search.panel.change.mapping.to.person"));
			}else{ // if the response is not mapped, the title should be 'more candidates'
				uiController.setText(panel,getI18NString("medic.candidate.search.panel.more.candidates"));
				uiController.setIcon(panel, "/icons/users.png");
				uiController.setText(uiController.find(panel,"changeButton"), getI18NString("medic.candidate.search.panel.set.mapping.to.person"));
			}
			uiController.add(mainPanel,panel);
			search("");
			tableController.setSelected(0);
		}
	}
	
	/**
	 * Called when a key is pressed in the search bar.
	 * Should be called only when searching=true
	 */
	public void search(String text){
		//if we aren't in search mode, then we shouldn't be here
		if(!searching){
			return;
		}
		if(!searchingCandidates){
			//if we aren't searching for candidates, then we just want a few patients
			tableController.setResults(patientDao.findPatientsByName(text,5));
		}else{
			//if we are searching for candidates, filter by name
			ArrayList<Candidate> results = new ArrayList<Candidate>();
			for(Candidate c: candidates){
				if(c.getName().toLowerCase().contains(text.toLowerCase())){
					results.add(c);
				}
			}
			tableController.setResults(results);
		}
		tableController.setSelected(0);
	}
	
	/**
	 * sets the mode to searching all patients
	 */
	public void searchAll(){
		searchingCandidates=false;
		search("");
	}
	
	/**
	 * sets the mode to searching candidates only
	 */
	public void searchCandidates(){
		searchingCandidates=true;
		search("");
	}
	
	/**
	 * Called when the selection in the search results table changes
	 */
	public void selectionChanged(Object selectedObject) {
		if(!searching){
			return;
		}
		uiController.removeAll(uiController.find(mainPanel,"personPanel"));
		if(selectedObject instanceof Candidate){
			PatientPanel p =new PatientPanel(uiController,appCon,((Candidate) selectedObject).getPatient());
			p.setPanelTitle("");
			uiController.add(uiController.find(mainPanel,"personPanel"),p.getMainPanel());
			currentlySelectedPatient = ((Candidate) selectedObject).getPatient();
		}else{
			PatientPanel p = new PatientPanel(uiController,appCon,((Patient) selectedObject));
			p.setPanelTitle("");
			uiController.add(uiController.find(mainPanel,"personPanel"),p.getMainPanel());
			currentlySelectedPatient = ((Patient) selectedObject);	
		}
	}
	
	public void doubleClickAction(Object selectedObject) {/*do nothing*/}
	public void resultsChanged() {/*do nothing*/}
	
	public Object getMainPanel(){
		return mainPanel;
	}
	
	/**
	 * 
	 */
	public void setSubjectToSelectedCandidate(){
		response.setSubject(currentlySelectedPatient);
		formResponseDao.updateMedicFormResponse(response);
		fieldResponseDao.updateSubjects(response);
		parentController.currentResponseMappingChanged();
	}
	
	public void expandCandidateSearchPanel(){
		switchModes(true);
	}
}
