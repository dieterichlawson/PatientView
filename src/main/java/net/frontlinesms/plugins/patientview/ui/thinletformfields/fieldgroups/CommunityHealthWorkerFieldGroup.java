package net.frontlinesms.plugins.patientview.ui.thinletformfields.fieldgroups;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.repository.CommunityHealthWorkerDao;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.FormFieldDelegate;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.personalformfields.PhoneNumberField;
import net.frontlinesms.ui.UiGeneratorController;

public class CommunityHealthWorkerFieldGroup extends PersonFieldGroup<CommunityHealthWorker> {

	private CommunityHealthWorkerDao chwDao;

	public CommunityHealthWorkerFieldGroup(UiGeneratorController ui, ApplicationContext appCon, FormFieldDelegate delegate, CommunityHealthWorker person) {
		super(ui, appCon, delegate, person);
		this.chwDao = (CommunityHealthWorkerDao) appCon.getBean("CHWDao");
	}

	@Override
	protected void addAdditionalFields() {
		PhoneNumberField phoneNumber = new PhoneNumberField(ui,isNewPersonGroup? "":person.getPhoneNumber(),null, appCon);
		super.addField(phoneNumber);
	}

	@Override
	protected void saveOrUpdatePerson() {
		if(isNewPersonGroup){
			chwDao.saveCommunityHealthWorker(person);
		}else{
			chwDao.updateCommunityHealthWorker(person);
		}
	}

	@Override
	protected CommunityHealthWorker createNewPerson() {
		return new CommunityHealthWorker();
	}

}
