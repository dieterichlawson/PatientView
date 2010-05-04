package net.frontlinesms.plugins.patientview.ui.dashboard;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.ui.personpanel.CommunityHealthWorkerPanel;
import net.frontlinesms.plugins.patientview.ui.personpanel.PatientPanel;
import net.frontlinesms.plugins.patientview.ui.personpanel.PersonAttributePanel;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class PatientDashboard extends PersonDashboard<Patient> {
	
	public PatientDashboard(UiGeneratorController uiController, ApplicationContext appCon, Patient p) {
		super(uiController, appCon, p);
	}

	@Override
	protected void init() {
		uiController.add(leftPanel,new PatientPanel(uiController,appCon,person).getMainPanel());
		uiController.add(leftPanel,new CommunityHealthWorkerPanel(uiController,appCon,person.getChw()).getMainPanel());
		uiController.add(leftPanel,new PersonAttributePanel(uiController,appCon,person).getMainPanel());
		tabs.add(new FormResponseTab(uiController,appCon,person));
		tabs.add(new SubmitFormTab(uiController,appCon,person));
	}

}
