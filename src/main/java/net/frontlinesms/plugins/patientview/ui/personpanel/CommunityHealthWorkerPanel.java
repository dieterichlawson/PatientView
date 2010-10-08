package net.frontlinesms.plugins.patientview.ui.personpanel;

import java.util.HashSet;
import java.util.Set;

import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.fieldgroups.CommunityHealthWorkerFieldGroup;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.fieldgroups.PersonFieldGroup;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.springframework.context.ApplicationContext;

public class CommunityHealthWorkerPanel extends PersonPanel<CommunityHealthWorker> {

	private static final String CHW_AAG ="personpanel.labels.chw.at.a.glance";
	private static final String EDIT_CHW_DATA = "personpanel.labels.edit.chw";
	private static final String ADD_CHW = "personpanel.labels.add.a.chw";
	private static final String PHONE_NUMBER_FIELD = "medic.common.labels.phone.number";
	private static final String DEMO_PHONE_NUMBER = "editdetailview.demo.phone.number";
		
	public CommunityHealthWorkerPanel(UiGeneratorController uiController, ApplicationContext appCon,CommunityHealthWorker p) {
		super(uiController, appCon,p);
	}
	
	public CommunityHealthWorkerPanel(UiGeneratorController uiController, ApplicationContext appCon) {
		super(uiController,appCon);
	}

	@Override
	protected void addAdditionalFields() {
		Object panel = uiController.createPanel("");
		uiController.setGap(panel, 5);
		uiController.add(panel,uiController.createLabel(InternationalisationUtils.getI18NString("medic.common.labels.phone.number")+":"));
		Object button = uiController.createButton(getPerson().getPhoneNumber());
		uiController.setChoice(button, "type", "link");
		uiController.setAction(button, "showSendMessageDialog", null, this);
		uiController.add(panel,button);
		uiController.add(super.getLabelPanel(),panel);
	}

	@Override
	protected CommunityHealthWorker createPerson() {
		return new CommunityHealthWorker();
	}

	@Override
	protected String getDefaultTitle() {
		return InternationalisationUtils.getI18NString(CHW_AAG);
	}

	@Override
	protected String getEditingTitle() {
		return InternationalisationUtils.getI18NString(EDIT_CHW_DATA);
	}

	@Override
	protected String getAddingTitle() {
		return InternationalisationUtils.getI18NString(ADD_CHW);
	}

	@Override
	protected void addAdditionalDemoFields() {
		addLabelToLabelPanel(InternationalisationUtils.getI18NString(PHONE_NUMBER_FIELD)+": " + InternationalisationUtils.getI18NString(DEMO_PHONE_NUMBER));
	}

	public void showSendMessageDialog(){
		Set<Object> number= new HashSet<Object>();
		number.add(getPerson().getPhoneNumber());
		uiController.show_composeMessageForm(number);
	}

	@Override
	protected PersonFieldGroup<CommunityHealthWorker> getEditableFields() {
		return new CommunityHealthWorkerFieldGroup(uiController, appCon, null, getPerson());
	}
}
