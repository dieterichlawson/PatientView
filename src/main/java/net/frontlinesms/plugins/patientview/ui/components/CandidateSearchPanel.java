package net.frontlinesms.plugins.patientview.ui.components;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import java.util.ArrayList;
import java.util.List;

import net.frontlinesms.plugins.patientview.analysis.Candidate;
import net.frontlinesms.plugins.patientview.analysis.FormMatcher;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormResponseDao;
import net.frontlinesms.plugins.patientview.data.repository.PatientDao;
import net.frontlinesms.plugins.patientview.ui.AdvancedTableActionDelegate;
import net.frontlinesms.plugins.patientview.ui.AdvancedTableController;
import net.frontlinesms.plugins.patientview.ui.personpanel.PatientPanel;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;

public class CandidateSearchPanel implements ThinletUiEventHandler, AdvancedTableActionDelegate{

	private UiGeneratorController uiController;
	private ApplicationContext appCon;
	
	private PatientDao patientDao;
	private MedicFormResponseDao responseDao;
	
	private AdvancedTableController tableController;
	
	private boolean searchingCandidates;
	private List<Candidate> candidates;
	
	private Object mainPanel;
	private Object personPanel;
	
	private Patient currentlySelectedPatient;
	private MedicFormResponse response;
	
	private FormMatcher matcher;
	
	private static final String UI_FILE= "/ui/plugins/patientview/candidateSearchPanel.xml";
	
	public CandidateSearchPanel(UiGeneratorController uiController, ApplicationContext appCon, MedicFormResponse response){
		this.uiController = uiController;
		this.patientDao = (PatientDao) appCon.getBean("PatientDao");
		responseDao = (MedicFormResponseDao) appCon.getBean("MedicFormResponseDao");
		matcher = new FormMatcher(appCon);
		this.response = response;
		this.appCon = appCon;
		init();
	}
	
	public void init(){
		mainPanel = uiController.loadComponentFromFile(UI_FILE, this);
		uiController.setText(uiController.find(mainPanel,"titleLabel"), response.isMapped()? "Change the Response Mapping": "Map this Response");
		uiController.setText(uiController.find(mainPanel,"changeButton"), response.isMapped() ? "Change Mapping to this Person": "Set Mapping to this Person");
		personPanel = uiController.find(mainPanel,"personPanel");
		tableController = new AdvancedTableController(this, uiController, uiController.find(mainPanel,"resultsTable"));
		tableController.putHeader(Patient.class, new String[]{getI18NString("medic.common.labels.name"),getI18NString("medic.common.labels.age"),getI18NString("medic.common.chw")}, new String[]{"getName", "getStringAge", "getCHWName"});
		tableController.putHeader(Candidate.class, new String[]{getI18NString("medic.common.labels.name"),getI18NString("medic.common.chw"),"confidence"}, new String[]{"getName", "getCHWName","getConfidence"});
		searchingCandidates = true;
		search("");
		tableController.setSelected(0);
	}
	public void search(String text){
		if(!searchingCandidates){
			tableController.setResults(patientDao.getPatientsByName(text));
		}else{
			candidates = matcher.getCandidatesForResponse(response);
			ArrayList<Candidate> results = new ArrayList<Candidate>();
			for(Candidate c: candidates){
				if(c.getName().contains(text)){
					results.add(c);
				}
			}
			tableController.setResults(results);
		}
	}
	
	public void searchAll(){
		searchingCandidates=false;
		search("");
	}
	
	public void searchCandidates(){
		searchingCandidates=true;
		search("");
	}
	
	public void selectionChanged(Object selectedObject) {
		uiController.removeAll(personPanel);
		if(selectedObject instanceof Candidate){
			uiController.add(personPanel,new PatientPanel(uiController,appCon,((Candidate) selectedObject).getPatient()).getMainPanel());
			currentlySelectedPatient = ((Candidate) selectedObject).getPatient();
		}else{
			uiController.add(personPanel,new PatientPanel(uiController,appCon,((Patient) selectedObject)).getMainPanel());
			currentlySelectedPatient = ((Patient) selectedObject);	
		}
	}
	
	public void doubleClickAction(Object selectedObject) {/*do nothing*/}
	public void resultsChanged() {/*do nothing*/}
	
	public Object getMainPanel(){
		return mainPanel;
	}
	
	public void changeSubject(){
		response.setSubject(currentlySelectedPatient);
		((SessionFactory) appCon.getBean("sessionFactory")).getCurrentSession().merge(response);
		responseDao.updateMedicFormResponse(response);
	}
}
