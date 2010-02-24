package net.frontlinesms.plugins.patientview.ui.administration.people;

import java.util.ArrayList;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.repository.CommunityHealthWorkerDao;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class CommunityHealthWorkerAdministrationPanelController extends PersonAdministrationPanelController<CommunityHealthWorker> {

	CommunityHealthWorkerDao chwDao;
	
	public CommunityHealthWorkerAdministrationPanelController(UiGeneratorController uiController, ApplicationContext appCon) {
		super(uiController, appCon);
	}

	@Override
	protected List<CommunityHealthWorker> getPeopleForString(String s) {
		if(chwDao == null){
			chwDao = (CommunityHealthWorkerDao) appCon.getBean("CHWDao");
		}
		return new ArrayList<CommunityHealthWorker>(chwDao.getCommunityHealthWorkerByName(s, 30));
	}

	@Override
	protected String getPersonType() {
		return "CHW";
	}

	@Override
	protected void putHeader() {
		advancedTableController.putHeader(CommunityHealthWorker.class, new String[]{"Name","Age","Phone Number"}, new String[]{"getName", "getStringAge","getPhoneNumber"});		
	}

	public String getListItemTitle() {
		return "Manage CHWs";
	}

}
