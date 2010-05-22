package net.frontlinesms.plugins.patientview.ui.administration;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import java.util.ArrayList;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.repository.CommunityHealthWorkerDao;
import net.frontlinesms.plugins.patientview.ui.personpanel.CommunityHealthWorkerPanel;
import net.frontlinesms.plugins.patientview.ui.personpanel.PersonPanel;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class CommunityHealthWorkerAdministrationPanelController extends PersonAdministrationPanelController<CommunityHealthWorker> {

	CommunityHealthWorkerDao chwDao;
	
	private static final String ADD_ICON = "/icons/big_user_add.png";
	private static final String EDIT_ICON = "/icons/big_user_edit.png";
	private static final String DELETE_ICON = "/icons/big_user_delete.png";
	
	public CommunityHealthWorkerAdministrationPanelController(UiGeneratorController uiController, ApplicationContext appCon) {
		super(uiController, appCon);
		chwDao = (CommunityHealthWorkerDao) appCon.getBean("CHWDao");
		search("");
	}

	@Override
	protected List<CommunityHealthWorker> getPeopleForString(String s) {
		return new ArrayList<CommunityHealthWorker>(chwDao.getCommunityHealthWorkerByName(s, 30));
	}

	@Override
	protected String getPersonType() {
		return getI18NString("medic.common.chw");
	}

	@Override
	protected void putHeader() {
		advancedTableController.putHeader(CommunityHealthWorker.class, new String[]{getI18NString("medic.common.labels.name"),getI18NString("medic.common.labels.age"),getI18NString("medic.common.labels.phone.number")}, new String[]{"getName", "getStringAge","getPhoneNumber"});		
	}

	public String getListItemTitle() {
		return getI18NString("admin.actionlist.manage.chws");
	}

	@Override
	protected PersonPanel getPersonPanelForPerson(Person person) {
		return new CommunityHealthWorkerPanel(uiController,appCon,(CommunityHealthWorker) person);
	}

	public String getIconPath() {
		return "/icons/big_users.png";
	}

	@Override
	protected String[] getIcons() {
		String[] icons = new String[3];
		icons[ADD_INDEX] = ADD_ICON;
		icons[EDIT_INDEX] = EDIT_ICON;
		icons[REMOVE_INDEX] = DELETE_ICON;
		return icons;
	}

}
