package net.frontlinesms.plugins.patientview.ui.dashboard;

import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.ui.dashboard.tabs.FormResponseTab;
import net.frontlinesms.plugins.patientview.ui.dashboard.tabs.SmsMessagesTab;
import net.frontlinesms.plugins.patientview.ui.personpanel.CommunityHealthWorkerPanel;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class CommunityHealthWorkerDashboard extends PersonDashboard<CommunityHealthWorker>{

	public CommunityHealthWorkerDashboard(UiGeneratorController uiController, ApplicationContext appCon, CommunityHealthWorker p) {
		super(uiController, appCon, p);
	}

	@Override
	protected void init() {
		uiController.add(leftPanel,new CommunityHealthWorkerPanel(uiController,appCon,person).getMainPanel());
		tabs.add(new FormResponseTab(uiController,appCon,person));
		tabs.add(new SmsMessagesTab(uiController,appCon,person));
	}

}
